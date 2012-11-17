<?php

/**
 * @file
 * Administration pages provided by Webform module.
 */

/**
 * Menu callback for admin/config/content/webform.
 */
function webform_admin_settings() {
	 db_set_active('default'); 
	 
 $sources = array();

    $query = db_select('DataSource', 'd');
	$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
	$result	= $query-> execute();
	
	$loopCounter = 0; 
	while ($row = $result-> fetchAssoc()){
		$sources[$loopCounter] = t($row['name']);
		$loopCounter ++; 
	}
	

  $options = $sources;

  module_load_include('inc', 'webform', 'includes/webform.export');
  // Article, Basic Page and Webform
  $node_types = node_type_get_names();
  $form['node_types'] = array(
    '#type' => 'checkboxes',
    '#title' => t('Data Fusion-enabled content types'),
    '#description' => t('Complete List of Data Fusion articles on the website.'),
    '#options' => $node_types,
    '#default_value' => webform_variable_get('webform_node_types'),
  );
//Row
$rows = array();
 //header setup
  $header = array
  (
     t('Title'),
     t('Url'),
	 t('Summary'),
	 t('Fused Article'),
	 t('Approved?'),
  );

  /**
  	Table Setup
   */

	$query = db_select('DataFusion');
	$query ->fields('DataFusion', array('id', 'nodeID','title', 'url', 'summary', 'approved')); //SELECT the fields from Data Source Table
	$results = $query-> execute();

  foreach ($results as $line) {
	//Get the Article's Title
	  $aid = $line->nodeID; // get the article's ID
	  $query = db_select('node', 'n');
	  $query ->fields('n', array('title')); //SELECT the title field from the node list
	  $query ->condition('nid', $aid, '=');
	  $results1 = $query-> execute();
	  $articles = $results1-> fetchAssoc();
	  $articleTitle= t($articles['title']);


	if ($line->approved == 1){
			$approved = 'YES';	
	}
	
	if ($line->approved == 0){
			$approved = 'NO';	
	}
//For each Loop
 
    $rows[$line->id] = array(  // <-- No array keys specified here 
            
		$line->title,
        $line->url,
        $line->summary,
		$articleTitle,
	 	$approved,
    );
}

  $form['table'] = array
  (
    '#type' => 'tableselect',
	'#title' => 'Data Fusion Aricles',
    '#header' => $header,
    '#options' => $rows,
	'#empty' => t('No Data Fusion articles found'),
    '#js_select' => TRUE,
  );
  // BUTTONS
  $form['Approve'] = array(
    '#type' => 'submit',
    '#title' => t('Approve'),
    '#weight' => 2,
	'#value' => t('Approve'),
	'#submit' => array('webform_admin_settings_approve'),
  );
  
    $form['reject'] = array(
    '#type' => 'submit',
    '#title' => t('Approve'),
    '#weight' => 2,
	'#value' => t('Reject'),
	'#submit' => array('webform_admin_settings_reject'),
  );
  
//Adding sources collapsable area
  $form['advanced'] = array(
    '#type' => 'fieldset',
    '#title' => t('Add Data Fusion Article'),
    '#collapsible' => TRUE,
    '#collapsed' => TRUE,
	'#weight' => 3
  );

  $form['advanced']['title']  = array(
    '#type' => 'textfield',
    '#title' => t('Article Title'),
    '#description' => t('Title of Article'),
    '#default_value' => ' ',
  );

    //Textfield for inserting Data Fusion url
 $form['advanced']['url']= array(
 	'#type' => 'textfield',
	'#title' => 'Insert Data Fusion Url',
	'#description' => 'e.g "www.outsideresource.com"', 
	'#default_value' => ' ',
	'#size' => 40,
	'#maxlength' => 255,
 ); 
  
  //Textfield for inserting Data Fusion summary
 $form['advanced']['summary']= array(
 	'#type' => 'textarea',
	'#title' => 'Insert Data Fusion Summary',
	'#description' => 'e.g "Outside Source Text"', 
	'#default_value' => ' ',
	'#resizable' => FALSE,
	'#rows' => 6,
 );  
 //Add
   $form['advanced']['add']  = array(
    '#type' => 'submit',
    '#value' => t('Add data fusion element'),
	'#submit' => array('webform_admin_settings_add'),
	'#weight' => 5,
  );	

   // Pick Chooser for ratings
   // Options for the Ratings Selector
 $rateOptions = array(
 1,2,3,4,5,6,7,8,9,10);
 
$form['advanced']['ratings'] = array(
	'#title' => 'Article Rating',
    '#type' => 'select',
	'#default_value' => ' ',
    '#options' => $rateOptions,
    '#weight' => 1,
); 

// Chooser for data sources
// Pick chooser for Data Source ID
$form['advanced']['chooser'] = array(
	'#title' => 'Choose Data Source',
	'#type' => 'select',
	'#default_value' => ' ',
	'#options' => $options,
	'#weight' => 1,
);

 //Options for the node Chooser
$Noptions = array();
$sources = array();
    $query = db_select('node', 'n');
	$query ->fields('n', array('nid', 'title')); //SELECT the fields from Data Source Table
	$result	= $query-> execute();
	
	$loopCounter = 0; 
	while ($row = $result-> fetchAssoc()){
		$sources[$loopCounter] = t($row['title']);
		$loopCounter ++; 
	}
  $Noptions = $sources;

// Chooser for nodes
// Pick chooser for Nodes ID
$form['advanced']['nodes'] = array(
	'#title' => 'Choose Article',
	'#type' => 'select',
	'#default_value' => ' ',
	'#options' => $Noptions,
	'#weight' => 1,
);

  $form = system_settings_form($form);
  $form['#theme'] = 'webform_admin_settings';
  array_unshift($form['#submit'], 'webform_admin_settings_submit');

  return $form;
}

