package org.etsi.its.tool.elvior;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriMessage;

/**
 * 
 * Note that "Enable Internal Codec" field shall be set to true
 *
 */
public class LibItsDenm_asn1 extends LibIts_asn1 {
    
    /**
     * Constructor
     */
    public LibItsDenm_asn1() {
        _logger.entering("LibItsDenm_asn1", "LibItsDenm_asn1");
        _encodingName = "DENM_PDU_Descriptions";
    }
    
    @Override
    public Value decode(final TriMessage message, final Type decodingHypothesis) {
        _logger.entering("LibItsDenm_asn1", "decode", decodingHypothesis.getName());
        
        if (decodingHypothesis.getName().endsWith("denmPacket")) {
            Type type = getTypeForName("DENM_PDU_Descriptions.DENM");
            return super.decode(message, type);
        }
        
        return super.decode(message, decodingHypothesis);
    }
    
} // End of class BuiltInCodec 
