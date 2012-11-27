<?php

/**
 * @file
 * Configuration file for Data Fusion Node Tab Displayed with article.
 * Drupal Custom Module for Sun_In_The_City, Cornell Daily Tech Newspaper
 * Author: Sean Salmon
 */

/**
 * Page Callback for node/%node/datafusion
 */
function datafusion_configure_form($form, &$form_state) {
	db_set_active('default'); // Set to Sun_In_The_City Database
	
		// Initialize Rows
		$rows = array(); 

		// Header Setup
		$header = array (
				t('Title'),
				t('Url'),
				t('Summary'),
				t('Source'),
				t('Rating'),
				t('Approved?'),
				);

		// Obtain nodeID through url parsing 
		$node_id = arg(1); 

		$query = db_select('DataFusion');
		$query ->fields('DataFusion', array('nodeID','id','title', 'url', 'rating', 'summary', 'DataSource_id', 'approved', 'deleted')); // SELECT the fields from Data Fusion Table
		$query ->condition('nodeID', $node_id, '=');
		$query ->condition('deleted', 0, '=');
		$query -> orderBy('rating', $direction = 'DESC'); // Get articles in order of highest rated shown higher [feel free to change] 
		$results = $query-> execute();
		
		foreach ($results as $line) {
			if ($line->approved == 1){
				$approved = 'YES';	
			}
			if ($line->approved == 0){
				$approved = 'NO';	
			}
			// DataSource table in database query
			$queryA= db_select('DataSource', 'd');
			$queryA -> fields('d', array('id', 'name'));
			$queryA -> condition ('id' , $line->DataSource_id, '=');
			$resultsA = $queryA -> execute(); 

			$rowA = $resultsA -> fetchAssoc();
			$sourceName = $rowA['name'];

			$rows[$line->id] = array( 
					$line->title,
					$line->url,
					$line->summary,
					$sourceName,
					$line-> rating,
					$approved,
					//	l(t('Edit'), 'node/' . $node->nid . '/datafusion/'),
					// Add edit button to the table array('colspan' => 3, 'data' => drupal_render($form['edit'])), 
					);
		}
		//Table Setup
		$form['table'] = array (
				'#type' => 'tableselect',
				'#title' => 'Approve',
				'#header' => $header,
				'#options' => $rows,
				'#empty' => t('No Data Fusion articles found'),
				'#js_select' => TRUE,
				'#weight' => 0,
				);

		// Approve button clicked
		$form['approve'] = array (
				'#type' => 'submit',
				'#value' => t('Approve'),
				'#submit' => array('datafusion_configure_form_approve'),
				'#weight' => 1,
				);
		//delete button clicked
		$form['delete'] = array (
				'#type' => 'submit',
				'#value' => t('Delete'),
				'#submit' => array('datafusion_configure_form_delete'),
				'#weight' => 2,
				);
		//Edit button 
		$form['edit'] = array(
				'#type'=> 'submit',		
				'#value'=> t('Edit'),
				'#submit'=> array('datafusion_configure_form_edit'),
				'#weight'=> 2,
			);
			
		// Options for the Ratings 'select' form element
		$rateOptions = array(0,1,2,3,4,5,6,7,8,9,10);
			
	//Boolean determining which form to create based on whether the edit button has been pressed or not
	$theTerm = variable_get('term','');

	// True means the Edit button has been clicked.
	if ($theTerm == true){		
	
		// Obtain global boolean data 
		$title = variable_get('title', '');
		$url = variable_get('url','');
		$summary = variable_get('summary', '');
		$rating = variable_get('rating', '');
		$dataSource= variable_get('dataSource', '');
		$form = array();
					
		//Textfield for inserting Data Fusion title
		$form['title']= array(
				'#type' => 'textfield',
				'#title' => 'Insert Data Fusion Title',
				'#description' => 'e.g "Outside Source Title"', 
				'#default_value' => $title,
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 3,
				);  
		//Textfield for inserting Data Fusion url
		$form['url']= array(
				'#type' => 'textfield',
				'#title' => 'Insert Data Fusion Url',
				'#description' => 'e.g "www.outsideresource.com"', 
				'#default_value' => $url,
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 4,
				); 

		//Textfield for inserting Data Fusion summary
		$form['summary']= array(
				'#type' => 'textarea',
				'#title' => 'Insert Data Fusion Summary',
				'#description' => 'e.g "Outside Source Text"', 
				'#default_value' => $summary,
				'#resizable' => FALSE,
				'#rows' => 6,
				'#weight' => 5,	
				);  

		
		// Pick Chooser for ratings
		$form['ratings'] = array(
				'#title' => 'Article Rating',
				'#type' => 'select',
				'#default_value' => $rating,
				'#options' => $rateOptions,
				'#weight' => 6,
				'#description' => 'A rating of 10 represents the best rating an article can receive',
				); 
				
		// Database query to get the options for the DataSource Chooser
		$sources = array();

		$query = db_select('DataSource', 'd');
		$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
		$result	= $query-> execute();

		$loopCounter = 0; 
		while ($row = $result-> fetchAssoc()){
			$sources[$loopCounter] = t($row['name']);
			$loopCounter ++; 
		}
		
		// Search Data Sources for right source
		$dataSourceKey = array_search($dataSource, $sources);
		
		// Pick chooser for Data Source ID
		$form['dataSource'] = array(
				'#title' => 'Choose Data Source',
				'#type' => 'select',
				'#default_value' => $dataSourceKey,
				'#options' => $sources,
				'#weight' => 7,
				);


		// Add data fusion element
		$form['update'] = array (
				'#type' => 'submit',
				'#value' => t('Update'),
				'#submit' => array('datafusion_configure_form_update'),
				'#weight' => 9,
				);


	}



	// If Button is not clicked
	if ($theTerm == false) {
		
		//Textfield for inserting Data Fusion title
		$form['title']= array(
				'#type' => 'textfield',
				'#title' => 'Insert Data Fusion Title',
				'#description' => 'e.g "Outside Source Title"', 
				'#default_value' => '',
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 4,
				); 

		//Textfield for inserting Data Fusion url
		$form['url']= array(
				'#type' => 'textfield',
				'#title' => 'Insert Data Fusion Url',
				'#description' => 'e.g "www.outsideresource.com"', 
				'#default_value' => ' ',
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 5,
				); 

		//Textfield for inserting Data Fusion summary
		$form['summary']= array(
				'#type' => 'textarea',
				'#title' => 'Insert Data Fusion Summary',
				'#description' => 'e.g "Outside Source Text"', 
				'#default_value' => ' ',
				'#resizable' => FALSE,
				'#rows' => 6,
				'#weight' => 6,
				);  
				
		// Pick Chooser for ratings
		$form['ratings'] = array(
				'#title' => 'Article Rating',
				'#type' => 'select',
				'#default_value' => ' ',
				'#options' => $rateOptions,
				'#description' => 'A rating of 10 represents the best rating an article can receive',
				'#weight' => 7,
				); 
				
		// Database query to get the options for the DataSource Chooser
		$sources = array();

		$query = db_select('DataSource', 'd');
		$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
		$result	= $query-> execute();

		$loopCounter = 0; 
		while ($row = $result-> fetchAssoc()){
			$sources[$loopCounter] = t($row['name']);
			$loopCounter ++; 
		}
		
		// Pick chooser for Data Source ID
		$form['dataSource'] = array(
				'#title' => 'Choose Data Source',
				'#type' => 'select',
				'#default_value' => ' ',
				'#options' => $sources,
				'#weight' => 8,
				);

		// Add data fusion element
		$form['add'] = array(
				'#type' => 'submit',
				'#value' => t('Add'),
				'#submit' => array('datafusion_configure_form_add'),
				'#weight' => 10,
				);
	}

	return $form;
}
/**
 * Method for handling actions upon update button being clicked
 */
