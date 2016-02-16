/**
 *  Test and debug module. 
 *  SHALL NOT BE INCLUDED IN RELEASE
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/Main.java $
 *              $Id: Main.java 1423 2014-05-22 13:59:50Z filatov $
 *
 */

package org.etsi.its.adapter;

import org.etsi.its.adapter.ports.ProtocolPort;
import org.etsi.its.adapter.ports.ProtocolPortFactory;

/**
 *  Test and debug module. 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/**
	 * Creates CAM port using stack LoopBack/Debug and sends a message
	 */
    @SuppressWarnings("unused")
    private static void camTest() {
		
		ProtocolPort port = ProtocolPortFactory.getInstance().createPort("camPort", "toto", "GN/Debug", "ACACACACACAC");
		port.send("CAM MESSAGE !".getBytes());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		port.dispose();
	}
    
    /**
     * Creates BTP port using stack GN/LoopBack/Debug and sends a message
     */
    @SuppressWarnings("unused")
    private static void btpTest() {
        
        ProtocolPort port = ProtocolPortFactory.getInstance().createPort("btpPort", "toto", "GN/Loopback/Debug", "ACACACACACAC");
        port.send("BTP MESSAGE !".getBytes());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        port.dispose();
    }
    	
    /**
     * Creates GN port using stack GnSource and sends a message
     */
    @SuppressWarnings("unused")
    private static void gnTest() {
		byte[] gnMsg = {(byte)0x00, (byte)0x50, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x1f, (byte)0x00, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xff,
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xff,
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xff, (byte)0x54, (byte)0x45, (byte)0x53, (byte)0x54, (byte)0x20, (byte)0x31, (byte)0x20, (byte)0x54, (byte)0x45, (byte)0x53, (byte)0x54, (byte)0x20,
				(byte)0x54, (byte)0x45, (byte)0x53, (byte)0x54, (byte)0x20, (byte)0x54, (byte)0x45, (byte)0x53, (byte)0x54, (byte)0x20, (byte)0x54, (byte)0x45, (byte)0x53, (byte)0x54, (byte)0x20, (byte)0x54,
				(byte)0x45, (byte)0x53, (byte)0x54};
		
		ProtocolPort port = ProtocolPortFactory.getInstance().createPort("geoNetworkingPort", "toto", "Loopback/Debug", "ACACACACACAC");
		port.send(gnMsg);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		port.dispose();
			
	}

}
