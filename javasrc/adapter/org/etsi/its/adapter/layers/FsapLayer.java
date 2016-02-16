package org.etsi.its.adapter.layers;

import java.util.Map;
import java.util.Stack;

import org.etsi.adapter.TERFactory;
import org.etsi.its.adapter.IManagementLayers;
import org.etsi.ttcn.tci.CharstringValue;

/**
 *  Implementation of ITS FSAP layer
 */
public class FsapLayer extends Layer implements IEthernetSpecific {
    
    /**
     * Constructor
     * @param  management   Layer management instance
     * @param  lowerStack   Lower protocol stack   
     */
    public FsapLayer(IManagementLayers management, Stack<String> lowerStack) {
        super(management, lowerStack);
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
     * @see org.etsi.its.adapter.layers.Layer#send(byte[], java.util.Map)
     */
    @Override
    public boolean send(byte[] message, Map<String, Object> params) { 
        return super.send(message, params);
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.layers.Layer#receive(byte[])
     */
    @Override
    public void receive(byte[] message, Map<String, Object> lowerInfo) {
        super.receive(message, lowerInfo);
    }
    
} // End of class FntpLayer
