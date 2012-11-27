package sun.datafusion.index;

import java.io.File;


/*******************************************************************************
 * This class clear index and unused DataStored automatically
 * 
 */
public class DataCleaner {
	private final String indexStoreDir;

	/***************************************************************************
	 * Creates a DataCleaner with the given directory and starts the processing
	 * thread.
	 * 
	 * @param indexStoreDir
	 *            The index directory
	 */

	public DataCleaner(String indexStoreDir) {
		this.indexStoreDir= indexStoreDir;
	}

	/***************************************************************************
	 * Clear Lucene Index
	 */
	public void clearLuceneIndex() {
		
		File indexFile= new File(indexStoreDir);
		
		DataIndexer.clearIndex(indexFile);
		
	}
}
