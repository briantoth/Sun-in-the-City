package sqlreadwrite;

import java.util.Set;

import sqlreadwrite.SourceTable;

public class Main {
	public static void main(String[] args) throws Exception {
	    SourceTable t = new SourceTable("newsdb","datameans","root","root");
	    Set<NewsSource> sources = t.getSources();
	    //sources.connect() (will get all of the sources connected and reading the URLs assigned)
	    //This will require some method of determining what sources to actually GET- my idea
	    //right now is to only look at sources newer than the time of program startup, thus ensuring that
	    //duplicates won't be placed in the destination table.
	    //threadpool.start() (start a thread pool to get sources going)
	    //while(true)
	    //	if(sources.hasNew())
	    //		threadpool.assign(sources.compileNewNews())
	    t.close();
	  }
}