/**
 * Reject a data fusion source
 */
function webform_admin_settings_reject($form, &$form_state) {
  foreach (($form_state['values']['table']) as $rows) {
	//Mark as deleted
	$num_deleted = db_update('DataFusion')
  		->fields(array('approved' => 0, 'deleted' => TRUE)) 
		->condition('id', $rows)
  		->execute();  
}
  drupal_set_message(t('The Data Fusion Element has been Marked as Deleted'));

}

/**
 * Approve a data fusion source
 */
function webform_admin_settings_approve($form, &$form_state) {
 foreach (($form_state['values']['table']) as $rows) {
	//Mark as approved
	$num_deleted = db_update('DataFusion')
  		->fields(array('approved' => TRUE, 'deleted' => 0)) 
		->condition('id', $rows)
  		->execute();  
}
	drupal_set_message(t('The Data Fusion Element has been Marked as Approved'));
}

/**
 * Reject a data fusion source
 */
function webform_admin_settings_add($form, &$form_state) {

  $source_title = $form_state['values']['title'];
  $source_url = $form_state['values']['url'];
  $source_summary = $form_state['values']['summary'];
  $source_ratings = $form_state['values']['ratings']; 
  $dataKey = $form_state['values']['chooser'];
  $source_chooser = $form['advanced']['chooser']['#options'][$dataKey]; // Get the string value from select box
  $data_nodes = $form_state['values']['nodes'];
  
   //Get the Data Source ID
 $query = db_select('DataSource', 'd');
	$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
	$query ->condition('name', $source_chooser, '=');
	$result1 = $query-> execute();
	$row = $result1-> fetchAssoc();
	
	$dbID = $row['id'];
	
// WORK ON USER PERMISSIONS!
if ($source_url == NULL || $source_title == NULL || $source_summary == NULL || 
$source_url == ' ' || 
$source_summary == ' ' || 
$data_source == ' ' ||
$source_ratings == ' ' ||
$source_title == ' '){
   drupal_set_message(t('Unable to add to the database. Please review entries into input fields.'), $type = 'error');
}

else{ 
 
 //get Time
 $time = date("Y-m-d H:i:s", time());	
$query = db_insert('DataFusion') 
  ->fields(array(
  	'nodeID' => $data_nodes,
    'title' =>  $source_title,
	'url' => $source_url,
	'summary' => $source_summary,
	'rating' => $source_ratings,
	'DataSource_id' => $dbID,
	'timestamp' => $time,
	'approved' => TRUE,
  ))
  ->execute(); 
  
drupal_set_message(t('Data Fusion Element has been added to the database')); 
}
}
/**
 * Submit handler for the webform_admin_settings() form.
 */
function webform_admin_settings_submit($form, &$form_state) {

  // Change the name of the node type variable and clean it up.
  $form_state['values']['webform_node_types'] = array_keys(array_filter($form_state['values']['node_types']));
  unset($form_state['values']['node_types']);
}
/**
 * Menu callback for admin/content/webform. Displays all webforms on the site.
 */
function webform_admin_content() {
  $webform_types = webform_variable_get('webform_node_types');

  $nodes = array();
  if ($webform_types) {
    $nodes = db_select('node', 'n')
      ->fields('n')
      ->condition('n.type', $webform_types, 'IN')
      ->execute()
      ->fetchAllAssoc('nid');
  }

  return theme('webform_admin_content', array('nodes' => $nodes));
}

/**
 * Create a comma-separate list of content types that are webform enabled.
 */
function webform_admin_type_list() {
  $webform_types = webform_variable_get('webform_node_types');
  $webform_type_list = '';
  $webform_type_count = count($webform_types);
  foreach ($webform_types as $n => $type) {
    $webform_type_list .= l(node_type_get_name($type), 'node/add/' . $type);
    if ($n + 1 < $webform_type_count) {
      $webform_type_list .= $webform_type_count == 2 ? ' ' : ', ';
    }
    if ($n + 2 == $webform_type_count) {
      $webform_type_list .= t('or') . ' ';
    }
  }

  return $webform_type_list;
}

