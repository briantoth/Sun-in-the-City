package sun.datafusion.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

public class DataStoredTable {
	private Connection tableConnection = null;
	private Statement query = null;
	private ResultSet results = null;
	private String tableref = null;
	
	/*
	 * Creates a connection to the DataStored table with database name dbname and table name tblname.
	 * Logs in using the provided username and password.
	 */
	public DataStoredTable(String dbname, String tblname, String user, String pw) throws Exception{
		tableref = dbname.toUpperCase() + "." + tblname.toUpperCase();
		Class.forName("com.mysql.jdbc.Driver");	
		tableConnection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbname + "?user=" + user + "&password=" + pw);
		query = tableConnection.createStatement();
	}
	
	public boolean insertDataStored(DataStored newData) throws SQLException{
		PreparedStatement prep = tableConnection.prepareStatement("INSERT into " + tableref + 
				" VALUES (default, ?, ?, ?, ?, ?, ?, ?, default)");
		prep.setInt(1, newData.getDataMeans_id());
		prep.setString(2, newData.getTitle());
		prep.setString(3, newData.getUrl());
		prep.setString(4, newData.getData());
		prep.setString(5, newData.getLinkedUrl());
		prep.setString(6, newData.getLinkedData());
		prep.setTime(7, new Time(newData.getTimestamp().getTime()));
		return prep.execute();
	}
}
