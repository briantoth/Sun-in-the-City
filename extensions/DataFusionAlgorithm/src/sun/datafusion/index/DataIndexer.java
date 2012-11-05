package sun.datafusion.index;

import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/*******************************************************************************
 * This class takes DataSources from the DataRetriever and uses Apache Lucene to
 * index the data. There will be many DataIndexers running in separate threads
 * as the index process is time consuming. Note that the DataRetriever object
 * will block until DataSource objects are available.
 * 
 */
public class DataIndexer extends Thread {
	IndexWriter w;

	/***************************************************************************
	 * Creates a DataIndexer with the given retriever and starts the processing
	 * thread.
	 * 
	 * @param retriever
	 *            The retriever to get DataSources from to process
	 */
	public DataIndexer(DataRetriever retriever) {
		
	}

	/***************************************************************************
	 * Processing thread that repeatedly gets DataSources from the DataRetriever
	 * and uses Apache Lucene to index them.
	 */
	public void run() {
		StandardAnalyzer analyzer= new StandardAnalyzer(Version.LUCENE_36);
		Directory index= new RAMDirectory();
		IndexWriterConfig config= new IndexWriterConfig(Version.LUCENE_36,
				analyzer);
		try {
			w= new IndexWriter(index, config);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			addDoc("Lucene in Action", "193398817");
			addDoc("Lucene for Dummies", "55320055Z");
			addDoc("Managing Gigabytes", "55063554A");
			addDoc("The Art of Computer Science", "9900333X");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			w.close();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Query q = null;
		
		//test query
		try {
			q= new QueryParser(Version.LUCENE_36, "title", analyzer).
					parse("aRT");
		} catch (org.apache.lucene.queryParser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//make a searcher using the query
		int hitsPerPage= 10;
		IndexReader reader= null;
		try {
			reader = IndexReader.open(index);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IndexSearcher searcher= new IndexSearcher(reader);
		TopScoreDocCollector collector= TopScoreDocCollector.create(
				hitsPerPage, true);
		try {
			searcher.search(q, collector);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ScoreDoc[] hits= collector.topDocs().scoreDocs;
		
		//display search results
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0; i<hits.length; ++i){
			int docID= hits[i].doc;
			Document d= null;
			try {
				d = searcher.doc(docID);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println((i+1) + ", " + d.get("isbn") + "\t" +
			d.get("title"));
		}
	}
	
	private void addDoc(String title, String isbn) throws IOException {
		Document doc = new Document();
		doc.add(new Field("title", title, Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("isbn", isbn, Field.Store.YES,
				Field.Index.ANALYZED));
		w.addDocument(doc);
	}
	
}
