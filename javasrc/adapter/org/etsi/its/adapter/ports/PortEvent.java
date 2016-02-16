/**
 *  Event transmitted by ports as observable objects to observers
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/PortEvent.java $
 *              $Id: PortEvent.java 633 2011-08-17 10:02:33Z berge $
 *
 */
package org.etsi.its.adapter.ports;

/**
 *  Event transmitted by ports as observable objects to observers
 */
public class PortEvent {

	/** 
	 * Byte data to transmit
	 */
	private byte[] _message;
	
	/** 
	 * Name of the port
	 */
	private String _portName;
	
	/** 
	 * Name of the component owner; needed for enqueueing message
	 */
	private String _componentName;

	/** 
	 * Constructor
	 * @param  message         Data to be transmitted
	 * @param receptionTime    Message reception time
	 * @param  portName        Name of the port
	 * @param  componentName   Name of the component owning the port instance   
	 */
	public PortEvent(final byte[] message, String portName, String componentName) {
		_message = message;
		_portName = portName;
		_componentName = componentName;
	}

	/** 
	 * Gets the data buffer
	 * @return Data as a byte array
	 */
	public byte[] get_message() {
		return _message;
	}

	/** 
	 * Gets the port name 
	 * @return Name of the port
	 */
	public String getPortName() {
		return _portName;
	}

	/** 
	 * Gets the component name 
	 * @return Name of the component
	 */
	public String getComponentName() {
		return _componentName;
	}
}
