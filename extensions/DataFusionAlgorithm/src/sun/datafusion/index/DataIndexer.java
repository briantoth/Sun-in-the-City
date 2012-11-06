package sun.datafusion.index;

import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import sun.datafusion.data.DataStored;

/*******************************************************************************
 * This class takes DataSources from the DataRetriever and uses Apache Lucene to
 * index the data. There will be many DataIndexers running in separate threads
 * as the index process is time consuming. Note that the DataRetriever object
 * will block until DataSource objects are available.
 * 
 */
public class DataIndexer extends Thread {
	private final DataStored dataStored;
	private final Directory indexLocation;
	private IndexWriter indexWriter;

	/***************************************************************************
	 * Creates a DataIndexer with the given retriever and starts the processing
	 * thread.
	 * 
	 * @param retriever
	 *            The retriever to get DataSources from to process
	 */

	public DataIndexer(DataStored ds, Directory indexLocation) {
		this.dataStored= ds;
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
			addDataStore(dataStored);
			indexWriter.close();
			
		} catch (Exception e) {
		}
	}
	
	private void addDataStore(DataStored ds) throws IOException {
		Document doc = new Document();
		doc.add(new Field("id", ds.getId()+"", Field.Store.YES,
				Field.Index.NOT_ANALYZED_NO_NORMS));
		doc.add(new Field("content", ds.getData(), Field.Store.NO,
				Field.Index.ANALYZED));
		indexWriter.addDocument(doc);
	}
}
