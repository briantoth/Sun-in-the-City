package sun.datafusion.index;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import sun.datafusion.data.DataStored;
import sun.datafusion.data.Manager;

/*******************************************************************************
 * This class takes DataSources from the DataRetriever and uses Apache Lucene to
 * index the data. There will be many DataIndexers running in separate threads
 * as the index process is time consuming. Note that the DataRetriever object
 * will block until DataSource objects are available.
 * 
 */
public class DataIndexer implements Runnable {
	private final DataStored dataStored;
	private final Directory indexLocation;
	private IndexWriter indexWriter;
	private final Manager manager;

	/***************************************************************************
	 * Creates a DataIndexer with the given retriever and starts the processing
	 * thread.
	 * 
	 * @param retriever
	 *            The retriever to get DataSources from to process
	 */

	public DataIndexer(DataStored ds, Directory indexLocation, Manager manager) {
		this.dataStored= ds;
		this.indexLocation = indexLocation;
		this.manager= manager;
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
			
			manager.setDataStoredIndexed(dataStored);
			
		} catch (Exception e) {
		}
	}
	
	/**
	 * Puts a fusable data source into the Lucene index
	 * 
	 * @param ds Datastored to add to the index
	 * @throws IOException
	 */
	private void addDataStore(DataStored ds) throws IOException {
		Document doc = new Document();
		doc.add(new Field("id", ds.getId()+"", Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("fusionData", ds.getData(), Field.Store.NO,
				Field.Index.ANALYZED));
		synchronized (indexLocation) {
			indexWriter.addDocument(doc);
		}
	}
	
	/**
	 * Removes existing index entries permenantly
	 * 
	 * @param indexLocation The location of the Lucene index in the filesystem
	 */
	public static void clearIndex(File indexLocation){
		String[] myFiles;      
        if(indexLocation.isDirectory()){  
            myFiles = indexLocation.list();  
            for (int i=0; i<myFiles.length; i++) {  
                File myFile = new File(indexLocation, myFiles[i]);   
                myFile.delete();  
            }  
         }  
	}
}
