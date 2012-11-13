package sun.datafusion.parser;

import java.util.Properties;

/***************************************************************************
 * This is the main class for running the parser, executed at runtime.
 * Reads a properties file and then executes RSS Parsing, sleeping
 * for a given amount of time defined in the properties file.
 * For more information about the properties file, see the documentation
 * on the main method in this class.
 * @author James McGuinness
 *
 */
public class Main {
	
	static long READER_TIMEOUT;
	static String DBNAME;
	static String USERNAME;
	static String PASSWORD;
	static String SERVER_IP;
	/***************************************************************************
	 * The main method to run the parser. Reads a properties file 
	 * (named .properties) and looks up the following information:
	 * READER_TIMEOUT: The amount of time (in millis) to sleep after parsing.
	 * DBNAME: The database name for the MySQL server to query.
	 * USERNAME: The username to log into the MySQL server with.
	 * PASSWORD: The password to log into the MySQL server with.
	 * SERVER_IP: The IP of the server, generally localhost.
	 * @param The base command line arguments, unused.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Properties properties = sun.datafusion.utils.PropertyUtils.loadProperties();
		//Default timeout is 1000 seconds, or about 17 minutes.
		READER_TIMEOUT = Long.parseLong(properties.getProperty("READER_TIMEOUT", "1000000"));
		DBNAME = properties.getProperty("DBNAME","sun_in_the_city");
		USERNAME = properties.getProperty("USERNAME","root");
		PASSWORD = properties.getProperty("PASSWORD","root");
		SERVER_IP = properties.getProperty("SERVER_IP","localhost");
		RSSParser parser = new RSSParser(DBNAME, USERNAME, PASSWORD, SERVER_IP);
		while(true){
			parser.parse();
			Thread.sleep(READER_TIMEOUT);
		}
	}
}
