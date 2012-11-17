<?php

/**
Drupal Custom Module for Sun_In_The_City, Cornell Daily Tech Newspaper
Author: Sean Salmon
 */

/**
 * Overview form of all components for this module.
 */
function webform_emails_form($form, $form_state, $node) {
	//Connect to sun_in_the_city database
	db_set_active('default'); 
 
 $form = array();
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

//Controls Data Source Name textbox field
  $form['type'] = array(
  	'#title' => 'Insert Name of Means  ',
	'#description' => 'e.g "CNET RSS Feed"', 
	'#default_value' => ' ',
    '#type' => 'textfield',
	'#size' => 40,
	'#maxlength' => 255,
);
//Controls the attributes of the Data Sources textbox field
  $form['name'] = array(
    '#title' => 'Insert URL to Data Means ',
	'#description' => 'e.g "http://www.cnet.com/rss"', 
	'#default_value' => ' ',
    '#type' => 'textfield',
    '#size' => 40,
    '#maxlength' => 255,
  );


//Controls Data Source type textbox field
  $form['tipe'] = array(
  	'#title' => 'Insert Type',
	'#description' => 'e.g "RSS"', 
	'#default_value' => ' ',
    '#type' => 'textfield',
	'#size' => 40,
	'#maxlength' => 255,
);

// Pick Chooser
$form['chooser'] = array(
	'#title' => 'Choose Original Data Source',
    '#type' => 'select',
	'#default_value' => ' ',
    '#options' => $options,
    '#weight' => 3,
); 
//The Add Button

  $form['add'] = array(
    '#type' => 'submit',
    '#value' => t('Add'),
    '#weight' => 45,
  );
  

  return $form;
}

/**
 * Submit handler for webform_emails_form().
 */
function webform_emails_form_submit($form, &$form_state) {
$means_url = $form_state['values']['name'];
$means_name = $form_state['values']['type'];
$means_type = $form_state['values']['tipe'];
$chooserKey = $form_state['values']['chooser']; //Gets Key from the select box
$source_chooser = $form['chooser']['#options'][$chooserKey]; // Get the string value from select box
// WORK ON USER PERMISSIONS!

if ($means_url == NULL || $means_name == NULL || $means_type == NULL || $chooserKey == NULL || $source_chooser == NULL || 
 $means_url == ' ' || $means_name == ' ' || $means_type == ' '){
	 // make sure type is int : || !ctype_digit($means_type)
   drupal_set_message(t('Unable to add to the database. Please review entries into input fields.'), $type = 'error');
}

else{
	
	$query = db_select('DataSource', 'd');
	$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
	$query  ->condition('name', $source_chooser,'=');
	$result	= $query-> execute();
	$row = $result-> fetchAssoc();
	
	$dbID = $row['id'];
   $lastP = $date1 = date("Y-m-d H:i:s", time());		
$query = db_insert('DataMeans') 
  ->fields(array(
  	'DataSource_id' => $dbID,
    'url' =>  $means_url,
	'name' => $means_name,
	'type' => 1, // Ask About Type!
	'lastProcessed' => $lastP, // ASK About lastProcessed. 
  ))
  ->execute();  
  
  // Success message
   drupal_set_message(t('Successfully added to the Database'));

    // Since Webform components have been updated but the node itself has not
    // been saved, it is necessary to explicitly clear the cache to make sure
    // the updated webform is visible to anonymous users.
    cache_clear_all();

	return $form_state;
}
}