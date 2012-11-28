package sun.datafusion.parser;

import sun.datafusion.data.DataMeans;
import sun.datafusion.data.DataStored;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;  
import java.net.URL;  
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;  
import java.util.List; 
import java.util.Date;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;  
import com.sun.syndication.feed.synd.SyndFeed;  
import com.sun.syndication.io.SyndFeedInput;  
import com.sun.syndication.io.XmlReader;  

/***************************************************************************
 * The RSSGrabber object takes a DataMeans object and then is able to
 * parse all new RSS posts from the RSS feed contained in that DataMeans
 * object.
 * @author James McGuinness
 *
 */
public class RSSGrabber {
	private String feedUrl;
	private HttpURLConnection conn;
	private SyndFeed feed;
	//Unfortunately, ROME does not specify a return type object on the
	//lists it returns, so we must use a generic list and then cast it
	//to the appropriate type specified in its JDocs.
	private List posts;
	private Iterator postIter;
	private Date lastChecked;
	private int dataMeansId;
	
	
	/***************************************************************************
	 * Creates a new RSSGrabber object from a DataMeans object. Saves all
	 * necessary information as well as creating the needed HTTP connection
	 * to the RSS link given.
	 * 
	 * @param data The DataMeans object to retrieve new RSS posts from
	 * @throws Exception
	 */
	public RSSGrabber(DataMeans data) throws Exception{
		URL url = new URL(data.getUrl());
		conn = (HttpURLConnection)url.openConnection();
		SyndFeedInput in = new SyndFeedInput();
		feed = in.build(new XmlReader(conn));
		posts = feed.getEntries();
		postIter = posts.iterator();
		lastChecked = data.getLastProcessed();
		dataMeansId = data.getId();
		feedUrl = data.getUrl();
	}
	
	/***************************************************************************
	 * Grabs the entire HTML source text for a given url.
	 * @param The HTTP URL to grab the source text for
	 * @return The source text, stored in a string
	 * @throws IOException
	 */
	private String grabHTML(URL url) throws IOException{
		URLConnection spoof = url.openConnection();
		//This line will make the remote url think that we're a Firefox browser; this ensures
		//that the connection will not be blocked or otherwise discarded by the remote
		//machine.
		spoof.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)" );
		BufferedReader in = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
	  	String strLine = "";
	  	String returnstr = "";
	  	while ((strLine = in.readLine()) != null){
		  returnstr += (strLine + "\n");
	  	}
	  	in.close();
	  	return returnstr;
	}
	
	/***************************************************************************
	 * Gets the latest posts for this RSSGrabber object. Uses the date given
	 * at creation time to determine what posts are 'new'.
	 * 
	 * @return The latest posts for this RSSGrabber object
	 */
	public List<DataStored> getNewPosts(){
		ArrayList<DataStored> newPosts = new ArrayList<DataStored>();
		while(postIter.hasNext()){
			SyndEntry next = (SyndEntry)postIter.next();
			if(next.getPublishedDate().getTime() > lastChecked.getTime()){
				DataStored newData = new DataStored();
				newData.setUrl(feedUrl);
				newData.setDataMeans_id(dataMeansId);
				newData.setTitle(next.getTitle());
				newData.setLinkedUrl(next.getLink());
				SyndContent description = next.getDescription();
				if(description != null)
					newData.setData(description.getValue());
				try{
					newData.setLinkedData(grabHTML(new URL(next.getLink())));
				}
				catch(Exception e){
					newData.setLinkedData("");
				}
				newPosts.add(newData);
			}
		}
		return newPosts;
	}
}
