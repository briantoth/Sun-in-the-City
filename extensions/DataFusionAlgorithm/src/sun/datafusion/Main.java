package sun.datafusion;

/*******************************************************************************
 * Class that holds the program configurations as well as the main function
 */
public class Main {

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
		// TODO

		// Check for properties file
		// If none exists, create one and store defaults
		// If one exists, load properties and do bounds checking

		// Startup a DataRetriever
		// Startup N DataIndexers and pass them the DataRetriever

		// Startup an ArticleRetriever
		// Startup N DataFusers and pass them the ArticleRetriever

		// Setup the shutdown hook (graceful kill)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutting down...");
				// TODO
			}
		});

		// Join on all threads (sleep until quit)
	}

}
