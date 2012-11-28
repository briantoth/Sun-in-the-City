package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.junit.Test;

import sun.datafusion.data.DataMeans;
import sun.datafusion.data.DataSource;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.Manager;
import sun.datafusion.index.DataIndexer;
import sun.datafusion.utils.PropertyUtils;

public class Test_Build_Index {
	
	private static final String testIndex= "./testLuceneIndex";

	private Manager setupManager(){
		Properties prop = PropertyUtils.loadProperties();
		
		return new Manager(prop);
	}
	
	/**
	 * Test creating the index files with an article 
	 * in the format of DataStored
	 * @throws IOException
	 */
	@Test
	public void testIndexCreation() throws IOException {
		Manager man= setupManager();
		
		man.cleanTables();
		createDataMeansAndSource(man);
		
		UUID idOne = UUID.randomUUID();
		
		DataStored ds= new DataStored();
		
		ds.setDataMeans_id(1);
		ds.setIndexed(false);
		ds.setLinkedData("test linked data");
		ds.setLinkedUrl("test linked url");
		ds.setData("test article data");
		ds.setTimestamp(new Date());
		ds.setUrl("test url");
		ds.setTitle(idOne.toString());
		
		if(!man.createDataStored(ds))
			fail("failed to update datastored table");
		
		//make sure to destroy old index
		File indexFile= new File(testIndex);
		DataIndexer.clearIndex(indexFile);
		indexFile.mkdir();
		Directory indexLocation= SimpleFSDirectory.open(indexFile);
		DataIndexer di= new DataIndexer(ds, indexLocation, man);
		di.run();
		
		if(indexFile.list().length == 0)
			fail("did not create index");
	}
	
	/**
	 * Test creating Data Means and Data Source records 
	 * in the corresponding tables
	 * @param man
	 */
	public void createDataMeansAndSource(Manager man){
		DataSource dsrc = new DataSource();
		dsrc.setId(1);
		dsrc.setName("test DataSource");
		dsrc.setUrl("test DataSource url");
		dsrc.setLogourl("test DataSource logourl");
		
		if(!man.createDataSource(dsrc))
			fail("Fails to create a DataSource object! ");
		
		DataMeans dm = new DataMeans();
		dm.setId(1);
		dm.setDataSource_id(1);
		dm.setName("test DataMeans");
		dm.setType(0);
		dm.setUrl("test url");
		dm.setLastProcessed(new Date());
		
		if(!man.createDataMeans(dm))
			fail("Fails to create a DataMeans object! ");
	}
	
}
