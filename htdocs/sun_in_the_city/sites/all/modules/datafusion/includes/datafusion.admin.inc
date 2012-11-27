<?php
/**
 * @file
 * Admin Configuration Page Setup.
 * Allows admin to configure Data Fusion Articles, Data Source, and Data Means existing on the entire website. 
 * Drupal Custom Module for Sun_In_The_City, Cornell Daily Tech Newspaper
 * Author: Sean Salmon
 */

/**
 * Menu Callback for admin/config/content/datafusion
 */
function admin_config_page() {
	$build[] = drupal_get_form('datafusion_form', 1);
	$build[] = drupal_get_form('datasource_form', 2);
	$build[] = drupal_get_form('datameans_form', 3);
	return drupal_render($build);
}

/**
 * Form for configuring datafusion elements
 */
function datafusion_form($form, $form_state, $i) {
	// False means the edit button has not been clicked as of yet
	$theTerm = variable_get('term','');

	
		$form = array();
		$rows = array(); 

		//header setup
		$header = array(
				t('Title'),
				t('Url'),
				t('Summary'),
				t('Fused Article'),
				t('Rating'),
				t('Approved?'),
				);

		
		// Table Setup
		
		$query = db_select('DataFusion');
		$query ->fields('DataFusion', array('id', 'nodeID','title', 'url', 'summary', 'rating', 'approved', 'timestamp', 'deleted')); //SELECT the fields from table
		$query-> condition('deleted', 0, '=');
		$query-> condition('approved', 0, '=');
		$query-> orderBy('timestamp', $direction = 'DESC'); // Show DataFusion Articles in order by most recently added [feel free to edit]
	
		// Uncomment this line to restrict the view of Datafusion Articles shown. Results older than a month old won't be shown.
		//$query -> condition('timestamp', $lastMonthTimestamp, '>' );
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
					$line->rating,
					$approved,
					);
		}


		// Table Setup
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
				'#submit' => array('datafusion_approve'),
				'#weight' => 1,
				);
		//delete button clicked
		$form['delete'] = array (
				'#type' => 'submit',
				'#value' => t('Delete'),
				'#submit' => array('datafusion_delete'),
				'#weight' => 2,
				);

		// edit button 
		$form['edit'] = array(
				'#type' => 'submit',
				'#value' => t('Edit'),
				'#submit' => array('datafusion_edit'),
				'#weight' => 3,
				);

