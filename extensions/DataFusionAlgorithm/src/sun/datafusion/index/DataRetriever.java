package sun.datafusion.index;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.lucene.store.Directory;

import sun.datafusion.Main;
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
	
	private final DataStoredTable dataStoredTable;
	private final ExecutorService threadPool= Executors.newFixedThreadPool(numThreads);
	private final Directory indexLocation;
	
	/***************************************************************************
	 * Constructor that initializes the parameters, the MySQL connection
	 * details, and starts the buffer thread
	 * @param indexLocation 
	 */
	public DataRetriever(Properties prop, Directory indexLocation) {
		
		DataStoredTable dst= null;
		try {
			dst= new DataStoredTable(prop);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dataStoredTable= dst;
		}
		
		this.indexLocation= indexLocation;
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
				Runnable dataIndexer= new DataIndexer(ds, dataStoredTable, indexLocation);
				threadPool.execute(dataIndexer);
			}
		}
		
		threadPool.shutdown();
	}
}
