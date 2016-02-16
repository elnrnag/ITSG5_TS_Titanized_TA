/**
 *  Pseudo layer that sends back what it receives from upper layer
 *  SHALL NOT BE INCLUDED IN RELEASE
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/test/LoopbackLayer.java $
 *              $Id: LoopbackLayer.java 1423 2014-05-22 13:59:50Z filatov $
 *
 */
package org.etsi.its.adapter.layers.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.etsi.its.adapter.IManagementLayers;
import org.etsi.its.adapter.layers.EthernetLayer;
import org.etsi.its.adapter.layers.Layer;

/**
 *  Pseudo layer that sends back what it receives from upper layer
 */
public class LoopbackLayer extends Layer {

    /**
     * Constructor
     * @param  management   Layer management instance
     * @param  lowerStack   Lower protocol stack   
     */
	public LoopbackLayer(IManagementLayers management, Stack<String> lowerStack) {
		super(management, lowerStack);
	}

	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#send(byte[], java.util.Map)
	 */
	@Override
	public boolean send(byte[] message, Map<String, Object> params) {
	    HashMap<String, Object> lowerInfo = new HashMap<String, Object>();
	    lowerInfo.put(EthernetLayer.LINK_LAYER_DESTINATION, new byte[] {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF});
		super.receive(message, lowerInfo);		
		return super.send(message, params);
	}
}