//True means the Edit button has been clicked.
	if ($theTerm == true){

		//retrieve info from cloud
		$title = variable_get('title', '');
		$url = variable_get('url','');
		$summary = variable_get('summary', '');
		$rating = variable_get('rating', '');
		$dataSource= variable_get('dataSource', '');
		$articleNodeName = variable_get('articleNodeName','');


		//Textfield for inserting Data Fusion title
		$form['title']= array(
				'#type' => 'textfield',
				'#title' => 'Insert Data Fusion Title',
				'#description' => 'e.g "Outside Source Title"', 
				'#default_value' => $title,
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 4,
				);  

		//Textfield for inserting Data Fusion url
		$form['url']= array(
				'#type' => 'textfield',
				'#title' => 'Insert Data Fusion Url',
				'#description' => 'e.g "www.outsideresource.com"', 
				'#default_value' => $url,
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 5,
				); 

		//Textfield for inserting Data Fusion summary
		$form['summary']= array(
				'#type' => 'textarea',
				'#title' => 'Insert Data Fusion Summary',
				'#description' => 'e.g "Outside Source Text"', 
				'#default_value' => $summary,
				'#resizable' => FALSE,
				'#rows' => 6,
				'#weight' => 6,
				); 

		// Options for the Ratings Selector
		$rateOptions = array (0,1,2,3,4,5,6,7,8,9,10);

		// Pick Chooser for ratings
		$form['ratings'] = array(
				'#title' => 'Article Rating',
				'#type' => 'select',
				'#default_value' => $rating,
				'#options' => $rateOptions,
				'#weight' => 5,
				'#description' => 'A rating of 10 represents the best rating an article can receive',
				); 


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

		// Search Data Sources for right source
		$dataSourceKey = array_search($dataSource, $options);

		// Pick chooser for Data Source ID
		$form['chooser'] = array(
				'#title' => 'Choose Data Source',
				'#type' => 'select',
				'#default_value' => $dataSourceKey,
				'#options' => $options,
				'#weight' => 6,
				);

		//Options for the node Chooser
		$Noptions = array();
		$sources = array();
		$query = db_select('node', 'n');
		$query ->fields('n', array('nid', 'title')); //SELECT the fields from Data Source Table
		$query-> orderBy('nid');
		$result	= $query-> execute();
		$articleNode = -1; // initialize	
		$loopCounter = 0; 
		
		while ($row = $result-> fetchAssoc()){
			// Check to synchronize the correct location of the previously checked element's fused article in 'select' form element
			if (t($row['title']) == $articleNodeName){
				$articleNode = $loopCounter;
			}
			$sources[$loopCounter] = t($row['title']);
			$loopCounter ++; 
		}
		$Noptions = $sources;

		// Chooser for nodes
		// Pick chooser for Nodes ID
		$form['nodes'] = array(
				'#title' => 'Select Which Article to Attach Data Fusion Article to',
				'#type' => 'select',
				'#default_value' => $articleNode,
				'#options' => $Noptions,
				'#weight' => 7,
				);

		// Add data fusion element
		$form['update'] = array(
				'#type' => 'submit',
				'#value' => t('Update'),
				'#submit' => array('datafusion_update'),
				'#weight' => 10,
				);

	}
	//False means the Edit button has NOT been clicked.
	if ($theTerm == false){
		//Adding Data Fusion collapsable area
		$form['addfusion'] = array(
				'#type' => 'fieldset',
				'#title' => t('Add Data Fusion'),
				'#collapsible' => TRUE,
				'#collapsed' => TRUE,
				'#weight' => 4
				);


		$form['addfusion']['title']  = array(
				'#type' => 'textfield',
				'#title' => t('Data Fusion Article Title'),
				'#description' => t('Title of Article'),
				'#default_value' => ' ',
				'#weight' => 4,
				);

		//Textfield for inserting Data Fusion url
		$form['addfusion']['url']= array(
				'#type' => 'textfield',
				'#title' => 'Insert Data Fusion Url',
				'#description' => 'e.g "www.outsideresource.com"', 
				'#default_value' => ' ',
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 5,
				); 

		//Textfield for inserting Data Fusion summary
		$form['addfusion']['summary']= array(
				'#type' => 'textarea',
				'#title' => 'Insert Data Fusion Summary',
				'#description' => 'e.g "Outside Source Text"', 
				'#default_value' => ' ',
				'#resizable' => FALSE,
				'#rows' => 6,
				'#weight' => 6,
				);  

		// Pick Chooser for ratings
		// Options for the Ratings Selector
		$rateOptions = array(0,1,2,3,4,5,6,7,8,9,10);

		$form['addfusion']['ratings'] = array(
				'#title' => 'Article Rating',
				'#type' => 'select',
				'#default_value' => ' ',
				'#options' => $rateOptions,
				'#weight' => 7,
				); 
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

		// Chooser for data sources
		// Pick chooser for Data Source ID
		$form['addfusion']['chooser'] = array(
				'#title' => 'Choose Data Source',
				'#type' => 'select',
				'#default_value' => ' ',
				'#options' => $options,
				'#weight' => 8,
				);

		//Options for the node Chooser
		$Noptions = array();
		$query = db_select('node', 'n');
		$query ->fields('n', array('nid', 'title')); //SELECT the fields from Data Source Table
		$query-> orderBy('nid');
		$result	= $query-> execute();

		$loopCounter = 0; 
		while ($row = $result-> fetchAssoc()){
			$Noptions[$loopCounter] = $row['title'];
			$loopCounter ++; 
		}
		// Chooser for nodes
		// Pick chooser for Nodes ID
		$form['addfusion']['nodes'] = array(
				'#title' => 'Select Which Article to Attach Data Fusion Article to',
				'#type' => 'select',
				'#default_value' => ' ',
				'#options' => $Noptions,
				'#weight' => 9,
				);

		//Add
		$form['addfusion']['add']  = array(
				'#type' => 'submit',
				'#value' => t('Add'),
				'#submit' => array('datafusion_add'),
				'#weight' => 10,
				);	
	}

	return $form;
}
/**
 * Form for configuring Data Sources
 */ 
