package sun.datafusion.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/*******************************************************************************
 * Manages the interaction with data
 */
public class Manager {

	/**
	 * Create a manager to handle SQL interactions
	 * 
	 * @param prop
	 *            A map populated with the user-defined configurations
	 */
	public Manager(Properties prop) {
		hostname = prop.getProperty("hostname");
		database = prop.getProperty("db");
		username = prop.getProperty("dbuser");
		password = prop.getProperty("dbpassword");
		tableNames = new HashMap<String, String>();
		tableNames.put("dataFusionTable", prop.getProperty("dataFusionTable"));
		tableNames.put("dataMeansTable", prop.getProperty("dataMeansTable"));
		tableNames.put("dataSourceTable", prop.getProperty("dataSourceTable"));
		tableNames.put("dataStoredTable", prop.getProperty("dataStoredTable"));
		tableNames.put("taxonomyTable", prop.getProperty("taxonomyTable"));
		tableNames.put("taxonomyIndex", prop.getProperty("taxonomyIndex"));
		tableNames.put("taxonomyHierarchy",
				prop.getProperty("taxonomyHierarchy"));
		tableNames.put("nodeTable", prop.getProperty("nodeTable"));
	}

	/***************************************************************************
	 * Gets the DataMeans objects that need to be processed- the ones that have
	 * lastProcessed values <= the one passed
	 * 
	 * @param lastProcessed
	 *            The date that is used to select DataMeans with
	 * @return A list of the results, or null if there was an error
	 */
	public List<DataMeans> getDataMeansToProcess(Date lastProcessed) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return null;

