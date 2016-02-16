/**
 *  Factory for Test Execution Required implementations. 
 *  Implementations have to register to this factory.  
 * 
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/adapter/TERFactory.java $
 *              $Id: TERFactory.java 749 2012-01-23 15:23:04Z tepelmann $
 *
 */
package org.etsi.adapter;

/**
 *  Factory for Test Execution Required implementations. 
 *  Implementations have to register to this factory.  
 */
public class TERFactory {

	/**
	 * Registered ITERequired implementation
	 */
	private static ITERequired instance; 

	/**
	 * Gets the registered ITERequired implementation
	 * @return Instance of ITERequired implementation registered through setImpl() or null
	 * @see    setImpl
	 */
	public static ITERequired getInstance() {
		return instance;
	}
	
	/**
	 * Private constructor (Singleton pattern) 
	 */
	private TERFactory() {
		//empty
	}
	
	/**
	 * Sets the implementation instance to be returned by the factory
	 * @param  impl    Instance of the implementation to be registered
	 */
	public static void setImpl(ITERequired impl) {
		instance = impl;
	}
}
