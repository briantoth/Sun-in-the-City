package tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.UUID;

import sun.datafusion.*;

import org.junit.Test;

import sun.datafusion.data.DataFusion;
import sun.datafusion.data.Manager;
import sun.datafusion.utils.PropertyUtils;

public class ManagerTests {
	
	private Manager setupManager(){
		return new Manager(PropertyUtils.loadProperties());
	}
	
	@Test
	public void testCreateDataFusion(){
		Manager man= setupManager();
		man.startConnection();
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
	}

}
