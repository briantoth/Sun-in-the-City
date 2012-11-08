<?php

/**
Drupal Custom Module for Sun_In_The_City, Cornell Daily Tech Newspaper
Author: Sean Salmon
 */

/**
 * Overview page of all components for this module.
 */
function webform_components_page($node, $page_number = 1) {
  $output = drupal_get_form('webform_components_form', $node);
//Connect to sun_in_the_city database
	db_set_active('default'); 
	
  return array(
    '#theme' => 'webform_components_page',
    '#node' => $node,
    '#form' => $output,
  );
}

/**
 * Theme the output of the main data sources page.
 */
function theme_webform_components_page($variables) {
  $node = $variables['node'];
  $form = $variables['form'];

  return drupal_render($form);
}

/**
 * The Data Sources Page layout.
 */
function webform_components_form($form, $form_state, $node) {
 $form = array();
  $options = array();
//Controls Data Source Name textbox field
  $form['type'] = array(
  	'#title' => 'Insert Name of Source ',
	'#description' => 'e.g "The New York Times"',
	'#default_value' => ' ',
    '#type' => 'textfield',
	'#size' => 40,
	'#maxlength' => 255,
);
//Controls the attributes of the Data Sources textbox field
  $form['name'] = array(
    '#title' => 'Insert URL to article',
	'#description' => 'e.g "http://www.nytimes.com"',
	'#default_value' => ' ',
    '#type' => 'textfield',
    '#size' => 40,
    '#maxlength' => 255,
  );


//Controls the logo url
  $form['logo'] = array(
    '#title' => 'Insert Logo URL  ',
	'#description' => 'e.g "http://www.imgur.com/n7jh.jpg"',
	'#default_value' => ' ',
    '#type' => 'textfield',
    '#size' => 40,
    '#maxlength' => 255,
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
 Add Source button Pressed
*/
function webform_components_form_submit($form, &$form_state) {
  $source_url = $form_state['values']['name'];
  $source_name = $form_state['values']['type'];
  $source_logo = $form_state['values']['logo'];

// WORK ON USER PERMISSIONS!
if ($source_url == NULL || $source_name == NULL || $source_logo == NULL || $source_url == ' ' || $source_name == ' ' || $source_logo == ' '){
   drupal_set_message(t('Unable to add to the database. Please review entries into input fields.'), $type = 'error');
}

else{
	 //get the node ID 
 // $argument_one = arg(0);
 // $node_id = arg(1); 
$query = db_insert('DataSource') 
  ->fields(array(
 //   'nodeID' => $node_id,
    'url' =>  $source_url,
	'name' => $source_name,
	'logourl' => $source_logo,
  ))
  ->execute();  
  // Success message
   drupal_set_message(t('Successfully added to the Database'));
}

	return $form_state;
}



/**
 * The Default settings for the module 
 */
function webform_component_defaults(&$component) {
  if ($defaults = webform_component_invoke($component['type'], 'defaults')) {
    foreach ($defaults as $key => $default) {
      if (!isset($component[$key])) {
        $component[$key] = $default;
      }
    }
    foreach ($defaults['extra'] as $extra => $default) {
      if (!isset($component['extra'][$extra])) {
        $component['extra'][$extra] = $default;
      }
    }
    $component['extra'] += array(
      'conditional_component' => '',
      'conditional_operator' => '=',
      'conditional_values' => '',
    );
  }
}