function datafusion_configure_form_update($form, &$form_state) {
	//Get values from the fields
	$source_title = $form_state['values']['title'];
	$source_url = $form_state['values']['url'];
	$source_summary = $form_state['values']['summary'];
	$source_ratings = $form_state['values']['ratings']; 
	$data_source = $form_state['values']['dataSource'];
	$source_chooser = $form['dataSource']['#options'][$data_source]; // Get the string value from select box

	// Error Handling
	if ($source_url == NULL || $source_title == NULL || $source_summary == NULL || $source_chooser == NULL ||
			$source_url == ' ' || $source_summary == ' ' || $source_title == ' '){
		drupal_set_message(t('Unable to update data fusion article. Please review entries into input fields.'), $type = 'error');
	}
	else{
		// Get the particular datafusion element
		$theDataFusionElement = variable_get('theDataFusionElement', '');

		// Change information in the database
		$query = db_select('DataSource', 'd');
		$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
		$query  ->condition('name', $source_chooser,'=');
		$result	= $query-> execute();
		$row = $result-> fetchAssoc();

		$dbID = $row['id'];

		//Datafusion table in database query	
		$num_deleted = db_update('DataFusion')
				->fields(array('title' => $source_title,'url' => $source_url, 'summary' => $source_summary, 'rating' => $source_ratings, 'DataSource_id' => $dbID))
				->condition('id', $theDataFusionElement)
				->execute();  

				drupal_set_message(t('Article has been updated'));
				variable_set('term', false); //set flag boolean back to false signifying that editing has been completed
	}	
}


/**
 * Method for handling actions upon edit button being clicked
 */