function datasource_form($form, $form_state, $i) {

	//Adding Data Sources collapsable area
	$form['sources'] = array(
			'#type' => 'fieldset',
			'#title' => t('Configure Data Sources'),
			'#collapsible' => TRUE,
			'#collapsed' => TRUE,
			'#weight' => 4
			);



	$rows = array(); 

	//header setup
	$header = array(
					t('Name of Source'),
					t('Url'),
					t('Logo Url'),
					);

	$query = db_select('DataSource');
	$query ->fields('DataSource', array('id','name', 'url', 'logourl')); //SELECT the fields from Data Source Table
	$results = $query-> execute();

	foreach ($results as $line) {
		$rows[$line->id] = array(   
				$line->name,
				$line->url,
				$line->logourl,
				);
	}


	//Checkboxes Setup
	$form['sources']['table'] = array (
			'#type' => 'tableselect',
			'#title' => 'Delete Source?',
			'#header' => $header,
			'#options' => $rows,
			'#weight' => 0,
			'#empty' => t('No Data Sources found'),
			'#js_select' => TRUE,
			);

	$form['sources']['delete'] = array (
			'#type' => 'submit',
			'#value' => t('Delete'),
			'#weight' => 1,
			'#submit' => array('datasource_delete'),
			);

	$form['sources']['edit'] = array (
			'#type' => 'submit',
			'#value' => t('Edit'),
			'#weight' => 1,
			'#submit' => array('datasource_edit'),
			);

	$data_source_boolean = variable_get('data_source_boolean', '');

	// Edit button clicked  
	if ($data_source_boolean == true){
		$name = variable_get('name','');  
		$url = variable_get('url', '');
		$logourl = variable_get('logourl','');  

	//Controls Data Source Name textbox field
		$form['sources']['name'] = array(
				'#title' => 'Insert Name of Source ',
				'#description' => 'e.g "The New York Times"',
				'#default_value' => $name,
				'#type' => 'textfield',
				'#size' => 40,
				'#weight' => 2,
				'#maxlength' => 255,
				);

		//Controls the attributes of the Data Sources textbox field
		$form['sources']['url'] = array(
				'#title' => 'Insert URL to Website',
				'#description' => 'e.g "http://www.nytimes.com"',
				'#default_value' => $url,
				'#type' => 'textfield',
				'#size' => 40,
				'#weight' => 3,
				'#maxlength' => 255,
				);

		//Controls the logo url
		$form['sources']['logo'] = array(
				'#title' => 'Insert Logo URL',
				'#description' => 'e.g "http://www.imgur.com/n7jh.jpg"',
				'#default_value' => $logourl,
				'#type' => 'textfield',
				'#size' => 40,
				'#weight' => 4,
				'#maxlength' => 255,
				);

		//The Update Button
		$form['sources']['update'] = array(
				'#type' => 'submit',
				'#value' => t('Update'),
				'#weight' => 6,
				'#submit' => array('datasource_update'),
				);

	}
	//initially data source boolean is false, hence set up as normal
	if ($data_source_boolean == false){ 

		//Controls Data Source Name textbox field
		$form['sources']['name'] = array(
				'#title' => 'Insert Name of Source ',
				'#description' => 'e.g "The New York Times"',
				'#default_value' => ' ',
				'#type' => 'textfield',
				'#size' => 40,
				'#weight' => 2,
				'#maxlength' => 255,
				);

		//Controls the attributes of the Data Sources textbox field
		$form['sources']['url'] = array(
				'#title' => 'Insert URL to article',
				'#description' => 'e.g "http://www.nytimes.com"',
				'#default_value' => ' ',
				'#type' => 'textfield',
				'#size' => 40,
				'#weight' => 3,
				'#maxlength' => 255,
				);

		//Controls the logo url
		$form['sources']['logo'] = array(
				'#title' => 'Insert Logo URL',
				'#description' => 'e.g "http://www.imgur.com/n7jh.jpg"',
				'#default_value' => ' ',
				'#type' => 'textfield',
				'#size' => 40,
				'#weight' => 4,
				'#maxlength' => 255,
				);


		//The Add Button
		$form['sources']['add'] = array(
				'#type' => 'submit',
				'#value' => t('Add'),
				'#weight' => 6,
				'#submit' => array('datasource_add'),
				);
	}
	return $form;
}

/**
 * Form for configuring Data Means
 */  
