package tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Properties;
import org.junit.Test;
import sun.datafusion.data.Manager;

// Tests sun.datafusion.data.Manager
public class Test_Manager {

	// Number of random iterations to run
	static int RANDOM_ITERATIONS = 1000;

	public static class Test_DB_Settings {
		public static String hostname = "localhost";
		public static String db = "testDB";
		public static String dbuser = "root";
		public static String dbpass = "root";
		public static String dataFusionTable = "DataFusion";
		public static String dataMeansTable = "DataMeans";
		public static String dataSourceTable = "DataSource";
		public static String dataStoredTable = "DataStored";
		public static String taxonomyTable = "Taxonomy";
		public static String taxonomyIndex = "Taxonomy";
		public static String taxonomyHierarchy = "Taxonomy";
		public static String nodeTable = "Node";

		public static Properties getProperties() {
			// Setup properties
			Properties prop = new Properties();
			prop.setProperty("hostname", hostname);
			prop.setProperty("db", db);
			prop.setProperty("dbuser", dbuser);
			prop.setProperty("dbpassword", dbpass);
			prop.setProperty("dataFusionTable", dataFusionTable);
			prop.setProperty("dataMeansTable", dataMeansTable);
			prop.setProperty("dataSourceTable", dataSourceTable);
			prop.setProperty("dataStoredTable", dataStoredTable);
			prop.setProperty("taxonomyTable", taxonomyTable);
			prop.setProperty("taxonomyIndex", taxonomyIndex);
			prop.setProperty("taxonomyHierarchy", taxonomyHierarchy);
			prop.setProperty("nodeTable", nodeTable);

			return prop;
		}
	}

	// Test prerequisite tests that this series of tests assume are true
	@Test
	public void testPrerequisites() {
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
	public void testConstructor() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			// Fields
			String hostname = Test_Random.randString();
			String db = Test_Random.randString();
			String dbuser = Test_Random.randString();
			String dbpass = Test_Random.randString();
			String dataFusionTable = Test_Random.randString();
			String dataMeansTable = Test_Random.randString();
			String dataSourceTable = Test_Random.randString();
			String dataStoredTable = Test_Random.randString();
			String taxonomyTable = Test_Random.randString();
			String taxonomyIndex = Test_Random.randString();
			String taxonomyHierarchy = Test_Random.randString();
			String nodeTable = Test_Random.randString();

			// Setup properties
			Properties prop = new Properties();
			prop.setProperty("hostname", hostname);
			prop.setProperty("db", db);
			prop.setProperty("dbuser", dbuser);
			prop.setProperty("dbpassword", dbpass);
			prop.setProperty("dataFusionTable", dataFusionTable);
			prop.setProperty("dataMeansTable", dataMeansTable);
			prop.setProperty("dataSourceTable", dataSourceTable);
			prop.setProperty("dataStoredTable", dataStoredTable);
			prop.setProperty("taxonomyTable", taxonomyTable);
			prop.setProperty("taxonomyIndex", taxonomyIndex);
			prop.setProperty("taxonomyHierarchy", taxonomyHierarchy);
			prop.setProperty("nodeTable", nodeTable);

			// Create manager
			Manager manager = new Manager(prop);

			// Test the fields
			assertTrue(manager.getHostname().compareTo(hostname) == 0);
			assertTrue(manager.getDatabase().compareTo(db) == 0);
			assertTrue(manager.getUsername().compareTo(dbuser) == 0);
			assertTrue(manager.getPassword().compareTo(dbpass) == 0);
			assertTrue(manager.getTableNames().get("dataFusionTable")
					.compareTo(dataFusionTable) == 0);
			assertTrue(manager.getTableNames().get("dataMeansTable")
					.compareTo(dataMeansTable) == 0);
			assertTrue(manager.getTableNames().get("dataSourceTable")
					.compareTo(dataSourceTable) == 0);
			assertTrue(manager.getTableNames().get("dataStoredTable")
					.compareTo(dataStoredTable) == 0);
			assertTrue(manager.getTableNames().get("taxonomyTable")
					.compareTo(taxonomyTable) == 0);
			assertTrue(manager.getTableNames().get("taxonomyIndex")
					.compareTo(taxonomyIndex) == 0);
			assertTrue(manager.getTableNames().get("taxonomyHierarchy")
					.compareTo(taxonomyHierarchy) == 0);
			assertTrue(manager.getTableNames().get("nodeTable")
					.compareTo(nodeTable) == 0);

			// Create new manager with the usual settings
			manager = new Manager(Test_DB_Settings.getProperties());

			// Test the fields
			assertTrue(manager.getHostname().compareTo(
					Test_DB_Settings.hostname) == 0);
			assertTrue(manager.getDatabase().compareTo(Test_DB_Settings.db) == 0);
			assertTrue(manager.getUsername().compareTo(Test_DB_Settings.dbuser) == 0);
			assertTrue(manager.getPassword().compareTo(Test_DB_Settings.dbpass) == 0);
			assertTrue(manager.getTableNames().get("dataFusionTable")
					.compareTo(Test_DB_Settings.dataFusionTable) == 0);
			assertTrue(manager.getTableNames().get("dataMeansTable")
					.compareTo(Test_DB_Settings.dataMeansTable) == 0);
			assertTrue(manager.getTableNames().get("dataSourceTable")
					.compareTo(Test_DB_Settings.dataSourceTable) == 0);
			assertTrue(manager.getTableNames().get("dataStoredTable")
					.compareTo(Test_DB_Settings.dataStoredTable) == 0);
			assertTrue(manager.getTableNames().get("taxonomyTable")
					.compareTo(Test_DB_Settings.taxonomyTable) == 0);
			assertTrue(manager.getTableNames().get("taxonomyIndex")
					.compareTo(Test_DB_Settings.taxonomyIndex) == 0);
			assertTrue(manager.getTableNames().get("taxonomyHierarchy")
					.compareTo(Test_DB_Settings.taxonomyHierarchy) == 0);
			assertTrue(manager.getTableNames().get("nodeTable")
					.compareTo(Test_DB_Settings.nodeTable) == 0);
		}
	}

	// Test the connection
	@Test
	public void testStartConnection() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			// Create new manager with the usual settings
			Manager manager = new Manager(Test_DB_Settings.getProperties());

			// Test connecting and disconnecting
			for (int j = 0; j < RANDOM_ITERATIONS; j++) {
				assertTrue(manager.startConnection());
				manager.close();
			}
		}
	}

	// Class to hold all the database objects
	public class DatabaseSet {
		// TODO
	}

	// Create the testing database
	public DatabaseSet createTestingDatabase() {
		// TODO
		return null;
	}

	// Test getting the data means to process
	@Test
	public void testGetDataMeansToProcess() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

	// Test setting the data means to process
	@Test
	public void testSetDataMeansProcessed() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

	// Test creating a data stored object
	@Test
	public void testCreateDataStored() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

	// Test getting the data means
	@Test
	public void testGetDataMeans() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

	// Test getting the data stored
	@Test
	public void testGetDataStored() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

	// Test setting the data stored as indexed
	@Test
	public void testSetDataStoredIndexed() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

	// Test getting the data stored objects to index
	@Test
	public void testGetDataStoredToIndex() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

	// Test getting nodes to process
	@Test
	public void testGetNodesToProcess() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

	// Test creating the data fusion object
	@Test
	public void testCreateDataFusion() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

	// Test assigning tags
	@Test
	public void testAssignTags() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

	// Test cleaning tables
	@Test
	public void testCleanTables() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			DatabaseSet set = createTestingDatabase();
			assertTrue(set != null);

			// TODO
		}
	}

}
