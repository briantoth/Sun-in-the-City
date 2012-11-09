package tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.UUID;

import sun.datafusion.*;

import org.junit.Test;

import sun.datafusion.data.DataFusion;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.Manager;
import sun.datafusion.utils.PropertyUtils;

public class ManagerTests {
	
	private Manager setupManager(){
		return new Manager(PropertyUtils.loadProperties());
	}
	
	@Test
	public void testCreateDataFusion(){
		Manager man= setupManager();

		UUID idOne = UUID.randomUUID();
		
		DataFusion df= new DataFusion();
		
		df.setNodeID(1);
		df.setDataSource_id(1);
		df.setDataStored_id(1);
		df.setTitle(idOne.toString());
		df.setUrl("lol.com");
		df.setSummary("this is summary");
		df.setRating(2);
		df.setTimestamp(new Date());
		
		System.out.println(idOne.toString());
		
		if(!man.createDataFusion(df))
			fail("failed to update datafusion table");
		
		man.close();
	}
	
	@Test
	public void testCreateDataStored(){
		Manager man= setupManager();
		
		UUID idOne = UUID.randomUUID();
		
		DataStored ds= new DataStored();
		
		ds.setId(2);
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
		
		man.close();
	}

}
