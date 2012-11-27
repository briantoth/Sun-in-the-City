package sun.datafusion.index;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.lucene.store.Directory;

import sun.datafusion.Main;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.Manager;

/**
 * Periodically gets unindexed data sources a spawns a dataFuser to deal with each of them
 *
 */
public class DataRetriever implements Runnable{
	private final static long timeToSleep = 1000*1*60;
	private final static int numThreads= 5;
	
	private final ExecutorService threadPool= Executors.newFixedThreadPool(numThreads);
	private final Directory indexLocation;
	private final Manager manager;
	
	/**
	 * Constructor that initializes the parameters, 
	 * details, and starts the buffer thread
	 * @param prop
	 * @param indexLocation
	 * @param manager
	 */
	public DataRetriever(Properties prop, Directory indexLocation, Manager manager) {
		
		this.manager= manager;
		this.indexLocation= indexLocation;
	}

	/***************************************************************************
	 * Execution thread used to query the MySQL database and buffer the data
	 * instances
	 */
	@Override
	public void run() {
		while(Main.keepRunning){
			//wait for a bit
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e) {
				break;
			}
			
			List<DataStored> unindexedData= manager.getDataStoredToIndex();
			
			for(DataStored ds : unindexedData){
				Runnable dataIndexer= new DataIndexer(ds, indexLocation, manager);
				threadPool.execute(dataIndexer);
			}
		}
		
		threadPool.shutdown();
	}
}
