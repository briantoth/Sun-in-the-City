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
import java.util.List;
import java.util.Map;

/*******************************************************************************
 * Manages the interaction with data
 */
public class Manager {

	/***************************************************************************
	 * Constructor that sets up the MySQL information
	 */
	public Manager(String hostname, String database, String username,
			String password) {
		// Store connection details
		this.hostname = hostname;
		this.database = database;
		this.username = username;
		this.password = password;

		// Initial connection setup
		connection = null;

		// Start connection
		startConnection();
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
	 * Closes the database connections
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

			// Get nodes that should be processed
			psGetNodesToProcess = connection.prepareStatement("SELECT DISTINCT"
					+ "node.nid, data.name " + "FROM " + database
					+ ".node node, " + database + ".taxonomy_index index, "
					+ database + ".taxonomy_term_data data "
					+ "WHERE node.created >= ? "
					+ "AND node.nid = index.nid AND index.tid = data.tid");

			// Indicate that a datastored object has been indexed
			psSetDataStoredIndexed = connection.prepareStatement("UPDATE "
					+ database + ".DataStored ds " + "SET indexed=1 "
					+ "WHERE id=?");

			// Create a data fusion object in the table
			psCreateDataFusion = connection
					.prepareStatement("INSERT into DataFusion "
							+ "(nodeID, DataSource_id, DataStored_id, title, url, summary, rating, timestamp) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?)");

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

		} catch (SQLException e) {
			System.out.println("Failed to create prepared statements");
			close();
			return false;
		}

		// Success
		return true;
	}

	// -------------------------------------------------------------------------

	// MySQL database details
	private Connection connection; // Connection to database
	private String hostname; // URL to the database
	private String database; // Name of the database
	private String username; // Username to connect with
	private String password; // Password to connect with

	// MySQL prepared statements
	private PreparedStatement psGetDataMeansToProcess;
	private PreparedStatement psSetDataMeansProcessed;
	private PreparedStatement psCreateDataStored;
	private PreparedStatement psGetDataStoredToIndex;
	private PreparedStatement psGetNodesToProcess;
	private PreparedStatement psCreateDataFusion;
	private PreparedStatement psGetDataMeans;
	private PreparedStatement psGetDataStored;
	private PreparedStatement psSetDataStoredIndexed;
}
