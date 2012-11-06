package sun.datafusion.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DataStoredTable {
	private final Connection tableConnection;
	private Statement query = null;
	private ResultSet results = null;
	private final String tableref;
	
	
	public DataStoredTable(Properties prop) throws Exception{
		this(prop.getProperty("db"), "datastored", prop.getProperty("dbuser"), 
				prop.getProperty("dbpassword"));
	}
	
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
	
	public List<DataStored> getUnindexedData() throws SQLException{
		List<DataStored> unindexedData = new ArrayList<DataStored>();
		ResultSet resultSet= query.executeQuery("SELECT * FROM " + tableref + " WHERE indexed = 0");
		while(resultSet.next()){
			unindexedData.add(new DataStored(results.getInt(1), results.getInt(2), results.getString(3), results.getString(4), results.getString(5),
					results.getString(6), results.getString(7), results.getTime(8), results.getInt(9)));
		}
		return unindexedData;
	}
	
	public boolean markAsIndexed(DataStored toMark) throws SQLException{
		PreparedStatement prep = tableConnection.prepareStatement("UPDATE " + tableref + 
				" SET indexed = 1 WHERE id = ?");
		prep.setInt(1, toMark.getId());
		return prep.execute();
	}
}
