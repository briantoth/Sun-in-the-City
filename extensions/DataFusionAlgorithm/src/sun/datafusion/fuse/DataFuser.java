package sun.datafusion.fuse;

/*******************************************************************************
 * Performs the DataFusion operation. This class continuously gets an Article to
 * perform DataFusion on and uses Apache Lucene to search for relevant
 * DataPrimary and DataSecondary sources. Upon finding a good match it stores
 * the result in the MySQL database for approval by the editors.
 */
public class DataFuser extends Thread {

	/***************************************************************************
	 * Constructor that initializes the Fusion operation and starts the
	 * processing thread.
	 */
	public DataFuser() {
		// TODO
	}

	/***************************************************************************
	 * Processing thread that performs the Fusion operation
	 */
	public void run() {
		// TODO
	}
}
