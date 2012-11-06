package sun.datafusion.data;

/*******************************************************************************
 * Represents an article (node)
 */
public class Node  {

	/***************************************************************************
	 * Constructor that sets default values (NULL)
	 */
	public Node(){
		nodeID = -1;
		tags = "";
	}
	
	//--------------------------------------------------------------------------
	
	public int getNodeID() {
		return nodeID;
	}
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	//--------------------------------------------------------------------------

	private int nodeID;
	private String tags;
}
