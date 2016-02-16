/**
 *  Layer abstract class implementing generic behaviour of a protocol layer
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/Layer.java $
 *              $Id: Layer.java 633 2011-08-17 10:02:33Z berge $
 *
 */
package org.etsi.its.adapter.layers;

import java.util.Map;
import java.util.Stack;

import org.etsi.its.adapter.IManagementLayers;

/**
 *  Layer abstract class implementing generic behaviour of a protocol layer
 */
public abstract class Layer {

    /**
     * Parameter name for reception timestamp
     */
    public static final String RECEPTION_TIMESTAMP = "ReceptionTimestamp";
 
    
	/**
	 * Constructor
	 * @param  management   Layer management instance
     * @param  lowerStack   Lower protocol stack   
	 */
	public Layer(IManagementLayers management, Stack<String> lowerStack) {
		this.management = management;
		this.lowerStack = lowerStack;
		this.initialized = false;
	}
	
	/**
	 * Initialises lower stack
	 */
	protected void initialize() {
		if(!initialized && lowerStack != null && !lowerStack.isEmpty()) {
			lowerLayerName = lowerStack.pop();
			lowerLayer = LayerFactory.getInstance().createLayer(management, lowerLayerName, lowerStack);
			lowerLayer.register(this);
		}
		initialized = true;
	}
	
	/**
	 * Transmits a message to registered lower layer after encapsulation
     * @param  message Encoded message to be sent
     * @param  params  Layer parameters for sending message
	 * @return true if send operation is successful, false otherwise
	 */
	public boolean send(byte[] message, Map<String, Object> params) {
		if(lowerLayer != null) {
			return lowerLayer.send(message, params);
		}
		return true;
	}
	
	/**
	 * Callback method invoked by registered lower layer upon message reception from lower stack
	 * @param  message       Message received from lower layer
	 * @param  lowerInfo     Additional information transmitted by lower layers
	 * @param receptionTime  Message reception time
	 */
	public void receive(byte[] message, Map<String, Object> lowerInfo) {
		if(registeredUpperLayer != null) {
			registeredUpperLayer.receive(message, lowerInfo);
		}
	}
	
	/**
	 * Registers an upper layer. 
	 * This method will also cause current layer to initialise its lower stack
	 * Messages received from lower layer will now be transmitted to upper layer, if necessary
	 * @param  upperLayer  Instance of the upper layer
	 */
	public void register(Layer upperLayer) {
		registeredUpperLayer = upperLayer;
		initialize();
	}
	
	/**
	 * Unregisters upper layer.
	 * This method will also cause current layer to unregister from its lower layer
	 * @param upperLayer
	 */
	public void unregister(Layer upperLayer) {
		registeredUpperLayer = null;
		if(lowerLayer != null) {
			lowerLayer.unregister(this);
		}
	}
	
	/**
	 * Name of the lower layer
	 */
	protected String lowerLayerName;
	
	/**
	 * Registered upper layer instance
	 */
	protected Layer registeredUpperLayer = null;
	
	/**
	 * Lower layer instance
	 */
	protected Layer lowerLayer = null;
	
	/**
	 * Management instance
	 */
	protected IManagementLayers management = null;
	
	/**
	 * Lower stack
	 */
	private Stack<String> lowerStack;
	
	/**
	 * Set to true if lower stack has been initialised (true if layer is operational), false otherwise
	 */
	private boolean initialized;
}
