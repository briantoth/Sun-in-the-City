package sqlreadwrite;

import java.util.Iterator;
import java.util.List;

import sun.datafusion.data.DataMeansTable;
import sun.datafusion.data.DataMeans;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.DataStoredTable;

public class Main {
	
	static int READER_TIMEOUT = 1000000; //1000 seconds, or about 17 minutes.
	static String DBNAME = "newsdb";
	static String DATA_MEANS_NAME = "DataMeans";
	static String DATA_STORED_NAME = "DataStored";
	static String USERNAME = "root";
	static String PASSWORD = "root";
	
	public static void main(String[] args) throws Exception {
		DataMeansTable t = new DataMeansTable(DBNAME,DATA_MEANS_NAME,USERNAME,PASSWORD);
		DataStoredTable dest = new DataStoredTable(DBNAME, DATA_STORED_NAME, USERNAME, PASSWORD);
		DataMeans next;
		while(true){
			next = t.getNextMeans();
			while(next != null){
				RSSGrabber grabber = new RSSGrabber(next);
				List<DataStored> newPosts = grabber.getNewPosts();
				Iterator<DataStored> iter = newPosts.iterator();
				while(iter.hasNext()){
					dest.insertDataStored(iter.next());
				}
			}
			Thread.sleep(READER_TIMEOUT);
		}
	}
}
