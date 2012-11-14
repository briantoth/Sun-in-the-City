package tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import sun.datafusion.*;

import org.apache.lucene.store.Directory;
import org.junit.Test;

import sun.datafusion.data.DataFusion;
import sun.datafusion.data.DataMeans;
import sun.datafusion.data.DataSource;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.Manager;
import sun.datafusion.data.Node;
import sun.datafusion.index.DataRetriever;
import sun.datafusion.utils.PropertyUtils;

public class Test_DataFusion {
	
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
	public void testDataFusion(){
		
		Properties prop = PropertyUtils.loadProperties();
		Directory indexLocation = null; 
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
		
		Node n = new Node(1);
		n.addTag("iPhone5");
		n.addTag("surface");
		n.addTag("Nexus");
		n.addTag("Galaxy III");
		if(!man.createNode(n))
			fail("Fails to create a DataMeans object! ");
		
		DataStored ds = new DataStored();
		ds.
		
		
	}
}
