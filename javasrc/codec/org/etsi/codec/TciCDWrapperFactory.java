/**
 * @author	STF 424_ITS_Test_Platform
 * @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/codec/TciCDWrapperFactory.java $
 *             $Id: TciCDWrapperFactory.java 1423 2014-05-22 13:59:50Z filatov $
 */
package org.etsi.codec;



/** 
 * This class implements ITciCDWrapperFactory interface
 * 
 * @see ITciCDWrapperFactory
 * @category factory
 */
public class TciCDWrapperFactory implements ITciCDWrapperFactory{

	/** 
	 * Unique instance of this class
	 */
	private static ITciCDWrapperFactory _instanceFactory = (ITciCDWrapperFactory)new TciCDWrapperFactory();

	/** 
	 * Unique instance of TciCDWrapper class
	 * @see setImpl
	 */
	private static ITciCDWrapper _instance;

	/** 
	 * Default internal ctor
	 */
	private TciCDWrapperFactory() {
		//empty
	}

	/** 
	 * Singleton access method
	 * 
	 * @return A unique reference to this class
	 */
	public static ITciCDWrapperFactory getInstance() {
		return _instanceFactory;
	}

	/** 
	 * Singleton access method
	 * 
	 * @return A unique reference to the TciCDWrapper class
	 */
	public static ITciCDWrapper getTciCDInstance() {
		return _instance;
	}

	/**
	 * This method is used by Vendor to implement specific TEE part of code for TciCD
	 * 
	 * @param tcicd A n instance of TciCDWrapper
	 * @see ITciCDWrapper
	 */
	@Override
    public void setImpl(final ITciCDWrapper tcicd) {
		_instance = tcicd;
	}

} // End of class TciCDWrapperFactory
