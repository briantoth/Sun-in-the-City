package sun.datafusion.index;

/*******************************************************************************
 * This class takes DataSources from the DataRetriever and uses Apache Lucene to
 * index the data. There will be many DataIndexers running in separate threads
 * as the index process is time consuming. Note that the DataRetriever object
 * will block until DataSource objects are available.
 * 
 */
public class DataIndexer extends Thread {

	/***************************************************************************
	 * Creates a DataIndexer with the given retriever and starts the processing
	 * thread.
	 * 
	 * @param retriever
	 *            The retriever to get DataSources from to process
	 */
	public DataIndexer(DataRetriever retriever) {
		// TODO
	}

	/***************************************************************************
	 * Processing thread that repeatedly gets DataSources from the DataRetriever
	 * and uses Apache Lucene to index them.
	 */
	public void run() {
		// TODO
	}
}
