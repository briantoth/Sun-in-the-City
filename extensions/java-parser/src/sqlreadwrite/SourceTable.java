package sqlreadwrite;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.Date;
import sqlreadwrite.NewsSource;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


public class SourceTable {
	private Connection tableConnection = null;
	private Statement query = null;
	private ResultSet results = null;
	//private String database = null;
	//private String table = null;
	private String tableref = null;
	
	public SourceTable(Properties prop, String tableName) throws Exception{
		this(prop.getProperty("db"), tableName, prop.getProperty("dbuser"), 
				prop.getProperty("dbpassword"));
	}
	
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
	
	public Set<NewsSource> getSources() throws SQLException{
		Set<NewsSource> sources = new HashSet<NewsSource>();
		while(results.next()){
			//The following is ENITRELY RELIANT on the table structure for datameans.
			//If datameans is changed structurally, this code WILL break.
			NewsSource next = new NewsSource();
			next.setId(results.getInt(0));
			next.setParent_id(results.getInt(1));
			next.setUrl(results.getString(2));
			next.setName(results.getString(3));
			next.setType(results.getInt(4));
			sources.add(next);
		}
		return sources;
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
