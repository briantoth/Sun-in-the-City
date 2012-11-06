package sun.datafusion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import java.sql.*;

import sun.datafusion.fuse.ArticleRetriever;
import sun.datafusion.index.DataIndexer;
import sun.datafusion.index.DataRetriever;

/*******************************************************************************
 * Class that holds the program configurations as well as the main function
 */
public class Main {
	
	public static final String configFile= "config.properties";
	public static volatile boolean keepRunning = true;

	/***************************************************************************
	 * Main entry function for the program. First it checks for a properties
	 * file. If none exists it creates one with the default settings. Once
	 * properties are determined, it sets up the Indexing and Fusing operations.
	 * The Indexing operation has a data retriever that gets the data from a
	 * MySQL database to buffer data to be used by the indexer. Only one is
	 * initialized. The main thread then launches a variable number of Indexers,
	 * configured by the properties, and these indexers take care of processing
	 * the data into an Apache Lucene Index. The main function also launches the
	 * Fusion operation. The fusion operation has one article retriever which
	 * gets the articles from the Drupal MySQL database and buffers it for the
	 * Fuser. The Fuser then takes the content of this article and executes an
	 * Apache Lucene search on the Index to find relevant data sources. The
	 * Fuser saves its results back to the MySQL database. Finally, the program
	 * registers a shutdown hook to handle kill (SIGINT) requests.
	 * 
	 * @param args
	 *            The command line arguments passed to the program. Ignored
	 */
	public static void main(String[] args) {
		Properties prop= new Properties();
		
		// Check for properties file
		// If none exists, create one and store defaults
		//TODO: If one exists, load properties and do bounds checking
		File f = new File(configFile);
		if(f.exists()){
			try {
				prop.load(new FileInputStream(configFile));			
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
	    		prop.setProperty("db", "sun_in_the_city");
	    		prop.setProperty("dbuser", "root");
	    		prop.setProperty("dbpassword", "");
	 
	    		//save properties to project root folder
	    		prop.store(new FileOutputStream("config.properties"), null);
	 
	    	} catch (IOException ex) {
	    		ex.printStackTrace();
	        }
		}
		Connection con = null;
		try {
			con= DriverManager.getConnection(
					prop.getProperty("connUrl")+"sun_in_the_city",
					prop.getProperty("dbuser"),
					prop.getProperty("dbpassword"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Statement stmt = null;
		try {
			stmt= con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			stmt.executeQuery("select * from DataMeans");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet rs= null;
		try {
			rs = stmt.getResultSet();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			while(rs.next()){
				System.out.println(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Startup a DataRetriever
		final Thread dataRetriever= new Thread(new DataRetriever(prop));
		dataRetriever.start();

		// Startup an ArticleRetriever
		final Thread articleRetriever= new Thread(new ArticleRetriever());
		articleRetriever.start();

		// Setup the shutdown hook (graceful kill)
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutting down...");
				
				keepRunning= false;
				
				dataRetriever.interrupt();
				articleRetriever.interrupt();
				
				// Join on all threads (sleep until quit)
				try {
					dataRetriever.join();
					articleRetriever.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
