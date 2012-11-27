<?php
/**
 * @file
 * Zen theme's implementation to display a sidebar region.
 *
 * Available variables:
 * - $content: The content for this region, typically blocks.
 * - $classes: String of classes that can be used to style contextually through
 *   CSS. It can be manipulated through the variable $classes_array from
 *   preprocess functions. The default values can be one or more of the following:
 *   - region: The current template type, i.e., "theming hook".
 *   - region-[name]: The name of the region with underscores replaced with
 *     dashes. For example, the page_top region would have a region-page-top class.
 * - $region: The name of the region variable as defined in the theme's .info file.
 *
 * Helper variables:
 * - $classes_array: Array of html class attribute values. It is flattened
 *   into a string within the variable $classes.
 * - $is_admin: Flags true when the current user is an administrator.
 * - $is_front: Flags true when presented in the front page.
 * - $logged_in: Flags true when the current user is a logged-in member.
 *
 * @see template_preprocess()
 * @see template_preprocess_region()
 * @see zen_preprocess_region()
 * @see template_process()
 */
?>
<?php if ($content): ?>
  <section class="<?php print $classes; ?>">
<?php 
// Establishing connection to database
 db_set_active('default'); 

// Obtain the node ID for the particular page being viewed by using the url
 $argument_one = arg(0);
 $node_id = arg(1); 
 
 // Initialization of array for node fusion block
 $fusion = array(); 
 
 // Show only data fusion articles corresponding to that particular Article
    $query = db_select('DataFusion', 'd');
	$query ->fields('d', array('nodeID', 'title', 'summary', 'approved', 'DataSource_id', 'rating', 'url')); 
	$query ->condition('nodeID', $node_id, '=');
	$query ->condition('approved', 1, '=');
	$query ->orderBy('rating', $direction = 'DESC'); // Descending direction allows for making articles with higher ratings appear higher up in the list of articles
	$result	= $query-> execute();
	
	$loopCounter = 0; //start the loop
	
	while ($row = $result-> fetchAssoc()){
	//Get the Logo image	
	$query1 = db_select('DataSource', 'd');
	$query1 ->fields('d', array('id', 'logourl')); 
	$query1 ->condition('id', t($row['DataSource_id']), '='); // Check to see the right data source id is being selected
	$result1 = $query1-> execute();
	
	$row1 = $result1 -> fetchAssoc(); // Go through the results  row by row
		$fusion[$loopCounter] = array(t($row1['logourl']), t($row['title']), t($row['summary']), t($row['url'])); // Information to be added to the block
		$loopCounter ++; 
	}
	
	//Check to see if there are any Data Fusion articles
	if (($fusion)) {
		echo "<div id = 'fusion'>
		<h2>Around the Web</h2>";
	
        foreach ( $fusion as $v ) {
          ?><div class = "fusionarticle"><?php echo "
          <div class = \"fusiontext\">
          	<h3><a href='{$v[3]}'>{$v[1]}</a></h3>
          	<p>{$v[2]}</p>
          </div>
          <p class = 'fusionlogop'><a href='{$v[3]}'><img src='{$v[0]}' alt = 'logo' class = 'fusionlogoimg' /></a></p>
          "; ?></div>
          <?php
        }
        echo "</div>";
     }

      ?>





    <?php print $content; ?>
  </section><!-- region__sidebar -->
<?php endif; ?>