function datameans_form($form, $form_state, $i) {
	// rows initialization 
	$rows = array(); 

	// header setup
	$header = array
			(
					t('Name of Means'),
					t('Url'),
					t('Data Source'),
					);

	$query = db_select('DataMeans');
	$query ->fields('DataMeans', array('id','name', 'url', 'DataSource_id')); //SELECT the fields from Data Source Table
	$results = $query-> execute();


	//Data Source table query for the Data Means header setup
	foreach ($results as $line) {

		$queryA = db_select('DataSource', 'd');
		$queryA ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
		$queryA -> condition('id', $line->DataSource_id, '=');
		$result	= $queryA-> execute();


		$row = $result-> fetchAssoc();
		$theArticle = $row['name'];

		$rows[$line->id] = array(  
				$line->name,
				$line->url,
				$theArticle,
				);
	}
	// Adding means collapsable area
	$form['means'] = array(
			'#type' => 'fieldset',
			'#title' => t('Configure Data Means'),
			'#collapsible' => TRUE,
			'#collapsed' => TRUE,
			'#weight' => 5
			);

	// Checkboxes Setup
	$form['means']['table'] = array(
			'#type' => 'tableselect',
			'#title' => 'Delete Source?',
			'#header' => $header,
			'#options' => $rows,
			'#empty' => t('No Data Means found'),
			'#js_select' => FALSE,
			'#weight' => 0
			);
	//Edit Button
	$form['means']['edit'] = array (
			'#type' => 'submit',
			'#value' => t('Edit'),
			'#submit' => array('datameans_edit'),
			'#weight' => 1
			); 
			
	//Delete Button
	$form['means']['delete'] = array (
			'#type' => 'submit',
			'#value' => t('Delete'),
			'#submit' => array('datameans_delete'),
			'#weight' => 2
			);

	$data_means_boolean = variable_get('data_means_boolean',''); 

	if ($data_means_boolean == true){
		$name = variable_get('name','');
		$url = variable_get('url', '');
		$DataSource_id = variable_get('DataSource_id','');

		// Data Means Name textfield
		$form['means']['name'] = array(
				'#title' => 'Insert Name of Means  ',
				'#description' => 'e.g "CNET RSS Feed"', 
				'#default_value' => $name,
				'#type' => 'textfield',
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 3
				);
		//Controls the attributes of the Data Sources textbox field
		$form['means']['url'] = array(
				'#title' => 'Insert URL to Data Means ',
				'#description' => 'e.g "http://www.cnet.com/rss"', 
				'#default_value' => $url,
				'#type' => 'textfield',
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 4
				);

		$sources = array();
		$dataSourceKey = -1; // initialize variable to -1
		$query = db_select('DataSource', 'd');
		$query ->fields('d', array('id', 'name')); //SELECT the fields 'id' and 'name' from Data Source Table
		$result	= $query-> execute();
		
		//Search for the correct data source to use for editing datameans
		$loopCounter = 0; 
		while ($row = $result-> fetchAssoc()){
			if ($DataSource_id == $row['id']){
				$dataSourceKey = $loopCounter; 
			}
			$sources[$loopCounter] = t($row['name']);
			$loopCounter ++; 
		}
		$options = $sources;


		// Pick Chooser
		$form['means']['meansources'] = array(
				'#title' => 'Choose Original Data Source',
				'#type' => 'select',
				'#default_value' => $dataSourceKey,
				'#options' => $options,
				'#weight' => 5,
				); 
		
		//The Add Button
		$form['means']['update'] = array(
				'#type' => 'submit',
				'#value' => t('Update'),
				'#weight' => 8,
				'#submit' => array('datameans_update'),
				); 

	}

// Check the Global variable, false means that the edit button was NOT clicked
	if ($data_means_boolean == false){
		
		//Controls Data Means Name textbox field
		$form['means']['name'] = array(
				'#title' => 'Insert Name of Means  ',
				'#description' => 'e.g "CNET RSS Feed"', 
				'#default_value' => ' ',
				'#type' => 'textfield',
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 3
				);
		//Controls the attributes of the Data Sources textbox field
		$form['means']['url'] = array(
				'#title' => 'Insert URL to Data Means ',
				'#description' => 'e.g "http://www.cnet.com/rss"', 
				'#default_value' => ' ',
				'#type' => 'textfield',
				'#size' => 40,
				'#maxlength' => 255,
				'#weight' => 4
				);

		//Get the options for the Data Source chooser
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


		// Pick Chooser
		$form['means']['meansources'] = array(
				'#title' => 'Choose Original Data Source',
				'#type' => 'select',
				'#default_value' => ' ',
				'#options' => $options,
				'#weight' => 5,
				); 
		
		//The Add Button
		$form['means']['add'] = array(
				'#type' => 'submit',
				'#value' => t('Add'),
				'#weight' => 8,
				'#submit' => array('datameans_add'),
				);
	}
	return $form;
}

/**
 * Update Handler for DataMeans configuration form
 */
