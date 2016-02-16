/**
 *  Pcap capture multiplexor
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: $
 *              $Id: $
 *  Note Copy jnetpcap.jar in C:\WINDOWS\Sun\Java\lib\ext, location of jpcap library
 */
package org.etsi.its.adapter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.etsi.adapter.TERFactory;
import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.layers.EthernetLayer;
import org.etsi.its.adapter.layers.Layer;
import org.etsi.ttcn.tci.CharstringValue; /* FIXME: import tci */
import org.jnetpcap.ByteBufferHandler;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;

public class PcapMultiplexer implements Runnable {

    /**
     * Unique instance of the factory
     */
    private static final PcapMultiplexer instance = new PcapMultiplexer();
    
    private static byte[] MAC_BROADCAST = new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
    
    private StringBuilder errbuf = new StringBuilder();     // For any error msgs  
    
    private PcapMultiplexer() {
        
        filter = "";
        
        // Obtain the list of network interfaces
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
        
                          
        int r = Pcap.findAllDevs(alldevs, errbuf);  
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
          System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
          return;  
        }  

        // Find the right interface        
        int ifaceIndex = 0;
        String expectedIface = ((CharstringValue)TERFactory.getInstance().getTaParameter("LocalEthernetMAC")).getString().toLowerCase(); 
        for( ; ifaceIndex < alldevs.size(); ifaceIndex++) {
            try {
                if (expectedIface.equalsIgnoreCase(ByteHelper.byteArrayToString(alldevs.get(ifaceIndex).getHardwareAddress()))) {
                    // Interface found
                    break;
                }
            } catch (IOException e) {
                // ignore
            }
        }
        // Check result
        if (ifaceIndex == alldevs.size()) {
            throw new RuntimeException(String.format("EthernetLayer.register: Network interface %s not found", expectedIface));
        }
       
        device = alldevs.get(ifaceIndex);
//        System.out.println("Listening: " + device.getName());
    }
    
    /**
     * Gets the unique factory instance
     * @return PcapMultiplexer instance
     */
    public static PcapMultiplexer getInstance(){
        return instance;
    }
    
    public synchronized void register(Layer client, byte[] macAddress, short frameType) {
//        System.out.println(">>>PcapMultiplexer.registering: " + frameType);
        
        if(clientsToMacs.isEmpty()) {
            // Open interface 
            int snaplen = 64 * 1024;            // Capture all packets, no truncation  
            int flags = Pcap.MODE_PROMISCUOUS;  // capture all packets  
            int timeout = 10;                   // 10 millis  
            pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);  
              
            if (pcap == null) {  
              System.err.printf("Error while opening device for capture: "  
                    + errbuf.toString());  
              return;  
            }  
            captureThread = new Thread(this);
            captureThread.start();   
            filter = "";
        }
        else {
//            System.out.println("Another Client !");
            filter = filter + " and ";
        }

        // Update Filter
        if (macAddress == null || macAddress.length == 0){
        	try {
				macAddress = device.getHardwareAddress();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        DatatypeConverter.printHexBinary(macAddress);
        String strMacAddress = String.format("%02x", macAddress[0]);
        for(int i=1; i < macAddress.length; i++) {
            strMacAddress += String.format(":%02x", macAddress[i]);
        }
        
        filter = filter + "not ether src " + strMacAddress;
//        System.out.println("New filter: " + filter);

        // Apply filter
        PcapBpfProgram bpfFilter = new PcapBpfProgram();
        int optimize = 0; // 1 means true, 0 means false
        int netmask = 0;            
        int r = pcap.compile(bpfFilter, filter, optimize, netmask);
        if (r != Pcap.OK) {
//            System.out.println("Filter error: " + pcap.getErr());
        }
        pcap.setFilter(bpfFilter);

        // Register client
        clientsToMacs.put(client.toString(), macAddress);
        clientsToLayers.put(client.toString(), client);
        clientsToFrameTypes.put(client.toString(), frameType);
    }
    
    public synchronized void unregister(Layer client) {
        if(clientsToMacs.containsKey(client.toString())) {
            clientsToMacs.remove(client.toString());
            clientsToFrameTypes.remove(client.toString());
            clientsToLayers.remove(client.toString());
            
            if(clientsToMacs.isEmpty()) {
                pcap.breakloop();
                try {
                    captureThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pcap.close();
            }
        }
    }
    
    /**
     * Thread function for jpcap capture loop
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        
        ByteBufferHandler<Object> handler = new ByteBufferHandler<Object>() {
            
            @Override
            public void nextPacket(PcapHeader pcapHeader, ByteBuffer byteBuffer, Object user) {
                if(byteBuffer.remaining() < 14) {
                    return;
                }

                Map<String, Object> lowerInfo = new HashMap<String, Object>();

                // Extract Dst info                
                byte[] dst = new byte[6];
                byteBuffer.get(dst, 0, dst.length);
                lowerInfo.put(EthernetLayer.LINK_LAYER_DESTINATION, dst);
                
                // Skip Src
                byteBuffer.position(byteBuffer.position() + 6);

                // Extract FrameType info
                byte[] rawFrameType = new byte[2];
                byteBuffer.get(rawFrameType, 0, rawFrameType.length);
                short frameType = ByteHelper.byteArrayToInt(rawFrameType).shortValue();
                
                // Extract Data
                byte[] data = new byte[byteBuffer.remaining()];
                byteBuffer.get(data, 0, byteBuffer.remaining());
                
                // Dispatch
                for (String mapKey : clientsToMacs.keySet()) {
                    if(frameType == clientsToFrameTypes.get(mapKey)) {
                        if(Arrays.equals(dst, MAC_BROADCAST)
                                || Arrays.equals(dst, clientsToMacs.get(mapKey))) {
                            
                            lowerInfo.put(Layer.RECEPTION_TIMESTAMP, pcapHeader.timestampInMicros());
                            clientsToLayers.get(mapKey).receive(data, lowerInfo);
                        }
                    }
                }
                
            }
        };
        
        pcap.loop(-1, handler, null);
    }
        
    public byte[] sendPacket(Layer client, byte[] dest, byte[] payload) {

        if(clientsToMacs.containsKey(client.toString())) {
            
            byte[] packet = ByteHelper.concat(
                    dest,   
                    clientsToMacs.get(client.toString()), 
                    ByteHelper.intToByteArray(clientsToFrameTypes.get(client.toString()), 2), 
                    payload);
    
            pcap.sendPacket(packet);
            return packet;
        } else {
        	System.out.println("NOT clientsToMacs.containsKey "+client.toString());
        }
        return null;
    }
    
    /**
     * Jpcap capture device
     */
    private Pcap pcap;
    
    /**
     * Jpcap capture thread instance.
     */
    private Thread captureThread;
    
    PcapIf device;
    private String filter;
    private Map<String, byte[]> clientsToMacs = new HashMap<String, byte[]>();
    private Map<String, Short> clientsToFrameTypes = new HashMap<String, Short>();
    private HashMap<String, Layer> clientsToLayers = new HashMap<String, Layer>();
}   