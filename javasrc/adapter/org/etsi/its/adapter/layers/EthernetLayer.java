/**
 *  Implementation of Ethernet layer using jpcap (background thread)
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/EthernetLayer.java $
 *                $Id: EthernetLayer.java 728 2011-11-10 10:44:04Z berge $
 *
 */
package org.etsi.its.adapter.layers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Stack;

import org.etsi.its.adapter.IManagementLayers;
import org.etsi.its.adapter.PcapMultiplexer;

/**
 *  Implementation of Ethernet layer using jpcap (background thread)
 */
public class EthernetLayer extends Layer {

    /**
     * Well-known Ethernet broadcast address
     */
	public static byte[] MAC_BROADCAST = new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
	
    /**
     * Parameter name for Link-Layer destination
     */
    public static final String LINK_LAYER_DESTINATION = "LinkLayerDestination";
	
	/**
     * Constructor
     * @param  management   Layer management instance
     * @param  lowerStack   Lower protocol stack   
     */
	public EthernetLayer(IManagementLayers management, Stack<String> lowerStack) {
		super(management, lowerStack);  
    }

	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#register(org.etsi.its.adapter.layers.Layer)
	 */
	@Override
	public void register(Layer upperLayer) {
		if(registeredUpperLayer == null) {
			super.register(upperLayer);
			
		    try {
		        Method getEthernetType = registeredUpperLayer.getClass().getMethod("getEthernetType", (Class<?>[])null);
		        if (getEthernetType != null) {
                    upperLayerFrameType = (Short) getEthernetType.invoke(registeredUpperLayer, (Object[]) null); 
		        }
		    } catch (SecurityException e) {
	            e.printStackTrace();
	        } catch (NoSuchMethodException e) {
	            e.printStackTrace();
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        } catch (InvocationTargetException e) {
	            e.printStackTrace();
	        }
	        
			localMacAddress = management.getLinkLayerAddress();
			PcapMultiplexer.getInstance().register(this, localMacAddress, upperLayerFrameType);
			
        }
    }
	
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#send(byte[], java.util.Map)
	 */
	@Override
	public boolean send(byte[] message, Map<String, Object> params) {
	    
		byte[] dst = (byte[])params.get(LINK_LAYER_DESTINATION);
		if(dst == null) {
			dst = MAC_BROADCAST;
		}
            
		byte[] sent = PcapMultiplexer.getInstance().sendPacket(this, dst, message); 
		return super.send(sent, params);		
	}
	
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#unregister(org.etsi.its.adapter.layers.Layer)
	 */
	@Override
	public void unregister(Layer upperLayer) {
	    
	    PcapMultiplexer.getInstance().unregister(this);
    }
              
	/**
	 * Local Ethernet address 
	 */
	private byte[] localMacAddress;
	
	/**
	 * Upper layer's frame type
	 */
	private short upperLayerFrameType;

}

