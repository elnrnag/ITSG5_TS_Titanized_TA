/**
 *  V2G port implementation (background thread)
 *  
 *  @author     ETSI / STFS46
 *  @version    $URL: $
 *              $Id: $
 *
 */
package org.etsi.its.adapter.ports;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.etsi.adapter.TERFactory;
import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.layers.Layer;
import org.etsi.ttcn.codec.its.v2g.ExiHelper;
import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.tci.IntegerValue;

/**
 *  V2G port implementation (background threads)
 */
public class V2GPort extends ProtocolPort {

	private static final byte V2G_IND = 0x01;
	private static final byte APP_PROTO_IND = 0x02;
	private static final byte SDP_IND = 0x03;
	
    private String role;
    private DatagramSocket sdpSocket;
    private Socket v2gSocket = null;
    private Thread sdpThread, v2gThread, v2gServerThread;
    private InetAddress sdpPeerAddress = null;
    private int sdpPeerPort = 15118;
    private int v2gPort;
    private String v2gHost;
	private int v2gtpSentCount = 0;
	private int v2gtpReceivedCount = 0;
	private byte v2gtpInd = APP_PROTO_IND;
	
	private ServerSocket v2gServerSocket = null;
	
	/**
	 * Indicates whether the port is still active. Setting this field to false will cause
	 * the beaconing thread to stop its execution.
	 */
	private volatile boolean running;
	
