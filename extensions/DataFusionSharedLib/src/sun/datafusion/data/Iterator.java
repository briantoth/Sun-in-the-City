package sun.datafusion.data;

import java.sql.ResultSet;

/*******************************************************************************
 * Returned by the manager to iterate through the items of a request
 */
public class Iterator <T>{

	/***************************************************************************
	 * Constructor that stores the needed result set and transformer
	 * @param resSet The result set of a db query to iterate
	 * @param transformer The transformer that turns RS to modeled objects
	 */
	public Iterator(ResultSet resSet, Transformer<T> transformer){
		this.resSet = resSet;
		this.transformer = transformer;
	}
	
	/***************************************************************************
	 * Gets the next item in the iterator
	 * @return The next item T in the iterator or null if it is done
	 */
	public T getNext(){
		return transformer.transform(resSet);
	}
	
	//--------------------------------------------------------------------------
	
	private ResultSet resSet;
	private Transformer<T> transformer;
}
