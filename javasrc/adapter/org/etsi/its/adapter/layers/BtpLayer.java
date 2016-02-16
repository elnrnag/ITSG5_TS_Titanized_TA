/**
 *  Implementation of ITS Basic Transport Protocol layer 
 * 
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/BtpLayer.java $
 *              $Id: BtpLayer.java 2230 2015-06-03 09:11:02Z mullers $
 *
 */
package org.etsi.its.adapter.layers;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.IManagementLayers;

/**
 *  Implementation of ITS Basic Transport Protocol layer 
 */
public class BtpLayer extends Layer {

    /**
     * Parameter name for BTP packet type
     */
	public static final String BTP_TYPE = "BtpType";
	
    /**
     * Parameter name for BTP destination port
     */	
	public static final String BTP_DSTPORT = "BtpDstPort";

    /**
     * Parameter name for BTP source port
     */	
	public static final String BTP_SRCPORT = "BtpSrcPort";
	
    /**
     * Parameter name for BTP destination port information
     */	
	public static final String BTP_DSTPORTINFO = "BtpDstPortInfo";
	
    /**
     * BTP packet type A
     */
	public static final int TYPE_A = 0;
	
    /**
     * BTP packet type B
     */	
	public static final int TYPE_B = 1;
	
    /**
     * Constructor
     * @param  management   Layer management instance
     * @param  lowerStack   Lower protocol stack   
     */
	public BtpLayer(IManagementLayers management, Stack<String> lowerStack) {
		super(management, lowerStack);
	}

	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#send(byte[], java.util.Map)
	 */
	@Override
	public boolean send(byte[] message, Map<String, Object> params) {
		
		// Destination Port (16 bits)
		int dstPort;
		try {
			dstPort = (Integer)params.get(BTP_DSTPORT);
		}
		catch (NullPointerException e) {
			dstPort = 0;
		}		
		byte[] encapsulated = ByteHelper.intToByteArray(dstPort, 2);
		
		if(params.get(BTP_TYPE).equals(TYPE_A)) {
			// Source Port (16 bits)
			int srcPort;
			try {
				srcPort = (Integer)params.get(BTP_SRCPORT);
			}
			catch (NullPointerException e) {
				srcPort = 0;
			}
			encapsulated = ByteHelper.concat(encapsulated, ByteHelper.intToByteArray(srcPort, 2));
		}
		else {
			// Destination port info (16 bits)
			int dstPortInfo;
			try {
				dstPortInfo = (Integer)params.get(BTP_DSTPORTINFO);
			}
			catch (NullPointerException e) {
				dstPortInfo = 0;
			}
			encapsulated = ByteHelper.concat(encapsulated, ByteHelper.intToByteArray(dstPortInfo, 2));
		}

		// Update params
//		if(lowerLayerName != null && lowerLayerName.equals("GN")) {
//			params.put(GnLayer.GN_NEXTHEADER, "BTP-A"); // TODO Alex to confirm removal
//		}
		
		return super.send(ByteHelper.concat(encapsulated, message), params);		
	}
	
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#receive(byte[])
	 */
	@Override
	public void receive(byte[] message, Map<String, Object> lowerInfo) {
	
		byte[] dstPort = new byte[2];
		System.arraycopy(message, 0, dstPort, 0, 2);
		
		byte[] srcPort = new byte[2];
		System.arraycopy(message, 2, srcPort, 0, 2);
		
		int payloadLength = message.length - 4;
		byte[] payload = new byte[payloadLength];
		System.arraycopy(message, 4, payload, 0, payloadLength);
		
        lowerInfo.put(BTP_DSTPORT, dstPort);
        lowerInfo.put(BTP_DSTPORTINFO, srcPort);
		
		super.receive(payload, lowerInfo);
	}
}
