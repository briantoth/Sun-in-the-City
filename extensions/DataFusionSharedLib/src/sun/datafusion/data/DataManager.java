package sun.datafusion.data;

/*******************************************************************************
 * Abstract method of managing interacting with data
 */
public abstract class DataManager {
	// TODO

	public abstract boolean create(Object obj);

	public abstract boolean read(Object obj);

	public abstract boolean update(Object obj);

	public abstract boolean delete(Object obj);
	
}
