/**
 *  GN6 port implementation
 * 
 *  @author     ETSI / STF424
 *  @version    $URL: svn+ssh://vcs.etsi.org/TTCN3/TOOL/ITS_TestPlatform/trunk/javasrc/adapter/org/etsi/its/adapter/ports/CamPort.java $
 *              $Id: CamPort.java 203 2011-05-03 09:17:50Z berge $
 *
 */
package org.etsi.its.adapter.ports;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.etsi.adapter.TERFactory;
import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.layers.Layer;
import org.etsi.ttcn.tci.CharstringValue;

/**
 *  CAM port implementation
 */
public class Gn6Port extends ProtocolPort implements Runnable {

    private static final byte CMD_REGISTER = 0x01;
    private static final byte CMD_UNREGISTER = 0x02;
    private static final byte CMD_PACKET = 0x04;
    
    private static final byte RPL_PACKET = 0x04;
    
    /**
     * Constructor
     * @param   portName        Name of the port
     * @param   componentName   Name of the component owning this port instance
     * @param   lowerStackDesc  Description of the port's lower stack in the form "Layer/Layer/Layer/..."
     * @param  linkLayerAddress    Link-layer address to be used by this port as source address (null if not applicable)
     */
	public Gn6Port(String portName, String componentName, String lowerStackDesc, String linkLayerAddress) {
		super(portName, componentName, lowerStackDesc, linkLayerAddress);
		
		try {
            remoteAdapterSocket = new DatagramSocket();
            remoteAdapterAddress = InetAddress.getByName(((CharstringValue)TERFactory.getInstance().getTaParameter("Gn6RemoteAdapterIp")).getString());
            remoteAdapterPort = Integer.decode(((CharstringValue)TERFactory.getInstance().getTaParameter("Gn6RemoteAdapterPort")).getString());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }         
        running = true;
        
		// Start reception Thread
		receptionThread = new Thread(this);
        receptionThread.start();
	}

	/* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.IPort#send(byte[])
     */
    @Override
    public boolean send(byte[] message) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        
        int ipv6pos = ByteHelper.byteArrayToInt(new byte[] {message[0]}) + 12 + 1;
        byte [] parameters = ByteHelper.extract(message, 0, ipv6pos);
        byte [] ipv6 = ByteHelper.extract(message, ipv6pos, message.length - ipv6pos);
        
        byte [] sent = ByteHelper.concat(new byte[] {CMD_PACKET}, parameters, ByteHelper.intToByteArray(ipv6.length, 2), ipv6);        
        DatagramPacket packet = new DatagramPacket(sent, sent.length, remoteAdapterAddress, remoteAdapterPort);
        try {
            remoteAdapterSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return send(sent, params);
    }
    	
	/**
     * Thread function for sending periodic beacons
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        byte[] buf = new byte[4096];
        byte[] data;
        
        // Send REGISTER to remote adapter
        DatagramPacket command = new DatagramPacket(new byte[] {CMD_REGISTER}, 1, remoteAdapterAddress, remoteAdapterPort);
        DatagramPacket response = new DatagramPacket(buf, buf.length);
        try {
            remoteAdapterSocket.send(command);
            remoteAdapterSocket.receive(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Process messages from remote adapter      
        try {
            remoteAdapterSocket.setSoTimeout(1000);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while(running) {
                    
            Map<String, Object> lowerInfo = new HashMap<String, Object>();
            try {                
                // receive packet
                remoteAdapterSocket.receive(response);                
            } catch (SocketTimeoutException e) {
                // ignore
                continue;
            } catch (IOException e) {
                running = false;
            } 
            
            data = ByteHelper.extract(response.getData(), response.getOffset(), response.getLength());
            if(data[0] == RPL_PACKET) {
                ByteHelper.dump("GN6 receive", data);
                lowerInfo.put(Layer.RECEPTION_TIMESTAMP, System.currentTimeMillis());
                receive(ByteHelper.extract(data, 1, data.length - 1), lowerInfo);
            }
        } 
        
        // Send UNREGISTER to remote adapter
        command = new DatagramPacket(new byte[] {CMD_UNREGISTER}, 1, remoteAdapterAddress, remoteAdapterPort);
        response = new DatagramPacket(buf, buf.length);
        try {
            remoteAdapterSocket.send(command);
            remoteAdapterSocket.receive(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }       
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.ProtocolPort#dispose()
     */
    @Override
    public void dispose() {
        if(running && receptionThread != null) {
            running = false;
            try {
                receptionThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.dispose();
    }    
 
    /**
     * Indicates whether the port is still active. Setting this field to false will cause
     * the reception thread to stop its execution.
     */
    private volatile boolean running;
    
	/**
     * Reception thread instance.
     */
    private Thread receptionThread = null;
 
    private volatile DatagramSocket remoteAdapterSocket;
    private InetAddress remoteAdapterAddress;
    private int remoteAdapterPort;
}
