package sun.datafusion.index;

import java.io.File;
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

import sun.datafusion.data.DataStored;
import sun.datafusion.data.DataStoredTable;

/*******************************************************************************
 * This class takes DataSources from the DataRetriever and uses Apache Lucene to
 * index the data. There will be many DataIndexers running in separate threads
 * as the index process is time consuming. Note that the DataRetriever object
 * will block until DataSource objects are available.
 * 
 */
public class DataIndexer extends Thread {
	private final DataStored dataStored;
	private final DataStoredTable dataStoredTable;
	private final Directory indexLocation;
	private IndexWriter indexWriter;

	/***************************************************************************
	 * Creates a DataIndexer with the given retriever and starts the processing
	 * thread.
	 * 
	 * @param retriever
	 *            The retriever to get DataSources from to process
	 */

	public DataIndexer(DataStored ds, DataStoredTable dataStoredTable, Directory indexLocation) {
		this.dataStored= ds;
		this.dataStoredTable= dataStoredTable;
		this.indexLocation = indexLocation;
	}

	/***************************************************************************
	 * Processing thread that repeatedly gets DataSources from the DataRetriever
	 * and uses Apache Lucene to index them.
	 */
	public void run() {
		StandardAnalyzer analyzer= new StandardAnalyzer(Version.LUCENE_36);
		IndexWriterConfig config= new IndexWriterConfig(Version.LUCENE_36, analyzer);
		
		try {
			indexWriter= new IndexWriter(indexLocation, config);
		
		
		
		
			indexWriter.close();
			
		} catch (Exception e) {
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
