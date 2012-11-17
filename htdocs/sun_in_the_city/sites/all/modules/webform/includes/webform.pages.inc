<?php

/**
 * @file
 *
 * Menu callbacks and functions for configuring and editing webforms.
 */

/**
 * Main configuration form for editing a webform node.
 */
function webform_configure_form($form, &$form_state, $node) {
 db_set_active('default'); 
 
  $form = array();
 //Options for the DataSource Chooser

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

 //$form = array();
 $rows = array(); 
 
 // Options for the Ratings Selector
 $rateOptions = array(
 1,2,3,4,5,6,7,8,9,10);
 //header setup
  $header = array
  (
     t('Title'),
     t('Url'),
	 t('Summary'),
	 t('Approved?'),
  );
  
//get the node ID 
 $argument_one = arg(0);
 $node_id = arg(1); 
 
	$query = db_select('DataFusion');
	$query ->fields('DataFusion', array('nodeID','id','title', 'url', 'summary', 'approved')); //SELECT the fields from Data Fusion Table
	$query ->condition('nodeID', $node_id, '=');
	$results = $query-> execute();


	
  foreach ($results as $line) {
	  if ($line->approved == 1){
			$approved = 'YES';	
	}
	
	if ($line->approved == 0){
			$approved = 'NO';	
	}
    $rows[$line->id] = array(  // <-- No array keys specified here 
        $line->title,
        $line->url,
        $line->summary,
		$approved,
    );
}

   // Pick Chooser for ratings
$form['ratings'] = array(
	'#title' => 'Article Rating',
    '#type' => 'select',
	'#default_value' => ' ',
    '#options' => $rateOptions,
    '#weight' => 1,
); 

// Pick chooser for Data Source ID
$form['dataSource'] = array(
	'#title' => 'Choose Data Source',
	'#type' => 'select',
	'#default_value' => ' ',
	'#options' => $options,
	'#weight' => 1,
);
 //Table Setup
  $form['table'] = array
  (
    '#type' => 'tableselect',
	'#title' => 'Approve',
    '#header' => $header,
    '#options' => $rows,
	'#empty' => t('No Data Fusion articles found'),
    '#js_select' => TRUE,
  );
 
	// Approve button clicked
  $form['submit'] = array
  (
    '#type' => 'submit',
    '#value' => t('Approve'),
	'#submit' => array('webform_configure_form_approve'),
  );
  //delete button clicked
  $form['delete'] = array
  (
    '#type' => 'submit',
    '#value' => t('Reject'),
	'#submit' => array('webform_configure_form_reject'),
  );
  //Textfield for inserting Data Fusion title
 $form['title']= array(
 	'#type' => 'textfield',
	'#title' => 'Insert Data Fusion Title',
	'#description' => 'e.g "Outside Source Title"', 
	'#default_value' => ' ',
	'#size' => 40,
	'#maxlength' => 255,
 );  
  //Textfield for inserting Data Fusion url
 $form['url']= array(
 	'#type' => 'textfield',
	'#title' => 'Insert Data Fusion Url',
	'#description' => 'e.g "www.outsideresource.com"', 
	'#default_value' => ' ',
	'#size' => 40,
	'#maxlength' => 255,
 ); 
  
  //Textfield for inserting Data Fusion summary
 $form['summary']= array(
 	'#type' => 'textarea',
	'#title' => 'Insert Data Fusion Summary',
	'#description' => 'e.g "Outside Source Text"', 
	'#default_value' => ' ',
	'#resizable' => FALSE,
	'#rows' => 6,
 );  

 // add data fusion element
   $form['add'] = array
  (
    '#type' => 'submit',
    '#value' => t('Add'),
	'#submit' => array('webform_configure_form_add'),
	'#weight' => 10,
  );

  return $form;
}

/**
 * Validate handler for webform_configure_form().
 */
function webform_configure_form_validate($form, &$form_state) {
}

/**
Add Data Fusion Element to the database 
*/ 
function webform_configure_form_add($form, &$form_state){

  $source_title = $form_state['values']['title'];
  $source_url = $form_state['values']['url'];
  $source_summary = $form_state['values']['summary'];
  $source_ratings = $form_state['values']['ratings']; 
  $data_source = $form_state['values']['dataSource'];
  $source_chooser = $form['dataSource']['#options'][$data_source]; // Get the string value from select box
  
// WORK ON USER PERMISSIONS!
if ($source_url == NULL || $source_title == NULL || $source_summary == NULL || $source_url == ' ' || $source_summary == ' ' || $source_title == ' '){
   drupal_set_message(t('Unable to add to the database. Please review entries into input fields.'), $type = 'error');
}

else{
//get the node ID 
 $argument_one = arg(0);
 $node_id = arg(1); 
 
 //Get the Data Source ID
 $query = db_select('DataSource', 'd');
	$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
	$query ->condition('name', $source_chooser);
	$result1 = $query-> execute();
	$row = $result1-> fetchAssoc();
	
	$dbID = $row['id'];
 
 //get Time
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
 * Approve Data Fusion Article
 */
function webform_configure_form_approve($form, &$form_state) {
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
 *Reject Data fusion article
 */
function webform_configure_form_reject($form, &$form_state) {
  foreach (($form_state['values']['table']) as $rows) {
	//Mark as deleted
	$num_deleted = db_update('DataFusion')
  		->fields(array('approved' => 0, 'deleted' => TRUE)) 
		->condition('id', $rows)
  		->execute();  
}
  drupal_set_message(t('The Data Fusion Element has been Marked as Deleted'));
}
