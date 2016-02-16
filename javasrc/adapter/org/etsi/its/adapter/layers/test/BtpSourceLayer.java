/**
 *  BTP source pseudo layer. It forwards a BTP message to upper layer every second
 *  SHALL NOT BE INCLUDED IN RELEASE
 *				
 *  @author	 ETSI / STF424
 *  @version	$URL: svn+ssh://vcs.etsi.org/TTCN3/TOOL/ITS_TestPlatform/trunk/javasrc/adapter/org/etsi/its/adapter/layers/test/CamSourceLayer.java $
 *			  $Id: CamSourceLayer.java 146 2011-04-21 11:33:57Z berge $
 *
 */
package org.etsi.its.adapter.layers.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.etsi.its.adapter.IManagementLayers;
import org.etsi.its.adapter.layers.GnLayer;

/**
 *  BTP source pseudo layer. It forwards a BTP message to upper layer every second
 */
public class BtpSourceLayer extends SourceLayer {

	/**
	 * Constructor
	 * @param  management   Layer management instance
	 * @param  lowerStack   Lower protocol stack   
	 */
	public BtpSourceLayer(IManagementLayers management, Stack<String> lowerStack) {
		super(management, lowerStack);
		// Do not forget to modify line 40/41 when switching from BTP-A to BTP-B
		//message = _btpa/*_btpb*/;
		message = _btpb_payload/*_btpa_payload*/;
	}
	
	@Override
	public void run() {
		while(running) {
			Map<String, Object>lowerInfo = new HashMap<String, Object>();
			//lowerInfo.put(GnLayer.GN_NEXTHEADER, 1); // BTP-A
			lowerInfo.put(GnLayer.GN_NEXTHEADER, 2); // BTP-B
			super.receive(message, lowerInfo);
			try {
				Thread.sleep(SOURCE_INTERVAL); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}	

	/**
	 * BTP A message
	 * 
	 * This header causes a TC failure because of payload expected
	 */
	@SuppressWarnings("unused")
	private byte[] _btpa = new byte[]{
		(byte)0x02, (byte)0x3c, // destinationPort: 572
		(byte)0x02, (byte)0x3b // sourcePort: 571
	};

	/**
	 * BTP A message
	 */
	@SuppressWarnings("unused")
	private byte[] _btpa_payload = new byte[]{
		(byte)0x02, (byte)0x3c, // destinationPort: 572
		(byte)0x02, (byte)0x3b, // sourcePort: 571
		(byte)0xde, (byte)0xce, (byte)0xa5, (byte)0xed
	};

	/**
	 * BTP B message
	 * 
	 * This header causes a TC failure because of payload expected
	 */
	@SuppressWarnings("unused")
	private byte[] _btpb = new byte[]{
		(byte)0x02, (byte)0x3c, // destinationPort: 572
		(byte)0x02, (byte)0x3d // destinationPortInfo: 573
	};

	/**
	 * BTP B message
	 */
	private byte[] _btpb_payload = new byte[]{
		(byte)0x02, (byte)0x3c, // destinationPort: 572
		(byte)0x02, (byte)0x3d, // destinationPortInfo: 573
		(byte)0xbe, (byte)0xef, (byte)0xde, (byte)0xad
	};

} // End of class BtpSourceLayer
