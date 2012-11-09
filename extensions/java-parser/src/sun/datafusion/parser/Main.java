package sun.datafusion.parser;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import sun.datafusion.data.DataMeans;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.Manager;

public class Main {
	
	static long READER_TIMEOUT = 1000000; //1000 seconds, or about 17 minutes.
	static String DBNAME = "sun_in_the_city";
	static String DATA_MEANS_NAME = "DataMeans";
	static String DATA_STORED_NAME = "DataStored";
	static String USERNAME = "root";
	static String PASSWORD = "root";
	static String URL = "localhost";
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starting parser!");
		Manager p = new Manager(URL, DBNAME, USERNAME, PASSWORD);
		DataMeans next;
		while(true){
			System.out.println("Getting sources...");
			List<DataMeans> means = p.getDataMeansToProcess(new Date());
			Iterator<DataMeans> mean_iter = means.iterator();
			while(mean_iter.hasNext()){
				//System.out.println("Parsing source...");
				next = mean_iter.next();
				RSSGrabber grabber = new RSSGrabber(next);
				List<DataStored> newPosts = grabber.getNewPosts();
				//System.out.println("Updating source timestamp...");
				p.setDataMeansProcessed(next);
				Iterator<DataStored> iter = newPosts.iterator();
				//System.out.println("Creating table entries...");
				while(iter.hasNext()){
					p.createDataStored(iter.next());
				}
			}
			System.out.println("Sources parsed, sleeping...");
			Thread.sleep(READER_TIMEOUT);
		}
	}
}