		try {
			// Create query
			java.sql.Timestamp sqlLastProcessed = new java.sql.Timestamp(
					lastProcessed.getTime());
			psGetDataMeansToProcess.setTimestamp(1, sqlLastProcessed);

			// Execute query
			ResultSet queryResult = psGetDataMeansToProcess.executeQuery();

			// Turn result set into the list of results
			List<DataMeans> result = new LinkedList<DataMeans>();
			while (queryResult.next()) {
				DataMeans cur = new DataMeans(queryResult.getInt(1),
						queryResult.getInt(2), queryResult.getString(3),
						queryResult.getString(4), queryResult.getInt(5),
						new java.util.Date(queryResult.getTimestamp(6)
								.getTime()));
				result.add(cur);
			}

			// Return the result list
			return result;
		} catch (SQLException e) {

			// Error occurred
			System.err.println("SQL Error occured while getting data means");
			return null;
		}

	}

	/***************************************************************************
	 * Sets the DataMeans object to processed. That is, the lastProcessed time
	 * will be set to the current time.
	 * 
	 * @param dm
	 *            The DataMeans object to update
	 * @return If the update was successful
	 */
	public boolean setDataMeansProcessed(DataMeans dm) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return false;

		try {
			// Create query
			psSetDataMeansProcessed.setTimestamp(1, new java.sql.Timestamp(
					new java.util.Date().getTime()));
			psSetDataMeansProcessed.setInt(2, dm.getId());

			// Execute query
			psSetDataMeansProcessed.executeUpdate();
			return true;

		} catch (SQLException e) {
			System.err
					.println("SQL Error when updating data means processed date");
			return false;
		}
	}

	/***************************************************************************
	 * Creates a DataStored object in the database and returns if it was
	 * successful
	 * 
	 * @param ds
	 *            The DataStored object to create
	 * @return If the creation was successful
	 */
	public boolean createDataStored(DataStored ds) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return false;

		try {
			psCreateDataStored.setInt(1, ds.getDataMeans_id());
			psCreateDataStored.setString(2, ds.getTitle());
			psCreateDataStored.setString(3, ds.getUrl());
			psCreateDataStored.setString(4, ds.getData());
			psCreateDataStored.setString(5, ds.getLinkedUrl());
			psCreateDataStored.setString(6, ds.getLinkedData());
			psCreateDataStored.setTimestamp(7, new java.sql.Timestamp(ds
					.getTimestamp().getTime()));
			return psCreateDataStored.executeUpdate() == 1;
		} catch (SQLException e) {
			System.err.println(e);
			return false;
		}
	}

	/***************************************************************************
	 * Creates a DataMeans object in the database and returns if it was
	 * successful
	 * 
	 * @param dm
	 *            The DataMeans object to create
	 * @return If the creation was successful
	 */
	public boolean createDataMeans(DataMeans dm) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return false;

		try {
			psCreateDataMeans.setInt(1, dm.getId());
			psCreateDataMeans.setInt(2, dm.getDataSource_id());
			psCreateDataMeans.setString(3, dm.getName());
			psCreateDataMeans.setString(4, dm.getUrl());
			psCreateDataMeans.setInt(5, dm.getType());
			psCreateDataMeans.setTimestamp(6, new java.sql.Timestamp(dm
					.getLastProcessed().getTime()));
			return psCreateDataMeans.executeUpdate() == 1;
		} catch (SQLException e) {
			System.err.println(e);
			return false;
		}
	}

	/***************************************************************************
	 * Creates a DataSource object in the database and returns if it was
	 * successful
	 * 
	 * @param ds
	 *            The DataSource object to create
	 * @return If the creation was successful
	 */
	public boolean createDataSource(DataSource ds) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return false;

		try {
			psCreateDataSource.setInt(1, ds.getId());
			psCreateDataSource.setString(2, ds.getUrl());
			psCreateDataSource.setString(3, ds.getName());
			psCreateDataSource.setString(4, ds.getLogourl());
			return psCreateDataSource.executeUpdate() == 1;
		} catch (SQLException e) {
			System.err.println(e);
			return false;
		}
	}

	/***************************************************************************
	 * Creates a Node object in the database and returns if it was successful
	 * 
	 * @param n
	 *            The Node object to create
	 * @return If the creation was successful
	 */
	public boolean createNode(Node n) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return false;

		try {
			psCreateNode.setInt(1, n.getNodeID());
			return psCreateNode.executeUpdate() == 1;

		} catch (SQLException e) {
			System.err.println(e);
			return false;
		}
	}

	/***************************************************************************
	 * Gets the DataMeans object represented by the given id
	 * 
	 * @param DataMeans_id
	 *            the ID of a data means object
	 * @return The DataMeans object or NULL if it was not found
	 */
	public DataMeans getDataMeans(int DataMeans_id) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return null;

		try {
			// Create query
			psGetDataMeans.setInt(1, DataMeans_id);

			// Perform query
			ResultSet rs = psGetDataMeans.executeQuery();

			// Get the result
			if (rs.next())
				// Found the object, return it
				return new DataMeans(rs.getInt(1), rs.getInt(2),
						rs.getString(3), rs.getString(4), rs.getInt(5),
						new Date(rs.getDate(6).getTime()));
			else
				// No object, return NULL
				return null;

		} catch (SQLException e) {
			// Error ocurred, return NULL
			System.err.println(e);
			return null;
		}
	}

	/***************************************************************************
	 * Gets the DataStored object represented by the given id
	 * 
	 * @param DataStored_id
	 *            the ID of a data stored object
	 * @return The DataStored object or NULL if it was not found
	 */
	public DataStored getDataStored(int DataStored_id) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return null;

		try {
			// Create query
			psGetDataStored.setInt(1, DataStored_id);

			// Perform query
			ResultSet queryResult = psGetDataStored.executeQuery();

			// Get object
			if (queryResult.next())
				// Object found, return it
				return new DataStored(queryResult.getInt(1),
						queryResult.getInt(2), queryResult.getString(3),
						queryResult.getString(4), queryResult.getString(5),
						queryResult.getString(6), queryResult.getString(7),
						new Date(queryResult.getDate(8).getTime()),
						queryResult.getBoolean(9));
			else
				// No object found, return NULL
				return null;

		} catch (SQLException e) {
			// Error ocurred, return NULL
			System.err.println(e);
			return null;
		}
	}

	/***************************************************************************
	 * Sets the given DataStored object as indexed
	 * 
	 * @param dataStored
	 *            The object to set updated
	 * @return If successfully updated the object
	 */
	public boolean setDataStoredIndexed(DataStored dataStored) {
		if (!startConnection())
			return false;

		try {
			// Set the ID to update
			psSetDataStoredIndexed.setInt(1, dataStored.getId());

			// Execute the query and return the result
			return psSetDataStoredIndexed.executeUpdate() == 1;
		} catch (SQLException e) {
			// Error occurred
			System.err
					.println("SQL Error occured while getting nodes to process");
			return false;
		}
	}

	/***************************************************************************
	 * Gets any DataStored objects in the database that are not indexed yet
	 * 
	 * @return A list of the results, or null if there was an error
	 */
	public List<DataStored> getDataStoredToIndex() {
		// Make connection if not already, ensure success
		if (!startConnection())
			return null;

		try {
			// Execute query
			ResultSet queryResult = psGetDataStoredToIndex.executeQuery();

			// Turn result set into the list of results
			List<DataStored> result = new LinkedList<DataStored>();
			while (queryResult.next()) {
				result.add(new DataStored(queryResult.getInt(1), queryResult
						.getInt(2), queryResult.getString(3), queryResult
						.getString(4), queryResult.getString(5), queryResult
						.getString(6), queryResult.getString(7), new Date(
						queryResult.getDate(8).getTime()), queryResult
						.getBoolean(9)));
			}

			// Return the result list
			return result;
		} catch (SQLException e) {

			// Error occurred
			System.err.println("SQL Error occured while getting data stored");
			return null;
		}
	}

	/***************************************************************************
	 * Gets nodes (articles) to process that are written after the given date
	 * 
	 * @param msInPast How long in the past to check for articles to fuse on
	 * @return A list of the results, or null if there was an error
	 */
	public List<Node> getNodesToProcess(long msInPast) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return null;

		try {
			// Set time for query
			psGetNodesToProcess.setLong(1, msInPast);

			// Execute query to get all ids
			ResultSet queryResult = psGetNodesToProcess.executeQuery();

			// Turn result set into map of nodes and list of ids
			Map<Integer, Node> nodes = new HashMap<Integer, Node>();
			while (queryResult.next()) {

				// Get the integer of the current node
				Integer nodeID = queryResult.getInt(1);

				// Check if we are already tracking nodeID
				Node curNode;
				if (nodes.containsKey(nodeID)) {

					// Get the current existing node
					curNode = nodes.get(nodeID);
				} else {

					// Create node and add to map
					curNode = new Node(nodeID);
					nodes.put(nodeID, curNode);
				}

				// Add the current tag
				curNode.addTag(queryResult.getString(2));
			}

			// Convert the map to the list
			List<Node> result = new LinkedList<Node>();
			Collection<Node> values = nodes.values();
			for (Node curNode : values) {
				result.add(curNode);
			}

			// Return the result list
			return result;
		} catch (SQLException e) {

			// Error occurred
			System.err
					.println("SQL Error occured while getting nodes to process");
			return null;
		}
	}

	/***************************************************************************
	 * Gets nodes (articles) to process that are written after the given date
	 * 
	 * @param nid
	 *            The request Node ID
	 * @return A list of the results, or null if there was an error
	 */
	public Node getNode(int nid) {

		List<Node> nodes;

		nodes = getNodesToProcess(2353379155L);

		for (Node node : nodes) {

			if (node.getNodeID() == nid)
				return node;
		}

		return null;
	}

	/***************************************************************************
	 * Creates a DataFusion object if the (nodeID, DataStored_id) pair does not
	 * yet exist in the database.
	 * 
	 * @param df
	 *            The DataFusion object to create
	 * @return If the creation was successful, can fail for non fatal reasons
	 *         (entry already exists)
	 */
	public boolean createDataFusion(DataFusion df) {
		// Make connection if not already, ensure success
		if (!startConnection()) {
			System.out.println("failed connection");
			return false;
		}

		try {
			psGetDataFusionPreviousCount.setInt(1, df.getNodeID());
			psGetDataFusionPreviousCount.setInt(2, df.getDataStored_id());
			ResultSet existingDF = psGetDataFusionPreviousCount.executeQuery();
			if(existingDF == null || !existingDF.next() || existingDF.getInt(1) != 0)
				return false;
			
			psCreateDataFusion.setInt(1, df.getNodeID());
			psCreateDataFusion.setInt(2, df.getDataSource_id());
			psCreateDataFusion.setInt(3, df.getDataStored_id());
			psCreateDataFusion.setString(4, df.getTitle());
			psCreateDataFusion.setString(5, df.getUrl());
			psCreateDataFusion.setString(6, df.getSummary());
			psCreateDataFusion.setInt(7, df.getRating());
			psCreateDataFusion.setDate(8, new java.sql.Date(df.getTimestamp()
					.getTime()));
			return psCreateDataFusion.executeUpdate() == 1;
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Gets a data fusion object by ID. This requires you to know the ID of the
	 * object in the table
	 * 
	 * @param dataFusionID  The ID in the SQL table of the datafusion object to be fetched
	 * @return Returns the requested datafusion object
	 */
	public DataFusion getDataFusion(int dataFusionID) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return null;

		try {
			// Create query
			psGetDataFusion.setInt(1, dataFusionID);

			// Perform query
			ResultSet qr = psGetDataFusion.executeQuery();

			// Get object
			if (qr.next())
				// Object found, return it
				return new DataFusion(qr.getInt(1), qr.getInt(2), qr.getInt(3),
						qr.getInt(4), qr.getString(5), qr.getString(6),
						qr.getString(7), qr.getBoolean(8), qr.getInt(9),
						new Date(qr.getDate(8).getTime()));
			else
				// No object found, return NULL
				return null;

		} catch (SQLException e) {
			// Error ocurred, return NULL
			System.err.println(e);
			return null;
		}
	}

	/**
	 * Deletes all records from tables that are related to articles, tags, data
	 * fusion, data indexing, etc. Used to enable unit testing
	 */
	public void cleanTables() {
		// Make connection if not already, ensure success
		if (!startConnection())
			return;

		PreparedStatement psClearTable;
		try {
			psClearTable = connection.prepareStatement("DELETE FROM "
					+ database + "." + tableNames.get("dataFusionTable"));
			psClearTable.executeUpdate();
			for (String table : tableNames.values()) {
				psClearTable = connection.prepareStatement("DELETE FROM "
						+ database + "." + table);
				psClearTable.executeUpdate();
			}

		} catch (SQLException e) {
			// Error ocurred, return NULL
			System.err.println(e);
			return;
		}
	}

	/**
	 * For a group of articles, adds the tags to the database and assigns those
	 * tags to those articles. Avoids duplicating tags for any of the inputed
	 * articles, but does not take into account any tags already in the database
	 * 
	 * @param nodes
	 *            The articles to assign tags to
	 * @return Indicates success or failure
	 */
	public boolean assignTags(List<Node> nodes) {
		Set<String> tags = new HashSet<String>();
		for (Node n : nodes) {
			for (String t : n.getTags()) {
				tags.add(t);
			}
		}

		String[] uploadedTags = new String[tags.size()];
		tags.toArray(uploadedTags);
		for (int i = 0; i < uploadedTags.length; i++) {
			try {
				psCreateTaxonomy_term_data.setInt(1, i);
				psCreateTaxonomy_term_data.setString(2, uploadedTags[i]);
				psCreateTaxonomy_term_hierarchy.setInt(1, i + 1);
				psCreateTaxonomy_term_hierarchy.setInt(2, 0);
				psCreateTaxonomy_term_data.executeUpdate();
				psCreateTaxonomy_term_hierarchy.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		for (Node n : nodes) {
			for (String s : n.getTags()) {
				int indexOf = 0;
				while (indexOf < uploadedTags.length
						&& !uploadedTags[indexOf].equals(s)) {
					indexOf++;
				}
				try {
					psCreateTaxonomy_index.setInt(1, n.getNodeID());
					psCreateTaxonomy_index.setInt(2, indexOf);
					psCreateTaxonomy_index.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	public void clearDataStored(long timeBefore) {

		// Make connection if not already, ensure success
		if (!startConnection())
			return;

		try {
			// Create query
			psCleanDataStored.setTimestamp(1, new java.sql.Timestamp(
					new java.util.Date().getTime() - timeBefore));

			// Execute query
			psCleanDataStored.executeUpdate();

		} catch (SQLException e) {

			// Error occurred
			System.err.println("SQL Error occured while cleaning DataStored");
			return;
		}

	}

	/***************************************************************************
	 * Closes the database connections
	 */
	public void close() {
		// Attempt connection close
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			System.out.println("Failed to close database connection");
		}

		// Nullify connection
		connection = null;
	}

	// -------------------------------------------------------------------------

	/***************************************************************************
	 * Opens the database connections
	 * 
	 * @return Indicates failure or success
	 */
	public boolean startConnection() {
		// Check to make sure database connection not already initialized
		try {
			if (connection != null && !connection.isClosed())
				return true;
		} catch (SQLException e1) {
			System.out.println("SQL Error occured when checking connected");
		}

		// Load JDBC driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Failed to load jdbc driver class");
			return false;
		}

		// Setup connection
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + hostname
					+ ":3306/" + database + "?user=" + username + "&password="
					+ password);
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Failed to setup database connection");
			close();
			return false;
		}

		// Setup prepared statements
		try {
			// Get all DataMeans where the lastProcessed is <= ?
			psGetDataMeansToProcess = connection
					.prepareStatement("SELECT "
							+ "dm.id, dm.DataSource_id, dm.name, dm.url, dm.type, dm.lastProcessed "
							+ "FROM " + database + ".DataMeans dm "
							+ "WHERE dm.lastProcessed <= ?");

			// Set the lastProcessed time for the given id
			psSetDataMeansProcessed = connection.prepareStatement("UPDATE "
					+ database + ".DataMeans dm " + "SET lastProcessed = ? "
					+ "WHERE id = ?");

			// Get all DataStored where not indexed
			psGetDataStoredToIndex = connection
					.prepareStatement("SELECT "
							+ "ds.id, ds.DataMeans_id, ds.title, ds.url, ds.data, ds.linkedUrl, ds.linkedData, ds.timestamp, ds.indexed "
							+ "FROM " + database + ".DataStored ds "
							+ "WHERE ds.indexed=false");

			// Indicate that a datastored object has been indexed
			psSetDataStoredIndexed = connection.prepareStatement("UPDATE "
					+ database + ".DataStored ds " + "SET indexed=1 "
					+ "WHERE id=?");
			
			// Checks if there is a previous Data Fusion object
			psGetDataFusionPreviousCount = connection.prepareStatement("SELECT COUNT(*) " 
					+ "FROM DataFusion df WHERE df.nodeID=? AND df.DataStored_id=?");

			// Create a data fusion object in the table
			psCreateDataFusion = connection
					.prepareStatement("INSERT into DataFusion "
							+ "(nodeID, DataSource_id, DataStored_id, title, url, summary, rating, timestamp) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?)");

			// Create a data means object in the table
			psCreateDataMeans = connection
					.prepareStatement("INSERT into DataMeans "
							+ "(id, DataSource_id, name, url, type, lastProcessed) "
							+ "values (?, ?, ?, ?, ?, ?)");

			// Create a data stored object
			psCreateDataStored = connection
					.prepareStatement("INSERT into DataStored "
							+ "(DataMeans_id, title, url, data, linkedUrl, linkedData, timestamp) "
							+ "values (?, ?, ?, ?, ?, ?, ?)");

			// Get a data stored object
			psGetDataStored = connection.prepareStatement("SELECT * " + "FROM "
					+ database + ".DataStored " + "WHERE id=?");

			// Get a data means object
			psGetDataMeans = connection.prepareStatement("SELECT * " + "FROM "
					+ database + ".DataMeans " + "WHERE id =?");

			// Get a data fusion object
			psGetDataFusion = connection.prepareStatement("SELECT * " + "FROM "
					+ database + ".DataFusion" + "WHERE Id =?");

			// Create a data source object in the table
			psCreateDataSource = connection
					.prepareStatement("INSERT into DataSource "
							+ "(id, url, name, logourl) "
							+ "values (?, ?, ?, ?)");

			// Create a node object in the table
			psCreateNode = connection.prepareStatement("INSERT into node "
					+ "(nid) " + "values (?)");

			// Create a taxonomy_term_data object in the table
			psCreateTaxonomy_term_data = connection
					.prepareStatement("INSERT into taxonomy_term_data "
							+ "(tid, name) " + "values (?, ?)");

			// Create a taxonomy_index object in the table
			psCreateTaxonomy_index = connection
					.prepareStatement("INSERT into taxonomy_index "
							+ "(nid, tid) " + "values (?, ?)");

			// Create a taxonomy_term_hierarchy object in the table
			psCreateTaxonomy_term_hierarchy = connection
					.prepareStatement("INSERT into taxonomy_term_hierarchy "
							+ "(tid, parent) " + "values (?, ?)");

			// Clean the database of data stored objects that are written before
			// a certain date and are not linked to data fusion
			psCleanDataStored = connection
					.prepareStatement("DELETE FROM "
							+ database
							+ ".DataStored "
							+ "WHERE timestamp < ? AND id NOT IN (SELECT DISTINCT df.DataStored_id FROM "
							+ database + ".DataFusion df)");

			// Create query
			// Get nodes that should be processed
			psGetNodesToProcess = connection
					.prepareStatement("SELECT "
							+ "node.nid, data.name " + "FROM " + database
							+ ".node node, " + database
							+ ".taxonomy_index ind, " + database
							+ ".taxonomy_term_data data "
							+ "WHERE unix_timestamp() - ? " 
							+ " <= node.created AND "
							+ "node.nid = ind.nid AND ind.tid = data.tid");

		} catch (SQLException e) {
			System.out.println("Failed to create prepared statements");
			close();
			return false;
		}

		// Success
		return true;
	}

	/**
	 * Returns the current database connection
	 * 
	 * @return The current database connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Returns the database's hostname
	 * 
	 * @return The hostname of the current database
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Returns the current database's name
	 * 
	 * @return The name of the current database as a string
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Returns the username used to log in to the database
	 * 
	 * @return The current username as a string
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Returns the password used to log in to the database
	 * 
	 * @return The current password as a string
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the mapping from internal table names to actual table names
	 * 
	 * @return The mapping from standard table names to actual table names
	 */
	public Map<String, String> getTableNames() {
		return tableNames;
	}

	// -------------------------------------------------------------------------

	// MySQL database details
	private Connection connection; // Connection to database
	private String hostname; // URL to the database
	private String database; // Name of the database
	private String username; // Username to connect with
	private String password; // Password to connect with
	private Map<String, String> tableNames;

	// MySQL prepared statements
	private PreparedStatement psGetDataMeansToProcess;
	private PreparedStatement psSetDataMeansProcessed;
	private PreparedStatement psCreateDataStored;
	private PreparedStatement psGetDataStoredToIndex;
	private PreparedStatement psGetNodesToProcess;
	private PreparedStatement psGetDataFusionPreviousCount;
	private PreparedStatement psCreateDataFusion;
	private PreparedStatement psGetDataFusion;
	private PreparedStatement psCreateDataMeans;
	private PreparedStatement psGetDataMeans;
	private PreparedStatement psGetDataStored;
	private PreparedStatement psSetDataStoredIndexed;
	private PreparedStatement psCreateDataSource;
	private PreparedStatement psCreateNode;
	private PreparedStatement psCreateTaxonomy_term_data;
	private PreparedStatement psCreateTaxonomy_index;
	private PreparedStatement psCreateTaxonomy_term_hierarchy;
	private PreparedStatement psCleanDataStored;
	
}
