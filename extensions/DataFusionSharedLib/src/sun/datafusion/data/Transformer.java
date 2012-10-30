package sun.datafusion.data;

import java.sql.ResultSet;

/*******************************************************************************
 * Transforms the data from a query to a modeled object
 */
public abstract class Transformer<T> {

	/***************************************************************************
	 * Returns the type of object given by the result set query and modeled java
	 * object type T
	 */
	public abstract T transform(ResultSet resSet);


}
