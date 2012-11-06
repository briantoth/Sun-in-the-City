package sun.datafusion.fuse;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sun.datafusion.Main;
import sun.datafusion.data.Node;

/*******************************************************************************
 * Obtains articles that need DataFusion operations to be performed. It checks
 * to see how old the article is as well as how many pending approvals are
 * waiting. It buffers this data to be used by the DataFuser.
 */
public class ArticleRetriever implements Runnable{
	private final static long timeToSleep = 60*1000*1;
	private final static int numThreads= 5;
	
	private NodeTable nodeTable= null;
	private ExecutorService threadPool= Executors.newFixedThreadPool(numThreads);
	
	/***************************************************************************
	 * Initializes the ArticleRetriever and starts the processing thread
	 */
	public ArticleRetriever() {
		// TODO
	}

	/***************************************************************************
	 * The processing thread that buffers Articles to be Fused on
	 */
	public void run() {
		while(Main.keepRunning){
			//wait for a bit
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e) {
			}
			
			List<Node> unfusedArticles;
			
			try {
				 //TODO: fill this in
				 //unfunsedArticles = nodeTable.getUnindexedData();
			} catch (SQLException e) {
				continue;
			}
			
			for(Node n : unfusedArticles){
				Runnable dataIndexer= new DataIndexer(dataStoredTable);
				threadPool.execute(dataIndexer);
			}
		}
		
		threadPool.shutdown();
	}
}
