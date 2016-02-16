/**
 *  Implementations of IUT specific settings for Ethernet support
 *  
 *  @author     ETSI / STF424
 *  @version    $URL:$
 *              $Id:$
 *
 */
package org.etsi.its.adapter.layers;

/**
 * Implementations of IUT specific settings for Ethernet support
 */
public interface IEthernetSpecific {

	/**
	 * Gets the Ethernet frame type for upper layer packets
	 * @return The Ethernet frame type value
	 */
	public short getEthernetType();

} // End of interface IEthernetSpecific
