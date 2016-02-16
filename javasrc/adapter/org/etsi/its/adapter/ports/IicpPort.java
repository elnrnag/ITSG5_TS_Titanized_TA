package org.etsi.its.adapter.ports;

import java.util.HashMap;
import java.util.Map;

import org.etsi.adapter.TERFactory;
import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.layers.EthernetLayer;
import org.etsi.its.adapter.layers.IEthernetSpecific;
import org.etsi.ttcn.tci.CharstringValue;

public class IicpPort extends ProtocolPort implements IEthernetSpecific {
    
    /**
     * Constructor
     * @param   portName        Name of the port
     * @param   componentName   Name of the component owning this port instance
     * @param   lowerStackDesc  Description of the port's lower stack in the form "Layer/Layer/Layer/..."
     * @param   linkLayerAddress    Link-layer address to be used by this port as source address (null if not applicable)
     */
    public IicpPort(String portName, String componentName, String lowerStackDesc, String linkLayerAddress) {
        super(portName, componentName, lowerStackDesc, linkLayerAddress);
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
        // Encode with IicpInd indication header
        // Extract LINK_LAYER_DESTINATION
        byte[] iicpInd = ByteHelper.concat(message, (byte[])lowerInfo.get(EthernetLayer.LINK_LAYER_DESTINATION));
            super.receive(iicpInd, lowerInfo);
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.IPort#send(byte[])
     */
    @Override
    public boolean send(byte[] message) {
        ByteHelper.dump("IicpPort.send", message);
        HashMap<String, Object> params = new HashMap<String, Object>();
        return send(message, params);
    }

    public boolean triggerInSapPrimitiveUp(byte[] inSapPrimitiveUp) {
        ByteHelper.dump("IicpPort.triggerInSapPrimitiveUp", inSapPrimitiveUp);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(EthernetLayer.LINK_LAYER_DESTINATION, management.getLinkLayerAddress());
        return send(inSapPrimitiveUp, params);
    }
    
} // End of class FntpPort