function datafusion_configure_form_edit($form, &$form_state) {
	$dfInfo = array();
	$dfInfo = array_filter($form_state['values']['table']);

	// Error handling for no elements selected
	if ($dfInfo == NULL){
		drupal_set_message(t('Please select a row to edit'), $type = 'error');
	}
	
	// Check to make sure only 1 element is selected 
	if(count($dfInfo) > 1){
		drupal_set_message(t('Must only select one row to be edited at a time.'), $type = 'error');
	}


	if (count($dfInfo) == 1){
		foreach ($dfInfo as $row) {

			//Get the data
			$query = db_select('DataFusion', 'd');
			$query ->fields('d', array('id', 'title', 'url', 'summary', 'rating', 'DataSource_id')); //SELECT the fields from Data Fusion Table
			$query ->condition('id', $row);
			$result	= $query-> execute();

			$row = $result-> fetchAssoc();
			$theDataFusionElement = $row['id'];

			// get the Data Source ID name
			$queryA = db_select('DataSource', 'd');
			$queryA ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
			$queryA  ->condition('id', $row['DataSource_id'],'=');
			$resultA = $queryA-> execute();
			$rowA = $resultA-> fetchAssoc();

			$theSource = $rowA['name'];

			//Store fields in global variables
			$title = ' ';
			variable_set('title', $row['title']);
			$url = '';
			variable_set('url', $row['url']);
			$summary = '';
			variable_set('summary', $row['summary']);
			$rating = -1;
			variable_set('rating', $row['rating']);
			$dataSource= '';
			variable_set('dataSource', $theSource);

			//keep track of which data fusion element was selected to be edited
			variable_set('theDataFusionElement', $theDataFusionElement);

			// Set global variable to represent the edit button being pressed
			$term = true;
			variable_set('term', true); 

		} 
	} 
}

/**
 * Handler for Adding Data Fusion Element to Database 
 */ 
function datafusion_configure_form_add($form, &$form_state){
	// Obtain the Input Field Data
	$source_title = $form_state['values']['title'];
	$source_url = $form_state['values']['url'];
	$source_summary = $form_state['values']['summary'];
	$source_ratings = $form_state['values']['ratings']; 
	$data_source = $form_state['values']['dataSource'];
	$source_chooser = $form['dataSource']['#options'][$data_source]; // Get the chosen Data Source's string value from 'select' form element

	// Error Handling 
	if ($source_url == NULL || $source_title == NULL || $source_summary == NULL || $source_chooser == NULL ||
			$source_url == ' ' || $source_summary == ' ' || $source_title == ' '){
		drupal_set_message(t('Unable to add to the database. Please review entries into input fields.'), $type = 'error');
	}

	else{

		// Get the page's node ID by using the url 
		$argument_one = arg(0);
		$node_id = arg(1); 

		// Database query to obtain the Data Source ID from datasource 'select' form element
		$query = db_select('DataSource', 'd');
		$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
		$query ->condition('name', $source_chooser);
		$result1 = $query-> execute();
		$row = $result1-> fetchAssoc();

		$dbID = $row['id'];

		// Get Current Time to be input as field for Data Fusion Element
		$time = date("Y-m-d H:i:s", time());	

		$query = db_insert('DataFusion') 
				->fields(array(
						'nodeID' => $node_id,
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
 * Approve Handler for Marking Data Fusion Article in database
 */
function datafusion_configure_form_approve($form, &$form_state) {
	$dfInfo = array();
	$dfInfo = array_filter($form_state['values']['table']);

	// Error handling for no elements selected
	if ($dfInfo == NULL){
		drupal_set_message(t('Please select an element to delete'), $type = 'error');
	}
	
if ($dfInfo != NULL){
	foreach (($form_state['values']['table']) as $rows) {

		//Mark as approved in database
		$data_fusion_approve_query = db_update('DataFusion')
				->fields(array('approved' => 1, 'deleted' => 0)) 
				->condition('id', $rows)
				->execute();  

		}
	drupal_set_message(t('This Data Fusion Element has been Marked as Approved to be Displayed alongside your Article.'));
	}
}
/**
 * Delete Handling for Data fusion article
 */
function datafusion_configure_form_delete($form, &$form_state) {
	$dfInfo = array();
	$dfInfo = array_filter($form_state['values']['table']);

	// Error handling for no elements selected
	if ($dfInfo == NULL){
		drupal_set_message(t('Please select an element to delete'), $type = 'error');
	}
// Elements selected
if ($dfInfo != NULL) {	
	foreach (($form_state['values']['table']) as $rows) {
		//Mark as deleted in database
		$data_fusion_update_query = db_update('DataFusion')
				->fields(array('approved' => 0, 'deleted' => 1)) 
				->condition('id', $rows)
				->execute();  
		}
	drupal_set_message(t('This Data Fusion Element has been Marked as Deleted, meaning it will NOT be Displayed alongside your Article.'));
	}
}