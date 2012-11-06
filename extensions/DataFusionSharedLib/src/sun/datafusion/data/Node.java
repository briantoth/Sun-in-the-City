package sun.datafusion.data;

import java.util.LinkedList;
import java.util.List;

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
		tags = new LinkedList<String>();
	}
	
	//--------------------------------------------------------------------------
	
	public int getNodeID() {
		return nodeID;
	}
	public List<String> getTags() {
		return tags;
	}
	
	//--------------------------------------------------------------------------

	private int nodeID;
	private List<String> tags;
}
