package sun.datafusion.fuse;

/*******************************************************************************
 * Obtains articles that need DataFusion operations to be performed. It checks
 * to see how old the article is as well as how many pending approvals are
 * waiting. It buffers this data to be used by the DataFuser.
 */
public class ArticleRetriever extends Thread {

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
		// TODO
	}
}