	/**
     * Constructor
     * @param   portName            Name of the port
     * @param   componentName       Name of the component owning this port instance
     * @param   lowerStackDesc      Description of the port's lower stack in the form "Layer/Layer/Layer/..."
     * @param   linkLayerAddress    Link-layer address to be used by this port as source address (null if not applicable)
     */
	public V2GPort(String portName, String componentName, String lowerStackDesc, String linkLayerAddress) {
		super(portName, componentName, lowerStackDesc, linkLayerAddress);

		// Initialize state variables
		v2gtpSentCount = 0;
		v2gtpReceivedCount = 0;
		v2gtpInd = APP_PROTO_IND;
		ExiHelper.setSchemaId("handshake");
		
		role = ((CharstringValue)TERFactory.getInstance().getTaParameter("V2gTsRole")).getString();
		v2gHost = ((CharstringValue)TERFactory.getInstance().getTaParameter("V2gSeccAddress")).getString();
		//v2gPort = Integer.decode(((CharstringValue)TERFactory.getInstance().getTaParameter("V2gSeccPort")).getString());
		v2gPort = ((IntegerValue)TERFactory.getInstance().getTaParameter("V2gSeccPort")).getInt();
		try {
			sdpPeerAddress = InetAddress.getByName("ff02::1");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		running = true;		
		
		if(role.equals("SECC")) {
			
			// UDP socket for SDP
			try {
				MulticastSocket mcSocket = new MulticastSocket(15118);
				mcSocket.joinGroup(InetAddress.getByName("FF02::1"));
				sdpSocket = mcSocket;
		        sdpThread = new UdpThread(sdpSocket);
		        sdpThread.start();
			} catch (Exception e) {
		        e.printStackTrace();
		    }
			
			// TCP socket for V2G
			try {
				v2gServerSocket = new ServerSocket(v2gPort);
				v2gServerThread = new TcpServerThread(v2gServerSocket);
				v2gServerThread.start();
			} catch (Exception e) {
		        e.printStackTrace();
		    }	
		}
		else if(role.equals("EVCC")) {
			
			// UDP socket for SDP
			try {
				sdpSocket = new DatagramSocket();				
		        sdpThread = new UdpThread(sdpSocket);
		        sdpThread.start();
			} catch (Exception e) {
		        e.printStackTrace();
		    }			
		}
		
		System.out.println("V2Gport initialized");
	}

	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.ports.ProtocolPort#dispose()
	 */
	@Override
	public void dispose() {
		if(running) {			
			running = false;
			if(v2gServerThread != null) {
				try {					
					v2gServerSocket.close();
					v2gServerThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(sdpThread != null) {
				try {
					sdpSocket.close();
					sdpThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(v2gThread != null) {
				try {
					v2gSocket.close();
					v2gThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		super.dispose();
	}
	
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.layers.Layer#send(byte[], java.util.Map)
     */
    @Override
    public boolean send(byte[] message, Map<String, Object> params) {
    	
    	byte[] type = ByteHelper.extract(message, 0, 1);
    	byte[] v2gtpHeader =  {(byte)0x01, (byte) 0xFE};
    	message = ByteHelper.extract(message, 1, message.length - 1);
    	if(type[0] == SDP_IND) {
    		// SDP    		
    		byte[] sdpMessageType = null;
    		if(role.equals("EVCC")) {
    			byte[] sdpReqMessageType = {(byte)0x90, (byte)0x00};
    			sdpMessageType = sdpReqMessageType;
    		}
    		else {
    			byte[] sdpRspMessageType = {(byte)0x90, (byte)0x01};
    			sdpMessageType = sdpRspMessageType;
    		}
    		message = ByteHelper.concat(v2gtpHeader, sdpMessageType, ByteHelper.intToByteArray(message.length, 4), message);
    		DatagramPacket packet = new DatagramPacket(message, message.length, sdpPeerAddress, sdpPeerPort);
            try {
                sdpSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }    		
    	}
    	else {
    		// V2G or supportedAppprotocol    		
    		if(role.equals("EVCC") && v2gSocket == null) {
    			try {
    				v2gSocket = new Socket(v2gHost, v2gPort);
    				v2gThread = new TcpThread(v2gSocket);
    				v2gThread.start();
    			} catch (Exception e) {
    		        e.printStackTrace();
    		    }	
    		}    		
    		try {
    			byte[] exiV2gMessageType = {(byte)0x80, (byte)0x01};
    			message = ByteHelper.concat(v2gtpHeader, exiV2gMessageType, ByteHelper.intToByteArray(message.length, 4), message);
				v2gSocket.getOutputStream().write(message);
			} catch (IOException e) {				
				e.printStackTrace();
			}
    		
    		v2gtpSentCount++;
    		if(role.equals("SECC")){
    		    if(v2gtpInd == APP_PROTO_IND && v2gtpReceivedCount > 0) {
    		        v2gtpInd = V2G_IND;
    		        ExiHelper.setSchemaId("v2g");
    		    }
    		}
    	}    	
    	return true;
    }
    
    private class UdpThread extends Thread {

        private DatagramSocket taSocket;
        
        public UdpThread(DatagramSocket taSocket) throws IOException {
            this.taSocket = taSocket;
        }

        @Override
        public void run() {
            
            while(running) {
                try {
                    byte[] buf = new byte[4096];
                    Map<String, Object> lowerInfo = new HashMap<String, Object>();
                    
                    // receive packet
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    taSocket.receive(packet);
                   	
                    if(role.equals("SECC")) {
                    	sdpPeerAddress = packet.getAddress();
                    	sdpPeerPort = packet.getPort();
                    }
                    lowerInfo.put(Layer.RECEPTION_TIMESTAMP, System.currentTimeMillis());
                    receive(ByteHelper.concat(new byte[]{(SDP_IND)}, ByteHelper.extract(packet.getData(), packet.getOffset() + 8, packet.getLength() - 8)), lowerInfo);
                } catch (IOException e) {
                    running = false;
                }
            }
            taSocket.close();
        }        
    }
    
    private class TcpServerThread extends Thread {
    
    	private ServerSocket taSocket;
        
        public TcpServerThread(ServerSocket taSocket) throws IOException {
            this.taSocket = taSocket;
        }

        @Override
        public void run() { 
        	try {
				v2gSocket = taSocket.accept();
				v2gThread = new TcpThread(v2gSocket);
				v2gThread.start();
				taSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		            	
        }    	
    }
    
    private class TcpThread extends Thread {

        private Socket taSocket;
        
        public TcpThread(Socket taSocket) throws IOException {
            this.taSocket = taSocket;
        }

        @Override
        public void run() {
            
        	InputStream input = null;
			try {
				input = taSocket.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
    	    while(running) {
                try {
                    byte[] buf = new byte[4096];
                    int nbRead;
                    Map<String, Object> lowerInfo = new HashMap<String, Object>();
                    
                    // receive packet
                    nbRead = input.read(buf);
                    if(nbRead > 8) {
                        ExiHelper.lockSchemaId();
                        lowerInfo.put(Layer.RECEPTION_TIMESTAMP, System.currentTimeMillis());
                        receive(ByteHelper.concat(new byte[]{(v2gtpInd)}, ByteHelper.extract(buf, 8, nbRead - 8)), lowerInfo);
                        v2gtpReceivedCount++;
                        if(role.equals("EVCC")){ 
                            if(v2gtpInd == APP_PROTO_IND && v2gtpSentCount > 0) {
                                v2gtpInd = V2G_IND;
                                ExiHelper.setSchemaId("v2g");
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    running = false;
                }
            }
    	    try {
				taSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}