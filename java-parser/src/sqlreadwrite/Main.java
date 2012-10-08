package sqlreadwrite;

import sqlreadwrite.SourceTable;

public class Main {
	public static void main(String[] args) throws Exception {
	    SourceTable t = new SourceTable("newsdb","datameans","root","root");
	    NewsSource[] sources = t.getSources();
	  }
}
