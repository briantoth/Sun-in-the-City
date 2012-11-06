package sun.datafusion.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.List;
import java.util.Map;

/*******************************************************************************
 * Manages the interaction with data
 */
public class Manager {

	/***************************************************************************
	 * Constructor that sets up the MySQL information
	 */
	public Manager(String url, String database, String username, String password) {
		// Store connection details
		this.url = url;
		this.database = database;
		this.username = username;
		this.password = password;

		// Initial connection setup
		connection = null;

		// Start connection
		startConnection();
	}

	public Manager(Properties prop) {
		this(prop.getProperty("hostname"), prop.getProperty("db"), prop
				.getProperty("dbuser"), prop.getProperty("dbpassword"));
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
			java.sql.Date sqlLastProcessed = new java.sql.Date(
					lastProcessed.getTime());
			psGetDataMeansToProcess.setDate(1, sqlLastProcessed);

			// Execute query
			ResultSet queryResult = psGetDataMeansToProcess.executeQuery();

			// Turn result set into the list of results
			List<DataMeans> result = new LinkedList<DataMeans>();
			while (queryResult.next()) {
				DataMeans cur = new DataMeans(queryResult.getInt(1),
						queryResult.getInt(2), queryResult.getString(3),
						queryResult.getString(4), queryResult.getInt(5),
						new Date(queryResult.getDate(6).getTime()));
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

		// Create query
		// TODO

		// Return results
		return false;
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

		// Create query
		// TODO

		// Return results
		return false;
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
				DataStored cur = new DataStored(queryResult.getInt(1),
						queryResult.getInt(2), queryResult.getString(3),
						queryResult.getString(4), queryResult.getString(5),
						queryResult.getString(6), queryResult.getString(7),
						new Date(queryResult.getDate(8).getTime()),
						queryResult.getBoolean(9));
				result.add(cur);
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
	 * @param writtenAfter
	 *            The selection date criteria to select nodes
	 * @return A list of the results, or null if there was an error
	 */
	public List<Node> getNodesToProcess(Date writtenAfter) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return null;

		try {
			// Create query
			java.sql.Date sqlWrittenAfter = new java.sql.Date(
					writtenAfter.getTime());
			psGetNodesToProcess.setDate(1, sqlWrittenAfter);

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
				curNode.getTags().add(queryResult.getString(2));
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
		if (!startConnection())
			return false;

		// Create query
		// TODO

		// Form list of results
		// TODO
		return false;
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
			System.err.println("Failed to close database connection");
		}

		// Nullify connection
		connection = null;
	}

	// -------------------------------------------------------------------------

	/***************************************************************************
	 * Closes the database connections
	 */
	public boolean startConnection() {
		// Check to make sure database connection not already initialized
		try {
			if (connection != null && !connection.isClosed())
				return true;
		} catch (SQLException e1) {
			System.err.println("SQL Error occured when checking connected");
		}

		// Load JDBC driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return false;
		} catch (ClassNotFoundException e) {
			System.err.println("Failed to load jdbc driver class");
		}

		// Setup connection
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + url
					+ "/" + database + "?user=" + username + "&password="
					+ password);
		} catch (SQLException e) {
			System.err.println("Failed to setup database connection");
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

			// Create a data stored object
			// TODO
			psCreateDataStored = connection.prepareStatement("");

			// Get all DataStored where not indexed
			psGetDataStoredToIndex = connection
					.prepareStatement("SELECT "
							+ "ds.id, ds.DataMeans_id, ds.title, ds.url, ds.data, ds.linkedUrl, ds.linkedData, ds.timestamp, ds.indexed "
							+ "FROM " + database + ".DataStored ds "
							+ "WHERE ds.indexed=false");

			// Get nodes that should be processed
			psGetNodesToProcess = connection.prepareStatement("SELECT DISTINCT"
					+ "node.nid, data.name " + "FROM " + database
					+ ".node node, " + database + ".taxonomy_index index, "
					+ database + ".taxonomy_term_data data "
					+ "WHERE node.created >= ? "
					+ "AND node.nid = index.nid AND index.tid = data.tid");

			// Create a data fusion object in the table
			// TODO
			psCreateDataFusion = connection.prepareStatement("");
		} catch (SQLException e) {
			System.err.println("Failed to create prepared statements");
			close();
			return false;
		}

		// Success
		return true;
	}

	// -------------------------------------------------------------------------

	// MySQL database details
	private Connection connection; // Connection to database
	private String url; // URL to the database
	private String database; // Name of the database
	private String username; // Username to connect with
	private String password; // Password to connect with

	// MySQL prepared statements
	private PreparedStatement psGetDataMeansToProcess;
	private PreparedStatement psCreateDataStored;
	private PreparedStatement psGetDataStoredToIndex;
	private PreparedStatement psGetNodesToProcess;
	private PreparedStatement psCreateDataFusion;
}
