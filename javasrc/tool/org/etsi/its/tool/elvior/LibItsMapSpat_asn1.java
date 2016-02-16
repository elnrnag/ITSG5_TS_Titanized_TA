package org.etsi.its.tool.elvior;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriMessage;


/**
 * 
 * Note that "Enable Internal Codec" field shall be set to true
 *
 */
public class LibItsMapSpat_asn1 extends LibIts_asn1 {
    
    /**
     * Constructor
     */
    public LibItsMapSpat_asn1() {
        _logger.entering("LibItsMapSpat_asn1", "LibItsMapSpat_asn1");
        _encodingName = "MAP_SPAT_ETSI";
    }
    
    @Override
    public Value decode(final TriMessage message, final Type decodingHypothesis) {
        _logger.entering("LibItsMapSpat_asn1", "decode", decodingHypothesis.getName());
        
        if (decodingHypothesis.getName().endsWith("msgMap")) {
            Type type = getTypeForName("MAP_SPAT_ETSI.MAP_PDU");
            return super.decode(message, type);
        } else if (decodingHypothesis.getName().endsWith("msgSpat")) {
            Type type = getTypeForName("MAP_SPAT_ETSI.SPAT_PDU");
            return super.decode(message, type);
        }
        
        return super.decode(message, decodingHypothesis);
    }
    
} // End of class LibItsMapSpat_asn1 
