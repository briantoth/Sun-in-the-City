package sun.datafusion.fuse;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import sun.datafusion.data.DataFusion;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.Manager;
import sun.datafusion.data.Node;

/*******************************************************************************
 * Performs the DataFusion operation. This class continuously gets an Article to
 * perform DataFusion on and uses Apache Lucene to search for relevant
 * DataPrimary and DataSecondary sources. Upon finding a good match it stores
 * the result in the MySQL database for approval by the editors.
 */
public class DataFuser implements Runnable {
	private final Node node;
	private final Directory indexLocation;
	private final Manager manager;

	/***************************************************************************
	 * Constructor that initializes the Fusion operation and starts the
	 * processing thread.
	 * @param indexLocation 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public DataFuser(Node n, Directory indexLocation, Manager manager){
		this.node=n;
		this.indexLocation=indexLocation;
		this.manager=manager;
	}

	/***************************************************************************
	 * Processing thread that performs the Fusion operation
	 */
	public void run() {

		// 0. Specify the analyzer for tokenizing text.
		//    The same analyzer should be used for indexing and searching
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);

		// 1. query

		// the "fusionData" arg specifies the default field to use
		// when no field is explicitly specified in the query.
		Query q = null;
		try {
			q = new org.apache.lucene.queryParser.QueryParser(Version.LUCENE_36, "fusionData", analyzer).parse(node.getTagsInOneString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 2. search and return results
		// Only return Top 10 best results
		int hitsPerPage = 10;
		IndexReader reader = null;

		synchronized (indexLocation) {

			try {
				reader = IndexReader.open(indexLocation);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				return;
			} // index - where the information is stored
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
			try {
				searcher.search(q, collector);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // the search step 
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			
			// return results
			for(int i=0;i<hits.length;++i) {
				int docId = hits[i].doc;
				Document d = null;
				try {
					d = searcher.doc(docId);
					searcher.close();
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// create a DataStored object and return it to the database
				DataStored ds = manager.getDataStored(Integer.parseInt(d.get("id")));

				DataFusion df = new DataFusion();
				df.setDataSource_id(manager.getDataMeans(ds.getDataMeans_id()).getDataSource_id());
				df.setDataStored_id(ds.getId());
				df.setNodeID(node.getNodeID());
				df.setRating(10-i);
				df.setSummary(ds.getData());
				df.setTimestamp(ds.getTimestamp());
				df.setTitle(ds.getTitle());
				df.setUrl(ds.getUrl());	    	

				manager.createDataFusion(df);
			}	    
		}
	}
}