package org.etsi.its.adapter.ports;

import java.util.HashMap;
import java.util.Map;

import org.etsi.adapter.TERFactory;
import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.layers.EthernetLayer;
import org.etsi.its.adapter.layers.IEthernetSpecific;
import org.etsi.ttcn.tci.CharstringValue;

public class FsapPort extends ProtocolPort implements Runnable, IEthernetSpecific {

    private volatile boolean _running;
    
    /**
     * Service Provider thread instance.
     */
    private Thread _beaconThread;

	private byte[] _sam;

    /**
     * Constructor
     * @param   portName        Name of the port
     * @param   componentName   Name of the component owning this port instance
     * @param   lowerStackDesc  Description of the port's lower stack in the form "Layer/Layer/Layer/..."
     * @param   linkLayerAddress    Link-layer address to be used by this port as source address (null if not applicable)
     */
    public FsapPort(String portName, String componentName, String lowerStackDesc, String linkLayerAddress) {
        super(portName, componentName, lowerStackDesc, linkLayerAddress);
        management.registerFsapPort(this);
        _running = false;
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.IEthernetSpecific#getEthernetType()
     */
    @Override
    public short getEthernetType() {
        
        // Retrieve EthernetType value
        Integer iutEthernetTypeValue = Integer.decode(((CharstringValue)TERFactory.getInstance().getTaParameter("IutEthernetTypeValue")).getString());
        return iutEthernetTypeValue.shortValue();
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.ProtocolPort#receive(byte[])
     */
    @Override
    public void receive(byte[] message, Map<String, Object> lowerInfo) {
        // Encode with FsapInd indication header
        // Extract LINK_LAYER_DESTINATION
        byte[] fsapInd = ByteHelper.concat(message, (byte[])lowerInfo.get(EthernetLayer.LINK_LAYER_DESTINATION));
            super.receive(fsapInd, lowerInfo);
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.IPort#send(byte[])
     */
    @Override
    public boolean send(byte[] message) {
        ByteHelper.dump("FsapPort.send", message);
        HashMap<String, Object> params = new HashMap<String, Object>();
        return send(message, params);
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.ProtocolPort#dispose()
     */
    @Override
    public void dispose() {
        if(_running && _beaconThread != null) {
            _running = false;
            try {
                _beaconThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _sam = null;
        super.dispose();
    }
    
    public boolean startSamTransmission(final byte[] sam) {
        ByteHelper.dump("FsapPort.startSamTransmission", sam);
        
        _sam = sam.clone();//ByteHelper.extract(sam, 2, sam.length - 2);
        if(_beaconThread == null) { 
            _beaconThread = new Thread(this);
            _beaconThread.start();
        }
        
        return true;
    }
    
    public boolean stopSamTransmission() {
        _running = false;
        try {
            _beaconThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        _sam = null;
        
        return true;
    }

    @Override
    public void run() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(EthernetLayer.LINK_LAYER_DESTINATION, management.getLinkLayerAddress());
        _running = true;
        while (_running) { 
        	send(_sam, params);
            try {
                Thread.sleep(3000); // 3 second 
            } catch (InterruptedException e) {
                
            }
        } // End of 'while' statement
        
        
    }
    
} // End of FntpPort
