/**
 *  CAM port implementation
 * 
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/CamPort.java $
 *              $Id: CamPort.java 2230 2015-06-03 09:11:02Z mullers $
 *
 */
package org.etsi.its.adapter.ports;

import java.util.HashMap;
import java.util.Map;

import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.layers.BtpLayer;
import org.etsi.its.adapter.layers.GnLayer;

/**
 *  CAM port implementation
 */
public class CamPort extends ProtocolPort {

    /**
     * Constructor
     * @param   portName        Name of the port
     * @param   componentName   Name of the component owning this port instance
     * @param   lowerStackDesc  Description of the port's lower stack in the form "Layer/Layer/Layer/..."
     * @param  linkLayerAddress    Link-layer address to be used by this port as source address (null if not applicable)
     */
    public CamPort(String portName, String componentName, String lowerStackDesc, String linkLayerAddress) {
        super(portName, componentName, lowerStackDesc, linkLayerAddress);
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.ProtocolPort#receive(byte[], java.util.Map)
     */
    @Override
    public void receive(byte[] message, Map<String, Object> lowerInfo) {
//        System.out.println(">>> denmPort.receive: " + ByteHelper.byteArrayToString(message));
        
        if (message[1] != 0x02) { // Check that received packet has CAM message id
            System.err.println("camPort.receive: drop packet " + ByteHelper.byteArrayToString(message));
            return; // Drop it
        }
        
        // Encode with CAM indication header
        byte[] msgInd = ByteHelper.concat(
            message, 
            new byte[] { (byte) lowerInfo.get(GnLayer.GN_NEXTHEADER) },
            ByteHelper.intToByteArray((int) lowerInfo.get(GnLayer.GN_TYPE), 1),
            ByteHelper.intToByteArray((int) lowerInfo.get(GnLayer.GN_SUBTYPE), 1),
            ByteHelper.intToByteArray((int) lowerInfo.get(GnLayer.GN_LIFETIME), Integer.SIZE / Byte.SIZE),
            ByteHelper.intToByteArray((int) lowerInfo.get(GnLayer.GN_TRAFFICCLASS), 1),
            (byte[]) lowerInfo.get(BtpLayer.BTP_DSTPORT),
            (byte[]) lowerInfo.get(BtpLayer.BTP_DSTPORTINFO)
        );
        super.receive(msgInd, lowerInfo);
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.IPort#send(byte[])
     */
    @Override
    public boolean send(byte[] message) {
    
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(BtpLayer.BTP_TYPE, BtpLayer.TYPE_B);
        params.put(BtpLayer.BTP_DSTPORT, 2001);
        params.put(BtpLayer.BTP_SRCPORT, 500);
        params.put(GnLayer.GN_TYPE, GnLayer.HT_TSB);
        params.put(GnLayer.GN_SUBTYPE, GnLayer.HST_SINGLEHOP);
        params.put(GnLayer.GN_NEXTHEADER, "BTP-B");
        return send(message, params);
    }
}
