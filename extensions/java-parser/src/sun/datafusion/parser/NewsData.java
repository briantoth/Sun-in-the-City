package sun.datafusion.parser;

import java.sql.Blob;

public class NewsData {
	private String url;
	private String name;
	private int type;
	private int source_id;
	private int means_id;
	private int primary_id;
	private int secondary_id;
	private Blob primary_data;
	private Blob secondary_data;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getSource_id() {
		return source_id;
	}
	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}
	public int getMeans_id() {
		return means_id;
	}
	public void setMeans_id(int means_id) {
		this.means_id = means_id;
	}
	public int getPrimary_id() {
		return primary_id;
	}
	public void setPrimary_id(int primary_id) {
		this.primary_id = primary_id;
	}
	public int getSecondary_id() {
		return secondary_id;
	}
	public void setSecondary_id(int secondary_id) {
		this.secondary_id = secondary_id;
	}
	public Blob getPrimary_data() {
		return primary_data;
	}
	public void setPrimary_data(Blob primary_data) {
		this.primary_data = primary_data;
	}
	public Blob getSecondary_data() {
		return secondary_data;
	}
	public void setSecondary_data(Blob secondary_data) {
		this.secondary_data = secondary_data;
	}
	
	
}
