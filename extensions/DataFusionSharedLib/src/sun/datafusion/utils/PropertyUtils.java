package sun.datafusion.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

/**
 * Some useful configuration and logging methods available across the various packages
 * @author Brian
 *
 */
public class PropertyUtils {
	private static final String configFile= "../config.properties";
	private static final String loggingConfigFile = "../logging.properties";
	private static Properties prop= null;
	
	/**
	 * @return A map containing all of the user-configured properties
	 */
	public static Properties loadProperties(){
		
		if(prop != null)
			return prop;
		
		prop= new Properties();
		
		// Check for properties file
		// If none exists, create one and store defaults
		//TODO: If one exists, load properties and do bounds checking
		File f = new File(configFile);
		
		if(f.exists()){
			try {
				prop.load(new FileInputStream(configFile));			
				System.out.println("db="+prop.getProperty("hostname"));
				System.out.println("db="+prop.getProperty("db"));
				System.out.println("user="+prop.getProperty("dbuser"));
				System.out.println("pass="+prop.getProperty("dbpassword"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			try {
				//set the properties value
				prop.setProperty("hostname", "localhost");
				prop.setProperty("db", "sun_in_the_city");
				prop.setProperty("dbuser", "root");
				prop.setProperty("dbpassword", "");
				prop.setProperty("reader_timeout", "1000000");
				prop.setProperty("clean_timeout", "604800000"); // a week
				prop.setProperty("nodeTable", "node");
				prop.setProperty("dataMeansTable", "DataMeans");
				prop.setProperty("dataFusionTable", "DataFusion");
				prop.setProperty("dataSourceTable", "DataSource");
				prop.setProperty("dataStoredTable", "DataStored");
				prop.setProperty("taxonomyTable", "taxonomy_term_data");
				prop.setProperty("taxonomyIndex", "taxonomy_index");
				prop.setProperty("taxonomyHierarchy", "taxonomy_term_hierarchy");

				//save properties to project root folder
				prop.store(new FileOutputStream(configFile), null);

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return prop;
	}
	
	/**
	 * Loads the logging file from the configured location
	 */
	public static void loadLoggingProperties(){
		PropertyConfigurator.configure(loggingConfigFile);
	}
}
