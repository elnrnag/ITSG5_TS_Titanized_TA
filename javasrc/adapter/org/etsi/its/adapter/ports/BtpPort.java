/**
 *  BTP port implementation
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/BtpPort.java $
 *              $Id: BtpPort.java 2230 2015-06-03 09:11:02Z mullers $
 *
 */
package org.etsi.its.adapter.ports;

import java.util.HashMap;
import java.util.Map;

import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.layers.BtpLayer;
import org.etsi.its.adapter.layers.GnLayer;

/**
 *  BTP port implementation
 */  
public class BtpPort extends ProtocolPort {

    /**
     * Constructor
     * @param   portName        Name of the port
     * @param   componentName   Name of the component owning this port instance
     * @param   lowerStackDesc  Description of the port's lower stack in the form "Layer/Layer/Layer/..."
     * @param  linkLayerAddress    Link-layer address to be used by this port as source address (null if not applicable)
     */
    public BtpPort(String portName, String componentName, String lowerStackDesc, String linkLayerAddress) {
        super(portName, componentName, lowerStackDesc, linkLayerAddress);
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.ProtocolPort#receive(byte[])
     */
    @Override
    public void receive(byte[] message, Map<String, Object> lowerInfo) {
        
        // Encode with GN next header info - BTP A or BTP B
        byte[] msgInd = ByteHelper.concat(
                new byte[] { (byte) lowerInfo.get(GnLayer.GN_NEXTHEADER) },
                message
            );
        super.receive(msgInd, lowerInfo);
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.IPort#send(byte[])
     */
    @Override
    public boolean send(byte[] message) {
    
        HashMap<String, Object> params = new HashMap<String, Object>();
        switch (message[0]) {
            case 1:
                params.put(BtpLayer.BTP_TYPE, BtpLayer.TYPE_A);
                params.put(GnLayer.GN_NEXTHEADER, "BTP-A");
                break;
            case 2:
                params.put(BtpLayer.BTP_TYPE, BtpLayer.TYPE_B);
                params.put(GnLayer.GN_NEXTHEADER, "BTP-B");
                break;
            default:
                //otherwise GN next header is ANY
                break;
        }
        params.put(BtpLayer.BTP_DSTPORT, 2001);
        params.put(BtpLayer.BTP_SRCPORT, 500);
        params.put(GnLayer.GN_TYPE, GnLayer.HT_TSB);
        params.put(GnLayer.GN_SUBTYPE, GnLayer.HST_SINGLEHOP);
        //params.put(GnLayer.GN_SUBTYPE, GnLayer.HST_MULTIHOP);
        
        byte[] msg2sent = new byte[message.length-1];
        //cut the next header indication byte
        System.arraycopy(message, 1, msg2sent, 0, msg2sent.length);
        
        return send(msg2sent, params);
    }
}
