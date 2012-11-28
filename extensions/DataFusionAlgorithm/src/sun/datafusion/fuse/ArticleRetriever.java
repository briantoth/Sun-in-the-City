package sun.datafusion.fuse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.lucene.store.Directory;

import sun.datafusion.Main;
import sun.datafusion.data.Manager;
import sun.datafusion.data.Node;

/*******************************************************************************
 * Obtains articles that need DataFusion operations to be performed. It checks
 * to see how old the article is as well as how many pending approvals are
 * waiting. It buffers this data to be used by the DataFuser.
 */
public class ArticleRetriever implements Runnable{
	private final static long timeToSleep = 1000*60*30;
	private final static long articleAgeToIndex = 1000 * 60 * 60 * 24 * 7;
	private final static int numThreads= 5;
	
	private final ExecutorService threadPool= Executors.newFixedThreadPool(numThreads);
	private final Directory indexLocation;
	private final Manager manager;
	
	/***************************************************************************
	 * Initializes the ArticleRetriever and starts the processing thread
	 * @param manager The SQL connection manager
	 * @param indexLocation The path for the Lucene directory
	 */
	public ArticleRetriever(Directory indexLocation, Manager manager) {
		this.indexLocation= indexLocation;
		this.manager= manager;
	}

	/***************************************************************************
	 * The processing thread that buffers Articles to be Fused on
	 */
	public void run() {
		while(Main.keepRunning){
			
			List<Node> unfusedArticles= manager.getNodesToProcess(articleAgeToIndex);
			
			if(unfusedArticles != null){
				for(Node n : unfusedArticles){
					Runnable dataFuser= new DataFuser(n, indexLocation, manager);
					threadPool.execute(dataFuser);
				}
			}
			
			//wait for a bit
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e) {
				break;
			}
		}
		
		threadPool.shutdown();
	}
}
