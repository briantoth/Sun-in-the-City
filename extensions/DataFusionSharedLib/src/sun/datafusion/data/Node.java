package sun.datafusion.data;

import java.util.ArrayList;
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
		tags = new ArrayList<String>();
	}
	
	//--------------------------------------------------------------------------
	
	public int getNodeID() {
		return nodeID;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	public String getTagsInOneString() {
		
		String oneString = new String();
		
		for(String tag: tags)
			oneString += " " + tag;
		
		return oneString;
	}
	
	public void addTag(String tag) {
		tags.add(tag);
	}
	
	//--------------------------------------------------------------------------

	private int nodeID;
	private List<String> tags;
}
