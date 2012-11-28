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
	
	/**
	 * Returns a list of the tags
	 * 
	 * @return A list of the tags as strings
	 */
	public List<String> getTags() {
		return tags;
	}
	
	/**
	 * Concatenates all of the tags into a space-delimited
	 * string
	 * 
	 * @return Returns the tags as a single string
	 */
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
