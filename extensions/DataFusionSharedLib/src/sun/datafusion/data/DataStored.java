package sun.datafusion.data;

import java.util.Date;

/*******************************************************************************
 * DataStored represents the information generated from a DataMeans like a
 * specific RSS Feed item
 */
public class DataStored {

	/***************************************************************************
	 * Constructor that sets default values (NULL)
	 */
	public DataStored() {
		id = -1;
		DataMeans_id = -1;
		title = "";
		url = "";
		data = "";
		linkedUrl = "";
		linkedData = "";
		timestamp = new Date();
	}

	public DataStored(int id, int DataMeans_id, String title, String url,
			String data, String linkUrl, String linkedData, Date timestamp) {
		this.id = id;
		this.DataMeans_id = DataMeans_id;
		this.title = title;
		this.url = url;
		this.data = data;
		this.linkedUrl = linkUrl;
		this.linkedData = linkedData;
		this.timestamp = timestamp;
	}

	// --------------------------------------------------------------------------

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDataMeans_id() {
		return DataMeans_id;
	}

	public void setDataMeans_id(int dataMeans_id) {
		DataMeans_id = dataMeans_id;
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getLinkedUrl() {
		return linkedUrl;
	}

	public void setLinkedUrl(String linkedUrl) {
		this.linkedUrl = linkedUrl;
	}

	public String getLinkedData() {
		return linkedData;
	}

	public void setLinkedData(String linkedData) {
		this.linkedData = linkedData;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	// --------------------------------------------------------------------------

	private int id;
	private int DataMeans_id;
	private String title;
	private String url;
	private String data;
	private String linkedUrl;
	private String linkedData;
	private Date timestamp;
}
