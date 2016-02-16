package org.etsi.its.adapter.ports;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.etsi.adapter.TERFactory;
import org.etsi.common.ByteHelper;
import org.etsi.ttcn.tci.CharstringValue;

/** This class implements behaviour for Configuration Tester port. This port is used to access to the IUT management port 
 * The Configuration tester entity in the SUT enables triggering Protocol functionalities by simulating primitives from 
 * from SAPs
 * It is required to trigger the Protocol layer in the SUT to send Protocol specific messages, which are 
 * resulting from management layer primitives
 */
public class ConfigTesterPort extends AdapterPort implements IPort, IObservable {
    
    private static final String SETTINGS_PATTERN = "(\\S+)\\:(\\d+)";
    
    /**
     * Constructor
     * @param   portName            Name of the port
     * @param   componentName       Name of the component owning this port instance
     * @param   localPortNumber     Local port number for the UDP listener
     * @param   remotePortNumber    UDP port listener of remote UT application
     */
    public ConfigTesterPort(final String portName, final String componentName) {
        super(portName, componentName);
    
        // UDP connection parameters
        String settings = ((CharstringValue)TERFactory.getInstance().getTaParameter("ConfigTesterSettings")).getString();
        Matcher matcher = settingsPattern.matcher(settings);
        if (matcher.find()) {
        try {
                cfPeerAddress = InetAddress.getByName(matcher.group(1));
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
    }
            cfPeerPort = Integer.parseInt(matcher.group(2));
        } else {
    
    }
        running = true;

        // UDP socket for communication with UT
        try {
            cfSocket = new DatagramSocket();
            cfThread = new UdpThread(cfSocket);
            cfThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean send(final byte[] message) {
        
        DatagramPacket packet = new DatagramPacket(message, message.length, cfPeerAddress, cfPeerPort);
        try {
            cfSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
            }
        return true;
    } 
    
    @Override
    public void dispose() {
        if(running) { 
            running = false;
            if(cfThread != null) {
                try {
                    cfSocket.close();
                    cfThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
    }
        }
    }
    
    private DatagramSocket cfSocket;
    private Thread cfThread;
    private InetAddress cfPeerAddress = null;
    private int cfPeerPort = 0;
    private Pattern settingsPattern = Pattern.compile(SETTINGS_PATTERN);
        
    /**
     * Indicates whether the port is still active. Setting this field to false will cause
     * the UDP communication with Config Tester to be stopped
     */
    private volatile boolean running;
    
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
                
                    // receive packet
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    taSocket.receive(packet);
                
            setChanged(); 
                    notifyObservers(new PortEvent(ByteHelper.extract(packet.getData(), packet.getOffset(), packet.getLength()), getPortName(), getComponentName()));
            } catch (IOException e) {
                    running = false;
            }
            }
            taSocket.close();
        }
    }
    
} // End of class ConfigTesterPort 
