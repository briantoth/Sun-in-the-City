package sqlreadwrite;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.Date;
import sqlreadwrite.NewsSource;


public class SourceTable {
	private Connection tableConnection = null;
	private Statement query = null;
	private ResultSet results = null;
	//private String database = null;
	//private String table = null;
	private String tableref = null;
	
	public SourceTable(String databaseName, String tableName, String user, String pw) throws Exception{
		try{
			//database = databaseName;
			//table = tableName;
			tableref = databaseName.toUpperCase() + "." + tableName.toUpperCase();
			Class.forName("com.mysql.jdbc.Driver");	
			tableConnection = DriverManager.getConnection("jdbc:mysql://localhost/" + databaseName + "?user=" + user + "&password=" + pw);
			query = tableConnection.createStatement();
			results = query.executeQuery("SELECT * FROM " + tableref);
		}
		catch(Exception e){
			throw e;
		}
	}
	
	public boolean executeQuery(String str) throws SQLException{
		return query.execute(str);
	}
	
	public NewsSource[] getSources() throws SQLException{
		NewsSource[] sources = new NewsSource[results.getFetchSize()];
		int i = 0;
		while(results.next()){
			//The following is ENITRELY RELIANT on the table structure for datameans.
			//If datameans is changed structurally, this code WILL break.
			sources[i] = new NewsSource();
			sources[i].setId(results.getInt(0));
			sources[i].setParent_id(results.getInt(1));
			sources[i].setUrl(results.getString(2));
			sources[i].setName(results.getString(3));
			sources[i].setType(results.getInt(4));
			i++;
		}
		return sources;
	}
}
