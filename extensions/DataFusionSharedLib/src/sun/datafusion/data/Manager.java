package sun.datafusion.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;

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

	/***************************************************************************
	 * Gets the DataMeans objects that need to be processed- the ones that have
	 * lastProcessed values <= the one passed
	 * 
	 * @param lastProcessed
	 *            The date that is used to select DataMeans with
	 * @return A list of the results, or null if there was an error
	 */
	public LinkedList<DataMeans> getDataMeansToProcess(Date lastProcessed) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return null;

		// Create query
		// TODO

		// Form list of results
		// TODO
		return null;
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
	public LinkedList<DataStored> getDataStoredToIndex() {
		// Make connection if not already, ensure success
		if (!startConnection())
			return null;

		// Create query
		// TODO

		// Form list of results
		// TODO
		return null;
	}

	/***************************************************************************
	 * Gets nodes (articles) to process that are written after the given date
	 * 
	 * @param writtenAfter
	 *            The selection date criteria to select nodes
	 * @return A list of the results, or null if there was an error
	 */
	public LinkedList<Node> getNodesToProcess(Date writtenAfter) {
		// Make connection if not already, ensure success
		if (!startConnection())
			return null;

		// Create query
		// TODO

		// Form list of results
		// TODO
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
