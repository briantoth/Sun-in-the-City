package sun.datafusion;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import sun.datafusion.data.Manager;
import sun.datafusion.fuse.ArticleRetriever;
import sun.datafusion.index.DataCleaner;
import sun.datafusion.index.DataRetriever;
import sun.datafusion.utils.PropertyUtils;

/*******************************************************************************
 * Class that holds the program configurations as well as the main function
 */
public class Main {
	
	static long CLEAN_TIMEOUT;
	
	private static final String indexStoreDir= "./LuceneIndex";
	
	public volatile static boolean keepRunning = true;
	private static Manager manager;
	
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
		
		Properties prop = sun.datafusion.utils.PropertyUtils.loadProperties();
		sun.datafusion.utils.PropertyUtils.loadLoggingProperties();
		CLEAN_TIMEOUT = Long.parseLong(prop.getProperty("clean_timeout", "604800000")); //default is a week (604800000 ms)
		
		final Directory indexLocation;
		Directory tempIndexLocation=null;
		File indexFile= new File(indexStoreDir);
		
		if(!indexFile.exists()){
			indexFile.mkdir();
		}
		
		try {
			 tempIndexLocation= SimpleFSDirectory.open(indexFile);
		} catch (IOException e1) {
		} finally {
			indexLocation= tempIndexLocation;
		}
		
		manager= new Manager(prop);
				
		// Startup a DataRetriever
		final Thread dataRetriever= new Thread(new DataRetriever(prop, indexLocation, manager));
		dataRetriever.start();

		// Startup an ArticleRetriever
		final Thread articleRetriever= new Thread(new ArticleRetriever(indexLocation, manager));
		articleRetriever.start();
		
		// Initiate a DataCleaner
		DataCleaner dc = new DataCleaner(indexStoreDir);

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
				
				manager.close();
			}
		});
		
		while(true){
			try {
				Thread.sleep(CLEAN_TIMEOUT);
				dc.clearLuceneIndex(); 
				manager.clearDataStored(CLEAN_TIMEOUT);
			} catch (InterruptedException e) {
			}
		}
		
	}
	

}
