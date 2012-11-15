package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import sun.datafusion.*;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.junit.Test;

import sun.datafusion.data.DataFusion;
import sun.datafusion.data.DataMeans;
import sun.datafusion.data.DataSource;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.Manager;
import sun.datafusion.data.Node;
import sun.datafusion.fuse.DataFuser;
import sun.datafusion.index.DataIndexer;
import sun.datafusion.index.DataRetriever;
import sun.datafusion.utils.PropertyUtils;

public class Test_DataFusion {
	
	private static final String testIndex= "./testLuceneIndex";
	
	private Manager setupManager(){
		Properties prop = PropertyUtils.loadProperties();
		
		return new Manager(prop);
	}
	
	/*@Test
	void testDataRetrieverConstructure(){
		
		Properties prop = null;
		Directory indexLocation = null; 
		Manager man = setupManager(); 
		
		DataRetriever dr= new DataRetriever(prop, indexLocation, man);
		
		
	}*/
	
	/*@Test
	public void testDataRetrieverRun(){
		
		Properties prop = PropertyUtils.loadProperties();
		Directory indexLocation = null; 
		Manager man = setupManager(); 
		
		DataRetriever dr= new DataRetriever(prop, indexLocation, man);
		
		dr.run();
	}*/
	
	@Test
	public void testDataFusion()throws IOException{
		
		//make sure to destroy old index
		File indexFile= new File(testIndex);
		indexFile.mkdir();
		Directory indexLocation= SimpleFSDirectory.open(indexFile);
		
		Manager man = setupManager();
		man.cleanTables();
		
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
		
		List<Node> nodes = new ArrayList<Node>();
		Node n = new Node(1);
		n.addTag("apple");
		n.addTag("google");
		nodes.add(n);
		if(!man.createNode(n))
			fail("Fails to create a Node object! ");
		man.assignTags(nodes);
		
		DataStored ds = new DataStored();
		ds.setDataMeans_id(1);
		ds.setLinkedData("Google Apple");
		ds.setTitle("test title 1");
		if(!man.createDataStored(ds))
			fail("Fails to create a DataStored object! ");
		
		ds = new DataStored();
		ds.setDataMeans_id(1);
		ds.setLinkedData("Apple Google Amazon");
		ds.setTitle("test title 2");
		man.createDataStored(ds);
		
		ds = new DataStored();
		ds.setDataMeans_id(1);
		ds.setLinkedData("Apple Facebook");
		ds.setTitle("test title 3");
		man.createDataStored(ds);
		
		ds = new DataStored();
		ds.setDataMeans_id(1);
		ds.setLinkedData("IBM HP");
		ds.setTitle("test title 4");
		man.createDataStored(ds);
		
		ds = new DataStored();
		ds.setDataMeans_id(1);
		ds.setLinkedData("Apple Google Apple");
		ds.setTitle("test title 5");
		man.createDataStored(ds);
		
		ds = new DataStored();
		ds.setDataMeans_id(1);
		ds.setLinkedData("Apple Google Amazon Facebook Yahoo LinkedIn");
		ds.setTitle("test title 6");
		man.createDataStored(ds);

		DataIndexer.clearIndex(indexFile);
		
		List<DataStored> unindexedData= man.getDataStoredToIndex();
		
		for(DataStored dss : unindexedData){
			DataIndexer di = new DataIndexer(dss, indexLocation, man);
			di.run();
		}
		
		DataFuser df = new DataFuser(n, indexLocation, man);
		df.run();
		
	}
	
	@Test
	public void test_Manager(){
		
		Manager man = setupManager();
		man.getNodesToProcess(new Date());
	}
}
