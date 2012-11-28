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
	/***************************************************************************
	 * The main method to run the parser. Reads a properties file 
	 * (named .properties) and looks up the following information:
	 * reader_timeout: The amount of time (in millis) to sleep after parsing.
	 * db: The database name for the MySQL server to query.
	 * user: The username to log into the MySQL server with.
	 * pass: The password to log into the MySQL server with.
	 * hostname: The IP of the server, generally localhost.
	 * @param args The base command line arguments, unused.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Properties properties = sun.datafusion.utils.PropertyUtils.loadProperties();
		sun.datafusion.utils.PropertyUtils.loadLoggingProperties();
		//Default timeout is 1000 seconds, or about 17 minutes.
		READER_TIMEOUT = Long.parseLong(properties.getProperty("reader_timeout", "1000000"));
		RSSParser parser = new RSSParser(properties);
		while(true){
			parser.parse();
			Thread.sleep(READER_TIMEOUT);
		}
	}
}
