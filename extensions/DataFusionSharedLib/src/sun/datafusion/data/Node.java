package sun.datafusion.data;

/*******************************************************************************
 * Represents an article (node)
 */
public class Node  {

	/***************************************************************************
	 * Constructor that sets default values (NULL)
	 * @param nodeID The ID of the node
	 */
	public Node(int nodeID){
		this.nodeID = nodeID;
		tags = "";
	}
	
	//--------------------------------------------------------------------------
	
	public int getNodeID() {
		return nodeID;
	}
	public String getTags() {
		return tags;
	}
	
	public void addTag(String tag) {
		tags += " " + tag;
	}
	
	//--------------------------------------------------------------------------

	private int nodeID;
	private String tags;
}
