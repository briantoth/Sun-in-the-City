package sun.datafusion.parser;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import sun.datafusion.data.DataMeans;
import sun.datafusion.data.DataStored;
import sun.datafusion.data.Manager;

/***************************************************************************
 * The base RSS Parsing object. Takes the needed parameters for logging into
 * the MySQL server with the DataMeans information needed for parsing.
 * Uses libraries in the sun.datafusion.data package; specifically, Manager,
 * DataMeans, and DataStored.
 * @author James McGuinness
 *
 */
public class RSSParser {
	
	String DBNAME;
	String USERNAME;
	String PASSWORD;
	String SERVER_IP;
	Manager p;
	
	/***************************************************************************
	 * Creates an RSSParser object, and connects to the MySQL database with
	 * the given parameters immediately.
	 * @param The database name
	 * @param Username to log in with
	 * @param Password to log in with
	 * @param The MySQL server ip (usually localhost)
	 */
	public RSSParser(String dbname, String username, String password, String server_ip){
		DBNAME = dbname;
		USERNAME = username;
		PASSWORD = password;
		SERVER_IP = server_ip;
		p = new Manager(SERVER_IP, DBNAME, USERNAME, PASSWORD);
	}
	
	/***************************************************************************
	 * Execute one 'pass' over the DataMeans MySQL table, parsing all new
	 * RSS posts and adding them to the DataStored MySQL table.
	 * If a DataMeans cannot be parsed, it will be skipped.
	 */
	public void parse(){
		DataMeans next;
		//Get the list of sources to parse from the MySQL table.
		List<DataMeans> means = p.getDataMeansToProcess(new Date());
		Iterator<DataMeans> mean_iter = means.iterator();
		while(mean_iter.hasNext()){
			try{
				//Parse this source.
				next = mean_iter.next();
				RSSGrabber grabber = new RSSGrabber(next);
				List<DataStored> newPosts = grabber.getNewPosts();
				//Update the source's timestamp.
				p.setDataMeansProcessed(next);
				Iterator<DataStored> iter = newPosts.iterator();
				//Store the returned DataStored objects in the target MySQL table.
				while(iter.hasNext()){
					p.createDataStored(iter.next());
				}
			} catch(Exception e){
				//If there was an error, write an error, but keep parsing.
				continue;
			}
		}
	}
}
