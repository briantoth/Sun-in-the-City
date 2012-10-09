package sun.datafusion.index;

/*******************************************************************************
 * The DataRetriever executes MySQL queries to obtain DataPrimary and
 * DataSecondary instances that have not yet been indexed by Apache Lucene. This
 * class uses a Thread to buffer the instances so that they are ready the second
 * the DataIndexer is ready to index a data instance.
 */
public class DataRetriever extends Thread {

	/***************************************************************************
	 * Constructor that initializes the parameters, the MySQL connection
	 * details, and starts the buffer thread
	 */
	public DataRetriever() {
		// TODO
	}

	/***************************************************************************
	 * Gets a buffered DataSource object that was loaded from the MySQL tables.
	 * This function blocks until a source can be obtained. After indexing the
	 * returned source, the setDataSourceIndexed function should be called
	 * 
	 * @return A DataSource that needs to be indexed
	 */
	public DataSource getUnindexedDataSource() {
		// TODO
		return null;
	}

	/***************************************************************************
	 * Saves to the MySQL database that the given DataSource has been Indexed.
	 * This function should be called immediatedly after the source has been
	 * indexed.
	 * 
	 * @param source
	 *            The source that has been indexed
	 */
	public void setDataSourceIndexed(DataSource source) {
		// TODO
	}

	/***************************************************************************
	 * Execution thread used to query the MySQL database and buffer the data
	 * instances
	 */
	public void run() {
		// TODO
	}
}
