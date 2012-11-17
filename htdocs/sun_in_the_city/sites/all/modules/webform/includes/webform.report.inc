<?php

/**
 
Drupal Custom Module for Sun_In_The_City, Cornell Daily Tech Newspaper
Author: Sean Salmon
 */

// All functions within this file need the webform.submissions.inc.
module_load_include('inc', 'webform', 'includes/webform.submissions');

/**
 * Retrieve lists of submissions for a given webform.
 */
function webform_results_submissions($node, $user_filter, $pager_count) {
  global $user;

  if (isset($_GET['results']) && is_numeric($_GET['results'])) {
    $pager_count = $_GET['results'];
  }

  $header = theme('webform_results_submissions_header', array('node' => $node));
  if ($user_filter) {
    if ($user->uid) {
      drupal_set_title(t('Submissions for %user', array('%user' => $user->name)), PASS_THROUGH);
    }
    else {
      drupal_set_title(t('Your submissions'));
      webform_disable_page_cache();
    }
    webform_set_breadcrumb($node);
    $submissions = webform_get_submissions(array('nid' => $node->nid, 'uid' => $user->uid), $header, $pager_count);
    $count = webform_get_submission_count($node->nid, $user->uid);
  }
  else {
    $submissions = webform_get_submissions($node->nid, $header, $pager_count);
    $count = webform_get_submission_count($node->nid);
  }

  $operation_column = end($header);

  $rows = array();
  foreach ($submissions as $sid => $submission) {
    $row = array(
      $submission->is_draft ? t('@sid (draft)', array('@sid' => $sid)) : $sid,
      format_date($submission->submitted, 'short'),
    );
    if (webform_results_access($node, $user)) {
      $row[] = theme('username', array('account' => $submission));
      $row[] = $submission->remote_addr;
    }
	
	/** 
	Delete, edit and view options
	*/
    $row[] = l(t('View'), "node/$node->nid/submission/$sid");
    $operation_count = 1;
    // No need to call this multiple times, just reference this in a variable.
    $destination = drupal_get_destination();
    if (webform_submission_access($node, $submission, 'edit', $user)) {
      $row[] = l(t('Edit'), "node/$node->nid/submission/$sid/edit", array('query' => $destination));
      $operation_count++;
    }
    if (webform_submission_access($node, $submission, 'delete', $user)) {
      $row[] = l(t('Delete'), "node/$node->nid/submission/$sid/delete", array('query' => $destination));
      $operation_count++;
    }
    if ($operation_count < $operation_total) {
      $row[count($row) - 1] = array('data' => $row[count($row) - 1], 'colspan' => $operation_total - $operation_count + 1);
    }
    $rows[] = $row;
  }

  $element['#theme'] = 'webform_results_submissions';
  $element['#node'] = $node;
  $element['#submissions'] = $submissions;
  $element['#total_count'] = $count;
  $element['#pager_count'] = $pager_count;
  $element['#attached']['library'][] = array('webform', 'admin');

  $element['table']['#theme'] = 'table';
  $element['table']['#header'] = $header;
  $element['table']['#rows'] = $rows;
 
  return drupal_render($element);

}

/**
 * Theme the header of the submissions table.
 *
 * This is done in it's own function so that webform can retrieve the header and
 * use it for sorting the results.
 */
function theme_webform_results_submissions_header($variables) {
  $node = $variables['node'];

  $columns = array(
    array('data' => t('Type'), 'field' => 'sid', 'sort' => 'desc'),
    array('data' => t('Name'), 'field' => 'submitted'),
  );
  if (webform_results_access($node)) {
    $columns[] = array('data' => t('Url'), 'field' => 'name');
    $columns[] = array('data' => t('LogoURL'), 'field' => 'remote_addr');
  }


  return $columns;
}


/**
 * Create a table containing all submitted values for data sources
 */
function webform_results_table($node, $pager_count = 0) {
  if (isset($_GET['results']) && is_numeric($_GET['results'])) {
    $pager_count = $_GET['results'];
  }

  // Get all the submissions for the node.
  $header = theme('webform_results_table_header', array('node' => $node));
  $submissions = webform_get_submissions($node->nid, $header, $pager_count);
  $total_count = webform_get_submission_count($node->nid);

  $output = theme('webform_results_table', array('node' => $node, 'components' => $node->webform['components'], 'submissions' => $submissions, 'total_count' => $total_count, 'pager_count' => $pager_count));
  if ($pager_count) {
    $output .= theme('pager');
  }
  return $output;
}

function theme_webform_results_table_header($variables) {
  return array(
    array('data' => t('Type'), 'field' => 'sid', 'sort' => 'desc'),
    array('data' => t('Name'), 'field' => 'submitted'),
    array('data' => t('Url'), 'field' => 'name'),
    array('data' => t('Logo URL'), 'field' => 'remote_addr'),
  );
}

/**
 * Theme the results table displaying all data sources
 */
