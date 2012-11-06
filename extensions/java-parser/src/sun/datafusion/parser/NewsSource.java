package sun.datafusion.parser;

public class NewsSource {
	//This class is just a shell right now; later it will have functions for reading
	//the actual data and checking if there are any updates to this source. TypeIDs will
	//have to be agreed on and normalized throughout the code.
	private String url;
	private String name;
	private int type;
	private int id;
	private int parent_id;
	
	//Generated functions by Eclipse.
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParent_id() {
		return parent_id;
	}
	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}
}
