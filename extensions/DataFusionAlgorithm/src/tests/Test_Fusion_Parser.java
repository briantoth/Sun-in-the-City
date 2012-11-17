package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.junit.Test;

import sun.datafusion.data.DataMeans;
import sun.datafusion.data.DataSource;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.Manager;
import sun.datafusion.data.Node;
import sun.datafusion.fuse.DataFuser;
import sun.datafusion.index.DataIndexer;
import sun.datafusion.parser.RSSParser;
import sun.datafusion.utils.PropertyUtils;

public class Test_Fusion_Parser {
	
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
	public void testDataFusion()throws IOException, InterruptedException{
		
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
		dm.setName("NYTimes Internet");
		dm.setType(1);
		dm.setUrl("http://www.nytimes.com/services/xml/rss/nyt/internet.xml");
		dm.setLastProcessed(new Date((new Date()).getTime() - (1000 * 60 * 60 *24)));
		if(!man.createDataMeans(dm))
			fail("Fails to create a DataMeans object! ");
		
		dm = new DataMeans();
		dm.setId(2);
		dm.setDataSource_id(1);
		dm.setName("NYTimes Bits Blog");
		dm.setType(1);
		dm.setUrl("http://bits.blogs.nytimes.com/feed/");
		dm.setLastProcessed(new Date((new Date()).getTime() - (1000 * 60 * 60 *24)));
		man.createDataMeans(dm);
		
		dm = new DataMeans();
		dm.setId(3);
		dm.setDataSource_id(1);
		dm.setName("The Verge");
		dm.setType(1);
		dm.setUrl("http://www.theverge.com/rss/index.xml");
		dm.setLastProcessed(new Date((new Date()).getTime() - (1000 * 60 * 60 *24)));
		man.createDataMeans(dm);
		
		List<Node> nodes = new ArrayList<Node>();
		Node n = new Node(1);
		n.addTag("hackers");
		man.createNode(n);
		nodes.add(n);
		
		n = new Node(2);
		n.addTag("facebook");
		n.addTag("iPhone");
		n.addTag("Nexus");
		man.createNode(n);
		nodes.add(n);
		
		man.assignTags(nodes);
		
		Properties properties = sun.datafusion.utils.PropertyUtils.loadProperties();
		sun.datafusion.utils.PropertyUtils.loadLoggingProperties();
		//Default timeout is 1000 seconds, or about 17 minutes.
		RSSParser parser = new RSSParser(properties);
		parser.parse();

		DataIndexer.clearIndex(indexFile);
		
		List<DataStored> unindexedData= man.getDataStoredToIndex();
		
		for(DataStored dss : unindexedData){
			DataIndexer di = new DataIndexer(dss, indexLocation, man);
			di.run();
		}
		
		DataFuser df = new DataFuser(nodes.get(0), indexLocation, man);
		df.run();
		
	}
}
