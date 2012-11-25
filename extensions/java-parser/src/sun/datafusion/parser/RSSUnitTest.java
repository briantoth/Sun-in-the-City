package sun.datafusion.parser;

import sun.datafusion.data.DataMeans;
import sun.datafusion.data.DataStored;

import java.util.Date;
import java.util.List;

public class RSSUnitTest {
	public static void main(String [] args){
		Date oneDayAgo = new Date((new Date()).getTime() - (24*60*60*1000));
		
		DataMeans test1 = new DataMeans(0, 0, "Joystiq", "http://www.joystiq.com/rss.xml", 1, oneDayAgo);
		DataMeans test2 = new DataMeans(1, 1, "Associated Press", "http://hosted2.ap.org/atom/APDEFAULT/3d281c11a96b4ad082fe88aa0db04305", 1, oneDayAgo);
		DataMeans test3 = new DataMeans(2, 2, "The New York Times", "http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml", 1, oneDayAgo);
		
		RSSGrabber one = null;
		RSSGrabber two = null;
		RSSGrabber three = null;
		try {
			one = new RSSGrabber(test1);
			two = new RSSGrabber(test2);
			three = new RSSGrabber(test3);
		} catch (Exception e) {
			System.err.println("There was a problem initializing the RSSGrabber objects.");
			System.err.println(e);
			e.printStackTrace();
		}
		
		List<DataStored> result1 = null;
		List<DataStored> result2 = null;
		List<DataStored> result3 = null;
		
		try {
			result1 = one.getNewPosts();
			result2 = two.getNewPosts();
			result3 = three.getNewPosts();
		} catch (Exception e){
			System.err.println("There was a problem retrieving new posts.");
			System.err.println(e);
			e.printStackTrace();
		}
		
		if(result1.size() == 0){
			System.err.println("The first RSS feed did not retrieve any posts. This may be normal.");
		}
		if(result2.size() == 0){
			System.err.println("The second RSS feed did not retrieve any posts. This may be normal.");
		}
		if(result3.size() == 0){
			System.err.println("The third RSS feed did not retrieve any posts. This may be normal.");
		}
		
		System.out.println("Unit test finished.");
	}
}
