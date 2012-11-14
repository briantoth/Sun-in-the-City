package tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.junit.Assert.*;

import sun.datafusion.data.DataFusion;
import sun.datafusion.data.Manager;
import sun.datafusion.utils.PropertyUtils;

// Tests sun.datafusion.data.Manager
public class Test_Manager {
	
	private Manager setupManager(){
		return new Manager(PropertyUtils.loadProperties());
	}
	
	@Test
	public void testStandardConstructor(){
		Manager man= setupManager();
		
		man.cleanTables();
		
		man.close();
	}
	
	@Test
	public void testPropertiesConstructor(){
		
	}
	
	@Test
	public void testGetDataMeansToProcess(){
		
	}
	
	@Test 
	public void testSetDataMeansProcessed(){
		
	}
	
	@Test
	public void testCreateDataStored(){
		
	}
	
	@Test
	public void testGetDataMeans(){
		
	}
	
	@Test
	public void testGetDataStored(){
		
	}
	
	@Test
	public void testSetDataStoredIndexed(){
		
	}
	
	@Test
	public void testCreateDataStoredFromResult(){
		
	}
	
	@Test
	public void testGetDataStoredToIndex(){
		
	}
	
	@Test
	public void testGetNodesToProcess(){
		
	}
	
	@Test
	public void testCreateDataFusion(){
		
	}
	
	@Test
	public void testClose(){
		
	}
	
	@Test
	public void testStartConnection(){
		
	}
	
}
