package org.etsi.its.adapter.layers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.IManagementLayers;

public class UdpIpLayer extends Layer {

    
    public UdpIpLayer(IManagementLayers management, Stack<String> lowerStack) {
        super(management, lowerStack);
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.layers.Layer#send(byte[], java.util.Map)
     */
    @Override
    public boolean send(byte[] message, Map<String, Object> params) {
        DatagramPacket packet = new DatagramPacket(message, message.length, iutAddress, iutPort);
        try {
            iutSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.layers.Layer#register(org.etsi.its.adapter.layers.Layer)
     */
    @Override
    public void register(Layer upperLayer) {
    	System.out.println("Registering IP/UDP layer");
        if(registeredUpperLayer == null) {
            super.register(upperLayer);
            try {
                iutAddress = InetAddress.getByName("127.0.0.1");
                //System.out.println("IUT Address: " + iutAddress.getHostAddress());
                iutPort = 3750;
                iutSocket = new DatagramSocket(3751);
                iutThread = new UdpThread(iutSocket);
                iutThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.layers.Layer#unregister(org.etsi.its.adapter.layers.Layer)
     */
    @Override
    public void unregister(Layer upperLayer) {
        iutSocket.close();
        iutThread.interrupt();
        try {
            iutThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.unregister(upperLayer);
    }
    
    private DatagramSocket iutSocket;
    private InetAddress iutAddress;
    private int iutPort;
    private Thread iutThread;
  
    private class UdpThread extends Thread {

        private DatagramSocket taSocket;
        private boolean running = true;
        
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
                    lowerInfo.put(Layer.RECEPTION_TIMESTAMP, System.currentTimeMillis());
                    receive(ByteHelper.extract(packet.getData(), packet.getOffset(), packet.getLength()), lowerInfo);
                } catch (IOException e) {
                    running = false;
                }
            }
        }
    }
}