function datameans_update($form, &$form_state){
	$source_name = $form_state['values']['name'];
	$source_url = $form_state['values']['url'];
	$dataSourceKey = $form_state['values']['meansources'];
	$source_source = $form['means']['meansources']['#options'][$dataSourceKey]; // New York Times Source

	// Error Handling
	if ($source_url == NULL || $source_name == NULL || $dataSourceKey == NULL ||
			$source_url == ' ' || $source_name == ' ' || $source_source == ' '){
		drupal_set_message(t('Unable to update data means. Please review entries into input fields.'), $type = 'error');
	}

	else{
		//Get the particular datafusion element being edited 
		$theDataMeansElement = variable_get('theDataMeansElement', '');

		//Query datasources table for the correct Data source Id associated with the data fusion element
		$query = db_select('DataSource', 'd');
		$query -> fields('d', array('id', 'name'));
		$query -> condition('name', $source_source, '=');
		$result	= $query-> execute();

		$row = $result-> fetchAssoc();

		$the_source = $row['id'];

		//Query Database for information	
		$num_deleted = db_update('DataMeans')
				->fields(array('name' => $source_name,'url' => $source_url, 'DataSource_id' => $the_source))
				->condition('id', $theDataMeansElement)
				->execute();  

				drupal_set_message(t('Data Means has been updated'));
				variable_set('data_means_boolean', false); //set flag boolean back to false, finished editing	
	}


}

/**
 * Edit Handler for DataMeans configuration form
 */
function datameans_edit($form, &$form_state){
	$dataInfo = array();
	$dataInfo = array_filter($form_state['values']['table']);	

	//Only 1 element can be selected at a time.
	if(count($dataInfo) > 1){
		drupal_set_message(t('Must only select one row to be edited at a time.'), $type = 'error');
	}
	// No elements selected
	if ($dataInfo == NULL){
		drupal_set_message(t('Please select a row to edit'), $type = 'error');
	}
	// If only one element is checked off then perform as usual
	if (count($dataInfo) == 1){

		foreach ($dataInfo as $row) {

			//Get the data
			$query = db_select('DataMeans', 'd');
			$query ->fields('d', array('id', 'name', 'url', 'DataSource_id')); //SELECT the fields from Data Fusion Table
			$query ->condition('id', $row);
			$result	= $query-> execute();

			$row = $result-> fetchAssoc();

			$theDataMeansElement = $row['id'];


			// Creating the global variables for the edit button
			$name = ' ';
			variable_set('name', $row['name']);
			$url = '';
			variable_set('url', $row['url']);
			$DataSource_id = 0;
			variable_set('DataSource_id', $row['DataSource_id']);

			// Keep track of which data source element was selected to be edited
			variable_set('theDataMeansElement', $theDataMeansElement);

			// Setting form boolean back to default
			$data_means_boolean = true;
			variable_set('data_means_boolean', true); 
		}
	} 	
}

/**
 *  Handler for deleting Data means entry
 */
function datameans_delete($form, &$form_state){
	$dfInfo = array();
	$dfInfo = array_filter($form_state['values']['table']);

	// Error handling for no elements selected
	if ($dfInfo == NULL){
		drupal_set_message(t('Please select an element to delete'), $type = 'error');
	}
	
if ($dfInfo != NULL){
	foreach (($form_state['values']['table']) as $rows) {

		//Data means for source is also deleted
		$dum_deleted = db_delete('DataMeans')
				->condition('id', $rows)
				->execute();  
	}
	drupal_set_message (t('Data Means Deleted!'));

	}
}
/**
 *  Add Data means to database
 */
function datameans_add($form, &$form_state){
	
	//Get input from fields
	$means_name = $form_state['values']['name'];
	$means_url = $form_state['values']['url'];
	$chooserKey = $form_state['values']['meansources']; //Gets Key from the select box
	$source_chooser = $form['means']['meansources']['#options'][$chooserKey]; // Get the string value from select box

	//Error Handling
	if ($means_url == NULL || $means_name == NULL || $chooserKey == NULL || $source_chooser == NULL || 
			$means_url == ' ' || $means_name == ' '){

		drupal_set_message(t('Unable to add to the database. Please review entries into input fields.'), $type = 'error');
	}

	else{
		//DataSource table in database query
		$query = db_select('DataSource', 'd');
		$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
		$query  ->condition('name', $source_chooser,'=');
		$result	= $query-> execute();
		$row = $result-> fetchAssoc();

		$dbID = $row['id'];
	    $currentDate = date("Y-m-d H:i:s", time()-(3600*24)); // Get Current Time one day before
	
		
		$query = db_insert('DataMeans') 
				->fields(array(
						'DataSource_id' => $dbID,
						'url' =>  $means_url,
						'name' => $means_name,
						'type' => 0, //Type is a mystery
						'lastProcessed' => $currentDate, 
						))
						->execute();  

						// Success message
						drupal_set_message(t('Successfully added to the Database'));
	}

}


/**
 * Update Handler for DataSource configuration form
 */   

