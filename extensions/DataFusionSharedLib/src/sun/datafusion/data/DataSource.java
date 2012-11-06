package sun.datafusion.data;

/*******************************************************************************
 * DataSource represents a source of information on the Internet like 
 * The New York Times
 */
public class DataSource{
	
	/***************************************************************************
	 * Constructor that sets default values (NULL)
	 */
	public DataSource(){
		id = -1;
		url = "";
		name = "";
		logourl = "";
	}
	
	public DataSource(int id, String url, String name, String logourl){
		this.id = id;
		this.url = url;
		this.name = name;
		this.logourl = logourl;
	}
	
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
