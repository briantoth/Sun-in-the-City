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

public class RSSGrabber {
	private String feedUrl;
	private HttpURLConnection conn;
	private SyndFeed feed;
	private List posts;
	private Iterator postIter;
	private Date lastChecked;
	private int dataMeansId;
	
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
	
	private String grabHTML(URL url) throws IOException{
		URLConnection spoof = url.openConnection();
		spoof.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)" );
		BufferedReader in = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
	  	String strLine = "";
	  	String returnstr = "";
	  	while ((strLine = in.readLine()) != null){
		  returnstr += (strLine + "\n");
	  	}
	  	return returnstr;
	}
	
	public List<DataStored> getNewPosts(){
		ArrayList<DataStored> newPosts = new ArrayList<DataStored>();
		while(postIter.hasNext()){
			SyndEntry next = (SyndEntry)postIter.next();
			if(next.getPublishedDate().compareTo(lastChecked) > 0){
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