function datasource_update($form, &$form_state){
	$source_name = $form_state['values']['name'];
	$source_url = $form_state['values']['url'];
	$source_logourl = $form_state['values']['logo'];

	// Error Handling
	if ($source_url == NULL || $source_name == NULL || $source_logourl == NULL ||
			$source_url == ' ' || $source_name == ' ' || $source_logourl == ' '){
		drupal_set_message(t('Unable to update data source. Please review entries into input fields.'), $type = 'error');
	}

	else{
		
		// Get the particular datafusion element
		$theDataSourceElement = variable_get('theDataSourceElement', '');


		// Query Database for information	
		$query = db_update('DataSource')
				->fields(array('name' => $source_name,'url' => $source_url, 'logourl' => $source_logourl))
				->condition('id', $theDataSourceElement)
				->execute();  

				drupal_set_message(t('Data Source has been updated'));

				variable_set('data_source_boolean', false); //set flag boolean back to false, finished editing	
	}

}
/**
 * Edit Handler for DataSource configuration form
 */

function datasource_edit($form, &$form_state){	
	$dataInfo = array();
	$dataInfo = array_filter($form_state['values']['table']);	

	// Only 1 element can be selected at a time.
	if(count($dataInfo) > 1){
		drupal_set_message(t('Must only select one row to be edited at a time.'), $type = 'error');
	}
	// No elements selected
	if ($dataInfo == NULL){
		drupal_set_message(t('Please select a row to edit'), $type = 'error');
	}
	// If only one element is checked off proceed as normal
	if (count($dataInfo) == 1){

		foreach ($dataInfo as $row) {

			//Get the data from the tables
			$query = db_select('DataSource', 'd');
			$query ->fields('d', array('id', 'name', 'url', 'logourl')); //SELECT the fields from Data Fusion Table
			$query ->condition('id', $row);
			$result	= $query-> execute();

			$row = $result-> fetchAssoc();

			$theDataSourceElement = $row['id'];


			//Store fields in global variables
			$name = ' ';
			variable_set('name', $row['name']);
			$url = '';
			variable_set('url', $row['url']);
			$logourl = '';
			variable_set('logourl', $row['logourl']);


			//keep track of which data source element was selected to be edited
			variable_set('theDataSourceElement', $theDataSourceElement);

			// Setting boolean representing edit button has been clicked to true 
			$data_source_boolean = true;
			variable_set('data_source_boolean', true); 
		}
	} 
}

function datasource_delete($form, &$form_state){
	$dfInfo = array();
	$dfInfo = array_filter($form_state['values']['table']);

	// Error handling for no elements selected
	if ($dfInfo == NULL){
		drupal_set_message(t('Please select an element to delete'), $type = 'error');
	}
	
if ($dfInfo != NULL){	
	foreach (($form_state['values']['table']) as $rows) {
		$num_deleted = db_delete('DataSource')
				->condition('id', $rows)
				->execute(); 

				//Data means for source is also deleted
				$dum_deleted = db_delete('DataMeans')
						->condition('DataSource_id', $rows)
						->execute();  
	}
	drupal_set_message (t('Data Source and connected Data Fusion Articles Deleted!'));
	}
}

/**
 * Add function callback for element into DataSource table
 */