function theme_webform_results_table($variables) {
  $node = $variables['node'];
  $components = $variables['components'];
  $submissions = $variables['submissions'];
  $total_count = $variables['total_count'];
  $pager_count = $variables['pager_count'];

  $header = array();
  $rows = array();
  $cell = array();

  // This header has to be generated seperately so we can add the SQL necessary.
  // to sort the results.
 $header = theme('webform_results_table_header', array('node' => $node));

  // Generate a row for each submission.
  foreach ($submissions as $sid => $submission) {
    $cell[] = l($sid, 'node/' . $node->nid . '/submission/' . $sid);
    $cell[] = format_date($submission->submitted, 'short');
    $cell[] = theme('username', array('account' => $submission));
    $cell[] = $submission->remote_addr;
    $component_headers = array();

    // Generate a cell for each component.
    foreach ($node->webform['components'] as $component) {
      $data = isset($submission->data[$component['cid']]['value']) ? $submission->data[$component['cid']]['value'] : NULL;
      $submission_output = webform_component_invoke($component['type'], 'table', $component, $data);
      if ($submission_output !== NULL) {
        $component_headers[] = check_plain($component['name']);
        $cell[] = $submission_output;
      }
    } 

    $rows[] = $cell;
    unset($cell);
  }
  if (!empty($component_headers)) {
    $header = array_merge($header, $component_headers);
  }

  if (count($rows) == 0) {
    $rows[] = array(array('data' => t('There are no submissions for this form. <a href="!url">View this form</a>.', array('!url' => url('node/' . $node->nid))), 'colspan' => 4));
  }


  $output = '';
  $output .= theme('table', array('header' => $header, 'rows' => $rows));
  return $output;
}

/**
 * Delete all submissions for a Data Means
 *
 * @param $nid
 *   The node id whose submissions will be deleted.
 */
function webform_results_clear_form_submit(&$form, &$form_state) {
foreach (($form_state['values']['table']) as $rows) {
	
	//Data means for source is also deleted
	$dum_deleted = db_delete('DataMeans')
  		->condition('id', $rows)
  		->execute();  
}
 drupal_set_message (t('Data Means Deleted!'));

}

/**
 * Confirmation form to delete data means entry in table
 */
function webform_results_clear_form($form, $form_state, $node) {
 db_set_active('default'); 
 
 //$form = array();
 $rows = array(); 
 
 //header setup
  $header = array
  (
     t('Name of Means'),
     t('Url'),
	 t('Type'),
  );
  
$query = db_select('DataMeans');
	$query ->fields('DataMeans', array('id','name', 'url', 'type')); //SELECT the fields from Data Source Table
	$results = $query-> execute();

  foreach ($results as $line) {
    $rows[$line->id] = array(  // <-- No array keys specified here 
        $line->name,
        $line->url,
        $line->type,
    );
}

 
 //Checkboxes Setup
  $form['table'] = array
  (
    '#type' => 'tableselect',
	'#title' => 'Delete Source?',
    '#header' => $header,
    '#options' => $rows,
	'#empty' => t('No Data Means found'),
    '#js_select' => FALSE,
  );
  
  $form['submit'] = array
  (
    '#type' => 'submit',
    '#value' => t('Delete'),
	'#submit' => array('webform_results_clear_form_submit'),
  );
  return $form;
}

/**
 * Form to add data sources!
 */
function webform_results_download_form($form, &$form_state, $node) {
 db_set_active('default'); 
 
 //$form = array();
 $rows = array(); 
 
 //header setup
  $header = array
  (
     t('Name of Source'),
     t('Url'),
	 t('Logo Url'),
  );
  
$query = db_select('DataSource');
	$query ->fields('DataSource', array('id','name', 'url', 'logourl')); //SELECT the fields from Data Source Table
	$results = $query-> execute();

  foreach ($results as $line) {
    $rows[$line->id] = array(  // <-- No array keys specified here 
        $line->name,
        $line->url,
        $line->logourl,
    );
}

 
 //Checkboxes Setup
  $form['table'] = array
  (
    '#type' => 'tableselect',
	'#title' => 'Delete Source?',
    '#header' => $header,
    '#options' => $rows,
	'#empty' => t('No Data Sources found'),
    '#js_select' => TRUE,
  );
  $form['submit'] = array
  (
    '#type' => 'submit',
    '#value' => t('Delete'),
	'#submit' => array('webform_results_download_form_submit'),
  );
  return $form;
}


/**
 * Delete Data Source from table / database
 */
function webform_results_download_form_submit(&$form, &$form_state) {
 
foreach (($form_state['values']['table']) as $rows) {
	$num_deleted = db_delete('DataSource')
  		->condition('id', $rows)
  		->execute(); 
		
	//Data means for source is also deleted
	$dum_deleted = db_delete('DataMeans')
  		->condition('DataSource_id', $rows)
  		->execute();  
}
 drupal_set_message (t('Data Source Deleted!'));

}


/**
Empty Page For Analysis
 */
function webform_results_analysis($node, $sids = array(), $analysis_component = NULL) {
  if (!is_array($sids)) {
    $sids = array();
  }

  // If showing a component's details, we don't want to loose the menu tabs.
  if ($analysis_component) {
    $item = menu_get_item('node/' . $node->nid . '/webform-results/analysis');
    menu_set_item(NULL, $item);
  }

  $components = isset($analysis_component) ? array($analysis_component['cid'] => $analysis_component) : $node->webform['components'];
  $data = array();
  foreach ($components as $cid => $component) {
    // Do component specific call.
    if ($row_data = webform_component_invoke($component['type'], 'analysis', $component, $sids, isset($analysis_component))) {
      $data[$cid] = $row_data;
    }
  }

  return theme('webform_results_analysis', array('node' => $node, 'data' => $data, 'sids' => $sids, 'component' => $analysis_component));
}
