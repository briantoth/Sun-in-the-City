package sun.datafusion.data;

/*******************************************************************************
 * DataSource represents a DataPrimary or DataSecondary in the MySQL database.
 * This class contains the necessary information needed to index the data as
 * well as update the database once the indexing is complete.
 */
public class DataSource {
	// TODO
	
	//--------------------------------------------------------------------------
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public String getLogourl() {
		return logourl;
	}
	public void setLogourl(String logourl) {
		this.logourl = logourl;
	}
	
	//--------------------------------------------------------------------------
	
	private int id;
	private String url;
	private String name;
	private String logourl;
}
