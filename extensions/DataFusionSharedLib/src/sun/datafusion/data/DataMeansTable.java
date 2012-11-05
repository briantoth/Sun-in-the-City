package sun.datafusion.data;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

public class DataMeansTable {
	private Connection tableConnection = null;
	private Statement query = null;
	private ResultSet results = null;
	private String tableref = null;
	
	/*
	 * Creates a connection to the DataMeans table with database name dbname and table name tblname.
	 * Logs in using the provided username and password.
	 */
	public DataMeansTable(String dbname, String tblname, String user, String pw) throws Exception{
		tableref = dbname.toUpperCase() + "." + tblname.toUpperCase();
		Class.forName("com.mysql.jdbc.Driver");	
		tableConnection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbname + "?user=" + user + "&password=" + pw);
		query = tableConnection.createStatement();
	}
	
	/*
	 * Gets the next DataSource from the given database.
	 * Returns NULL if there is nothing left to iterate through.
	 * Calling getNextSource after a null value is returned will
	 * start the iteration again from the first row of the SQL table.
	 */
	public DataMeans getNextMeans() throws SQLException{
		if(results == null){
			results = query.executeQuery("SELECT * FROM " + tableref);
		}
		if(results.next()){
			return new DataMeans(results.getInt(1),results.getInt(2),
					results.getString(3),results.getString(4), results.getInt(5), results.getTime(6));
		}
		else{
			results = null;
			return null;
		}
	}
	
	public boolean updateDataMeans(int id, Date date) throws SQLException{
		ResultSet res;
		res = query.executeQuery("SELECT * FROM " + tableref + " WHERE id = " + id);
		if(res.next()){
			res.updateTime(6, new Time(date.getTime()));
			res.updateRow();
			return true;
		}
		else
			return false;
	}
	
	public void close() throws SQLException{
		if(results != null)
			results.close();
		if(query != null)
			query.close();
		if(tableConnection != null)
			tableConnection.close();
	}
}
