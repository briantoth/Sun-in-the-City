package tests;

import java.util.Date;
import java.util.UUID;
import org.junit.Test;
import org.junit.Assert.*;
import sun.datafusion.data.Manager;

// Tests sun.datafusion.data.Manager
public class Test_Manager {
	
	// Number of random iterations to run
	static int RANDOM_ITERATIONS = 1000;
	
	// Test prerequisite tests that this series of tests assume are true
	@Test 
	public void testPrerequisites(){
		// Test representation classes
		Test_Representations reps = new Test_Representations();
		reps.testDataFusion();
		reps.testDataMeans();
		reps.testDataSource();
		reps.testDataStored();
		reps.testNode();
		
		// Test properties loader
		Test_PropertyUtils props = new Test_PropertyUtils();
		props.testLoadProperties();
	}
	
	// Test the constructor
	@Test
	public void testConstructor(){
		
	}
	
	// Test getting the data means to process
	@Test
	public void testGetDataMeansToProcess(){
		
	}
	
	// Test setting the data means to process
	@Test 
	public void testSetDataMeansProcessed(){
		
	}
	
	// Test creating a data stored object
	@Test
	public void testCreateDataStored(){
		
	}
	
	// Test getting the data means
	@Test
	public void testGetDataMeans(){
		
	}
	
	// Test getting the data stored
	@Test
	public void testGetDataStored(){
		
	}
	
	// Test setting the data stored as indexed
	@Test
	public void testSetDataStoredIndexed(){
		
	}
	
	// Test getting the data stored objects to index
	@Test
	public void testGetDataStoredToIndex(){
		
	}
	
	// Test getting nodes to process
	@Test
	public void testGetNodesToProcess(){
		
	}
	
	// Test creating the data fusion object
	@Test
	public void testCreateDataFusion(){
		
	}
	
	// Test assigning tags
	@Test
	public void testAssignTags(){
		
	}
	
	// Test cleaning tables
	@Test
	public void testCleanTables(){
		
	}
	
}
