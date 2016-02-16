/**
 *  GeoNetworking source pseudo layer. It forwards a GN message to upper layer every second
 *  SHALL NOT BE INCLUDED IN RELEASE
 *                
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/test/GnSourceLayer.java $
 *              $Id: GnSourceLayer.java 469 2011-05-02 14:17:46Z garciay $
 *
 */
package org.etsi.its.adapter.layers.test;

import java.util.Stack;

import org.etsi.its.adapter.IManagementLayers;

/**
 *  GeoNetworking source pseudo layer. It forwards a GN message to upper layer every second
 */
public class GnSourceLayer extends SourceLayer {

	/**
	 * Constructor
	 * @param  management   Layer management instance
	 * @param  lowerStack   Lower protocol stack   
	 */
	public GnSourceLayer(IManagementLayers management, Stack<String> lowerStack) {
		super(management, lowerStack);
		message = lsRequest/*beacon*/;
	}
	
	/**
	 * Beacon message
	 */
	@SuppressWarnings("unused")
	private byte[] beacon = new byte[]{
		// CommonHdr
		(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, 
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xff, 
				// SEPV
					// GN_ADDR
		(byte)0xcc, (byte)0x2a, (byte)0xab, (byte)0xcd, 
		(byte)0xef, (byte)0xab, (byte)0xcd, (byte)0xe2, 
					// TST
		(byte)0x4a, (byte)0x27, (byte)0x87, (byte)0x79, 
					// Lat
		(byte)0x00, (byte)0x00, (byte)0x7e, (byte)0xca, 
					// Long
		(byte)0x00, (byte)0x00, (byte)0x30, (byte)0xaa, 
					// Speed + Heading
		(byte)0x00, (byte)0x00, (byte)0x30, (byte)0xaa,
					// Alt + acc
		(byte)0x00, (byte)0x00, (byte)0x30, (byte)0xaa
	};

	/**
	 * LS-Request
	 */
	private byte[] lsRequest = new byte[]{
			// CommonHdr
			(byte)0x00, (byte)0x60, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF,
				// SEPV
					// GN_ADDR
			(byte)0x00, (byte)0x42, (byte)0xDE, (byte)0xAD,
			(byte)0xBE, (byte)0xEF, (byte)0x00, (byte)0x01, 
					// TST
			(byte)0x54, (byte)0x84, (byte)0x75, (byte)0x10,
					// Lat
			(byte)0x42, (byte)0x54, (byte)0x00, (byte)0x00,
					// Long
			(byte)0x07, (byte)0x54, (byte)0x00, (byte)0x00,
					// Speed + Heading
			(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00,
					// Alt + acc
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			// SN + LT
			(byte)0x00, (byte)0x42, (byte)0x33, (byte)0x00,
			// SOPV
				// GN_ADDR
			(byte)0x00, (byte)0x42, (byte)0xDE, (byte)0xAD,
			(byte)0xBE, (byte)0xEF, (byte)0x00, (byte)0x02, 
				// TST
			(byte)0x54, (byte)0x84, (byte)0x75, (byte)0x10,
				// Lat
			(byte)0x42, (byte)0x54, (byte)0x00, (byte)0x00,
				// Long
			(byte)0x07, (byte)0x54, (byte)0x00, (byte)0x00,
				// Speed + Heading
			(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00,
				// Alt + acc
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			// Target Gn_Address
			(byte)0x00, (byte)0x42, (byte)0xDE, (byte)0xAD, 
			(byte)0xBA, (byte)0xBE, (byte)0xBE, (byte)0xEF 
	};
} // End of class GnSourceLayer
