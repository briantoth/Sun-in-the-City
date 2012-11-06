package sun.datafusion.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class NodeTable {
	private Connection tableConnection = null;
	private Statement query = null;
	private ResultSet results = null;
	private String tableref = null;
	
	/*
	 * Creates a connection to the DataStored table with database name dbname and table name tblname.
	 * Logs in using the provided username and password.
	 */
	public NodeTable(String dbname, String tblname, String user, String pw) throws Exception{
		tableref = dbname.toUpperCase() + "." + tblname.toUpperCase();
		Class.forName("com.mysql.jdbc.Driver");	
		tableConnection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbname + "?user=" + user + "&password=" + pw);
		query = tableConnection.createStatement();
	}
	
	public List<Node> getNewNodes(Date date) throws SQLException{
		ArrayList<Node> retlist = new ArrayList<Node>();
		results = query.executeQuery("SELECT * FROM " + tableref + " WHERE changed >= " + date.getTime());
		while(results.next()){
			
		}
	}
}
