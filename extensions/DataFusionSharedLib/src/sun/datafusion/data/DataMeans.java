package sun.datafusion.data;

import java.util.Date;

/*******************************************************************************
 * DataMeans represents the entity that generates information like
 * The New York Times RSS Feed
 */
public class DataMeans{

	/***************************************************************************
	 * Constructor that sets default values (NULL)
	 */
	public DataMeans(){
		id = -1;
		DataSource_id = -1;
		name = "";
		url = "";
		type = -1;
		lastProcessed = null;
	}
	
	/**
	 * Constructor that takes non-default values.  Used when creating
	 *  an in-memory object to correspond to an entry in the database
	 * 
	 * @param id The ID
	 * @param DataSource_id ID of the data source that owns this means
	 * @param name Name of the data means (twitter, rss, etc.)
	 * @param url Direct link to this means
	 * @param type An indicator number to keep track of the type of data means
	 * @param lastProcessed
	 */
	public DataMeans(int id, int DataSource_id, String name, String url, int type, Date lastProcessed){
		this.id = id;
		this.DataSource_id = DataSource_id;
		this.name = name;
		this.url = url;
		this.type = type;
		this.lastProcessed = lastProcessed;
	}
	
	//--------------------------------------------------------------------------
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDataSource_id() {
		return DataSource_id;
	}
	public void setDataSource_id(int dataSource_id) {
		DataSource_id = dataSource_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Date getLastProcessed() {
		return lastProcessed;
	}
	public void setLastProcessed(Date lastProcessed) {
		this.lastProcessed = lastProcessed;
	}
	
	//--------------------------------------------------------------------------
	
	private int id;
	private int DataSource_id;
	private String name;
	private String url;
	private int type;
	private Date lastProcessed;
}
