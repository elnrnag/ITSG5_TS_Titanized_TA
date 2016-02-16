/**
 *  Pseudo layer that prints what it receives from upper layer
 *  SHALL NOT BE INCLUDED IN RELEASE
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/test/DebugLayer.java $
 *              $Id: DebugLayer.java 748 2012-01-23 14:51:03Z tepelmann $
 *
 */
package org.etsi.its.adapter.layers.test;

import java.util.Map;
import java.util.Stack;

import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.IManagementLayers;
import org.etsi.its.adapter.layers.Layer;

/**
 *  Pseudo layer that prints what it receives from upper layer
 */
public class DebugLayer extends Layer {

    /**
     * Constructor
     * @param  management   Layer management instance
     * @param  lowerStack   Lower protocol stack   
     */
	public DebugLayer(IManagementLayers management, Stack<String> lowerStack) {
		super(management, lowerStack);
	}

	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#send(byte[], java.util.Map)
	 */
	@Override
	public boolean send(byte[] message, Map<String, Object> params) {
		
		ByteHelper.dump("Sending: ", message);
		return true;
	}
}
