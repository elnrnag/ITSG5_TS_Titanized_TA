package org.etsi.its.tool.elvior;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriMessage;


/**
 * 
 * Note that "Enable Internal Codec" field shall be set to true
 *
 */
public class LibItsCam_asn1 extends LibIts_asn1 {
    
    /**
     * Constructor
     */
    public LibItsCam_asn1() {
        _logger.entering("LibItsCam_asn1", "LibItsCam_asn1");
        _encodingName = "CAM_PDU_Descriptions";
    }
    
    @Override
    public Value decode(final TriMessage message, final Type decodingHypothesis) {
        _logger.entering("LibItsCam_asn1", "decode", decodingHypothesis.getName());
        
        if (decodingHypothesis.getName().endsWith("camPacket")) {
            Type type = getTypeForName("CAM_PDU_Descriptions.CAM");
            return super.decode(message, type);
        }
        
        return super.decode(message, decodingHypothesis);
    }
    
} // End of class LibItsCam_asn1 
