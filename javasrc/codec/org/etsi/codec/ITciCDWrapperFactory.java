/**
 * @author	STF 424_ITS_Test_Platform
 * @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/codec/ITciCDWrapperFactory.java $
 *             $Id: ITciCDWrapperFactory.java 747 2012-01-23 14:13:47Z tepelmann $
 */
package org.etsi.codec;



/** This interface provides mandatory method to be used by Vendor to implement specific TEE part of code for TciCD
 * 
 * See ETSI ES 201 873-6 V4.2.1 - Clause 7.3.2.1 TCI-CD required
 */
public interface ITciCDWrapperFactory {

	/**
	 * This method is used by Vendor to implement specific TEE part of code for TciCD
	 * 
	 * @param tcicd A n instance of TciCDWrapper
	 * @see ITciCDWrapper
	 */
	public void setImpl(final ITciCDWrapper tcicd);

} // End of interface ITciCDWrapperFactory
