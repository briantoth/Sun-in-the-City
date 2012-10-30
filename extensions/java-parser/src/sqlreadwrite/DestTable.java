package sqlreadwrite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.util.Date;

public class DestTable {
	private Connection table = null;
	//private Statement query = null;
	//private String database = null;
	//private String table = null;
	private String table_primary = null;
	private String table_secondary = null;
	private String tableref_primary = null;
	private String tableref_secondary = null;
	
	public DestTable(String databaseName, String tableName1, String tableName2, String user, String pw) throws Exception{
		try{
			//database = databaseName;
			//table = tableName;
			table_primary = tableName1;
			table_secondary = tableName2;
			tableref_primary = databaseName.toUpperCase() + "." + tableName1.toUpperCase();
			tableref_secondary = databaseName.toUpperCase() + "." + tableName2.toUpperCase();
			Class.forName("com.mysql.jdbc.Driver");	
			table = DriverManager.getConnection("jdbc:mysql://localhost/" + databaseName + "?user=" + user + "&password=" + pw);
			//query = table.createStatement();
		}
		catch(Exception e){
			throw e;
		}
	}
	
	public NewsData writeData(NewsData news) throws SQLException{
		//Note that the value of news.primary_id and news.secondary_id will be set and applied here, hence the return value.
		PreparedStatement p = table.prepareStatement("INSERT into " + tableref_primary + "values (default, ?, ?, ?, ?, ?)");
		p.setInt(1, news.getMeans_id());
		p.setInt(2, news.getSource_id());
		p.setString(3, news.getUrl());
		p.setBlob(4, news.getPrimary_data());
		Date current_time = new Date();
		p.setLong(5, current_time.getTime());
		p.execute();
		p.close();
		PreparedStatement select = table.prepareStatement("SELECT * FROM " + tableref_primary + " WHERE " + table_primary + ".url=?");
		select.setString(1, news.getUrl());
		ResultSet result = select.executeQuery();
		result.next();
		select.close();
		news.setPrimary_id(result.getInt(1));
		result.close();
		PreparedStatement n = table.prepareStatement("INSERT into " + tableref_secondary + "values (default, ?, ?, ?, ?, ?)");
		n.setInt(1, news.getPrimary_id());
		n.setInt(2, news.getMeans_id());
		n.setInt(3, news.getSource_id());
		n.setString(4, news.getUrl());
		n.setBlob(5, news.getSecondary_data());
		n.execute();
		n.close();
		select = table.prepareStatement("SELECT * FROM " + tableref_secondary + " WHERE " + table_secondary + ".url=?");
		select.setString(1, news.getUrl());
		result = select.executeQuery();
		result.next();
		select.close();
		news.setSecondary_id(result.getInt(1));
		result.close();
		return news;
	}
}
