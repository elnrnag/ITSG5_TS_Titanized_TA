/**
 *  Interface to port implementation
 * 
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/IPort.java $
 *              $Id: IPort.java 417 2011-04-20 15:12:06Z berge $
 *
 */
package org.etsi.its.adapter.ports;

/**
 *  Interface to port implementation
 */
public interface IPort {

	/**
	 * Gets the name of the component on which this port instance is mapped
	 * @return Component's name
	 */
	String getComponentName();

	/**
	 * Gets the name of the port
	 * @return Port's name
	 */
	String getPortName();

	/**
	 * Sends a message to SUT through the port
	 * @param  message encoded message to be sent
	 * @return true if send operation is successful, false otherwise
	 */
	boolean send(byte[] message);

	/**
	 * Deletes the port instance in a clean manner (stops background threads, ...)
	 */
	void dispose();

}
