package sun.datafusion.index;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sqlreadwrite.SourceTable;
import sun.datafusion.Main;
import sun.datafusion.data.DataSource;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.DataStoredTable;

/*******************************************************************************
 * The DataRetriever executes MySQL queries to obtain DataStored
 * instances that have not yet been indexed by Apache Lucene. This
 * class uses a Thread to buffer the instances so that they are ready the second
 * the DataIndexer is ready to index a data instance.
 */
public class DataRetriever implements Runnable{
	private final static long timeToSleep = 60*1000*1;
	private final static int numThreads= 5;
	
	private DataStoredTable dataStoredTable= null;
	private ExecutorService threadPool= Executors.newFixedThreadPool(numThreads);
	
	/***************************************************************************
	 * Constructor that initializes the parameters, the MySQL connection
	 * details, and starts the buffer thread
	 */
	public DataRetriever(Properties prop) {
		try {
			dataStoredTable= new DataStoredTable(prop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * Execution thread used to query the MySQL database and buffer the data
	 * instances
	 */
	public void run() {
		while(Main.keepRunning){
			//wait for a bit
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e) {
			}
			
			List<DataStored> unindexedData;
			
			try {
				 unindexedData= dataStoredTable.getUnindexedData();
			} catch (SQLException e) {
				continue;
			}
			
			for(DataStored ds : unindexedData){
				Runnable dataIndexer= new DataIndexer(dataStoredTable);
				threadPool.execute(dataIndexer);
			}
		}
		
		threadPool.shutdown();
	}
}
