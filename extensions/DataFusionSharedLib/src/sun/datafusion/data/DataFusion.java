package sun.datafusion.data;

import java.util.Date;

/*******************************************************************************
 * DataFusion represents the outside data information that is displayed below
 * an article written by the client. This could be generated manual by the
 * client (writer / editor) or by the automatic algorithm
 */
public class DataFusion{
	
	/***************************************************************************
	 * Constructor that sets default values (NULL)
	 */
	public DataFusion(){
		id = -1;
		nodeID = -1;
		DataSource_id = -1;
		DataStored_id = -1;
		title = "";
		url = "";
		summary = "";
		approved = false;
		rating = -1;
		timestamp = new Date();
	}
	
	//--------------------------------------------------------------------------

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNodeID() {
		return nodeID;
	}
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
	public int getDataSource_id() {
		return DataSource_id;
	}
	public void setDataSource_id(int dataSource_id) {
		DataSource_id = dataSource_id;
	}
	public int getDataStored_id() {
		return DataStored_id;
	}
	public void setDataStored_id(int dataStored_id) {
		DataStored_id = dataStored_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public boolean isApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	//--------------------------------------------------------------------------
	
	private int id;
	private int nodeID;
	private int DataSource_id;
	private int DataStored_id;
	private String title;
	private String url;
	private String summary;
	private boolean approved;
	private int rating;
	private Date timestamp;
}