function datasource_add($form, &$form_state){
	$source_name = $form_state['values']['name'];
	$source_url = $form_state['values']['url'];
	$source_logo = $form_state['values']['logo'];

	// WORK ON USER PERMISSIONS!
	if ($source_url == NULL || $source_name == NULL || $source_logo == NULL || $source_url == ' ' || $source_name == ' ' || $source_logo == ' '){
		drupal_set_message(t('Unable to add to the database. Please review entries into input fields.'), $type = 'error');
	}

	else{
		$query = db_insert('DataSource') 
				->fields(array(
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
 * Delete Handler for DataFusion Element 
 */
function datafusion_delete($form, &$form_state) {
	$dfInfo = array();
	$dfInfo = array_filter($form_state['values']['table']);

	// Error handling for no elements selected
	if ($dfInfo == NULL){
		drupal_set_message(t('Please select an element to delete'), $type = 'error');
	}
if ($dfInfo != NULL){	
	foreach (($form_state['values']['table']) as $rows) {
		
		// Mark as deleted
		$num_deleted = db_update('DataFusion')
				->fields(array('approved' => 0, 'deleted' => 1)) 
				->condition('id', $rows)
				->execute();  
	}
	drupal_set_message(t('Data Fusion Element[s] Marked as Deleted, meaning it will NOT be displayed alongside article '));
}
}

/**
 * Approve Handler for DataFusion Element
 */
function datafusion_approve($form, &$form_state) {
	//	$data_nodes = $form_state['values']['nodes'];
	//	$article_name= $form['nodes']['#options'][$data_nodes];
	
	$dfInfo = array();
	$dfInfo = array_filter($form_state['values']['table']);

	// Error handling for no elements selected
	if ($dfInfo == NULL){
		drupal_set_message(t('Please select an element to delete'), $type = 'error');
	}
if ($dfInfo != NULL){ 	
	foreach (($form_state['values']['table']) as $rows) {
		//Mark as approved
		$num_deleted = db_update('DataFusion')
				->fields(array('approved' => 1, 'deleted' => 0)) 
				->condition('id', $rows)
				->execute();  
	}
	drupal_set_message(t('Data Fusion Element[s] Marked as Approved to be displayed alongside articles '));
}
}

/**
 * Add a data fusion source
 */
function datafusion_add($form, &$form_state) {
	// Get User Input from form fields
	$source_title = $form_state['values']['title'];
	$source_url = $form_state['values']['url'];
	$source_summary = $form_state['values']['summary'];
	$source_ratings = $form_state['values']['ratings']; 
	$dataKey = $form_state['values']['chooser'];
	$source_chooser = $form['addfusion']['chooser']['#options'][$dataKey]; // Get the string value from Data source select box
	$data_nodes = $form_state['values']['nodes'];
	$source_node = $form['addfusion']['nodes']['#options'][$data_nodes]; // Gets the string value of the article select

	// Get the Data Source ID
	$query = db_select('DataSource', 'd');
	$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
	$query ->condition('name', $source_chooser, '=');
	$result1 = $query-> execute();
	$row = $result1-> fetchAssoc();

	$dbID = $row['id'];
	
	// Get the article
	
	$queryA = db_select('node', 'n');
	$queryA -> fields('n', array('nid', 'title'));
	$queryA -> condition('title', $source_node, '=');
	$myresult = $queryA -> execute();
	$rowA = $myresult -> fetchAssoc();
	$theNode = $rowA['nid'];
	
	// Error Handling
	if ($source_url == NULL || $source_title == NULL || $source_summary == NULL || $source_node == NULL || 
			$source_url == ' ' || 
			$source_summary == ' ' || 
			$data_nodes == ' ' ||
			$source_ratings == ' ' ||
			$source_title == ' '){
		drupal_set_message(t('Unable to add to the database. Please review entries into input fields.'), $type = 'error');
	}

	else{ 
		// Get Current Time
		$time = date("Y-m-d H:i:s", time());	
		$query = db_insert('DataFusion') 
				->fields(array(
						'nodeID' => $theNode,
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
 * Edit handler for DataFusion form
 */
function datafusion_edit($form, &$form_state){

	$dfInfo = array();
	$dfInfo = array_filter($form_state['values']['table']);

	//Only 1 element can be selected at a time.
	if(count($dfInfo) > 1){
		drupal_set_message(t('Must only select one row to be edited at a time.'), $type = 'error');
	}
	// No elements selected
	if ($dfInfo == NULL){
		drupal_set_message(t('Please select a row to edit'), $type = 'error');
	}
	//One Element was selected
	if (count($dfInfo) == 1){

		foreach ($dfInfo as $row) {

			// DataFusion table query
			$query = db_select('DataFusion', 'd');
			$query ->fields('d', array('id', 'nodeID', 'title', 'url', 'summary', 'rating', 'DataSource_id')); //SELECT the fields from Data Fusion Table
			$query ->condition('id', $row);
			$result	= $query-> execute();

			$row = $result-> fetchAssoc();
			$theDataFusionElement = $row['id'];

			// Get the Data Source ID name
			$queryA = db_select('DataSource', 'd');
			$queryA ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
			$queryA  ->condition('id', $row['DataSource_id'],'=');
			$resultA = $queryA-> execute();
			$rowA = $resultA-> fetchAssoc();
			$theSource = $rowA['name'];

			// Get the article's name to be matched for the Article Node Chooser in the Data Fusion Table
			$queryB = db_select('node', 'n');
			$queryB -> fields('n', array('nid','title'));
			$queryB -> condition('nid', $row['nodeID'], '=');
			$resultB  =$queryB-> execute();
			$rowB= $resultB-> fetchAssoc();
			$theArticle = $rowB['title'];

			// Create global variables to be used as input for editing fields
			$title = ' ';
			variable_set('title', $row['title']);
			$url = '';
			variable_set('url', $row['url']);
			$summary = '';
			variable_set('summary', $row['summary']);
			$rating = -1; // Initialize rating
			variable_set('rating', $row['rating']);
			$dataSource= '';
			variable_set('dataSource', $theSource);
			$articleNodeName = '';
			variable_set('articleNodeName', $theArticle);

			//keep track of which data fusion element was selected to be edited
			variable_set('theDataFusionElement', $theDataFusionElement);

			// Set global flag boolean to true representing the edit button has been clicked
			$term = true;
			variable_set('term', true); 
		} 
	} 
}



/**
 * Update handler for updating Data Fusion table entry in database 
 */
function datafusion_update($form, &$form_state){

	// Obtain the information input to textfields and from 'select' form element 
	$source_title = $form_state['values']['title'];
	$source_url = $form_state['values']['url'];
	$source_summary = $form_state['values']['summary'];
	$source_ratings = $form_state['values']['ratings']; 
	$data_source = $form_state['values']['chooser'];
	$source_chooser = $form['chooser']['#options'][$data_source]; // Get the Data Source string value from 'select' form element
	$article_node = $form_state['values']['nodes'];
	$article_chooser = $form['nodes']['#options'][$article_node]; //Get the Article Node string value from 'select' form element
	
	// Error Handling - Validating inputs 
	if ($source_url == NULL || $source_title == NULL || $source_summary == NULL || $source_chooser == NULL || $data_source == NULL || $article_node == NULL || $source_url == ' ' || $source_summary == ' ' || $source_title == ' '){
		drupal_set_message(t('Unable to update data fusion article. Please review entries into input fields.'), $type = 'error');
	}

	else {
		// Get the particular datafusion element checked to be edited
		$theDataFusionElement = variable_get('theDataFusionElement', '');

		// Change information in the database
		$query = db_select('DataSource', 'd');
		$query ->fields('d', array('id', 'name')); //SELECT the fields from Data Source Table
		$query  ->condition('name', $source_chooser,'=');
		$result	= $query-> execute();
		$row = $result-> fetchAssoc();	
		$dbID = $row['id'];

		// Get the corresponding nodeID with the one chosen in the 'select' form element from query to node table
		$node_query = db_select('node','n');
		$node_query ->fields('n', array('nid','title'));
		$node_query -> condition('title', $article_chooser, '=');
		$resultA = $node_query->execute();
		$rowA = $resultA -> fetchAssoc(); 

		$nodeID = $rowA['nid']; 

		// Update element in datafusion table's fields	
		$data_fusion_query = db_update('DataFusion')
				->fields(array('title' => $source_title,'nodeID' => $nodeID, 'url' => $source_url, 'summary' => $source_summary, 'rating' => $source_ratings, 'DataSource_id' => $dbID))
				->condition('id', $theDataFusionElement)
				->execute();  
				drupal_set_message(t('Data Fusion Article has been updated'));
				variable_set('term', false); //set global flag boolean back to false representing the editing has finished

	}
}

/**
 * Data Fusion Form Validation Handler
 */
function datafusion_form_validate($form, &$form_state){
	$errors = drupal_get_messages(); // (dpm()'s has to go AFTER this line--they get cleared)
	foreach ($errors as $type => $id) {
		foreach ($id as $message){
			// Loop through individual messages, looking for ones to remove or replace
			if (test_for_invalid_error($message)===FALSE){
				drupal_set_message($message,$type);
			} elseif (test_for_invalid_error($message)!==TRUE){
				drupal_set_message(test_for_invalid_error($message), $type);
			}
		}
	}
}

function test_for_invalid_error($message){
	if (strpos($message, 'An illegal choice has been detected. Please contact the site administrator.') !== false) {
		return t('Error, Please Retry Configuration of DataFusion/DataSources/DataMeans!');
	}
	return FALSE;
}


/**
 * Data Source Form Validation Handler
 */
function datasource_form_validate($form, &$form_state){
	$errors = drupal_get_messages(); // (dpm()'s has to go AFTER this line--they get cleared)
	foreach ($errors as $type => $id) {
		foreach ($id as $message){
			// Loop through individual messages, looking for ones to remove or replace
			if (test_for_invalid_error($message)===FALSE){
				drupal_set_message($message,$type);
			} elseif (test_for_invalid_error($message)!==TRUE){
				drupal_set_message(test_for_invalid_error($message), $type);
			}
		}
	}
}

/**
 * Data Means Form Validation Handler
 */
function datameans_form_validate($form, &$form_state){
	$errors = drupal_get_messages(); // (dpm()'s has to go AFTER this line--they get cleared)
	foreach ($errors as $type => $id) {
		foreach ($id as $message){
			// Loop through individual messages, looking for ones to remove or replace
			if (test_for_invalid_error($message)===FALSE){
				drupal_set_message($message,$type);
			} elseif (test_for_invalid_error($message)!==TRUE){
				drupal_set_message(test_for_invalid_error($message), $type);
			}
		}
	}
}
