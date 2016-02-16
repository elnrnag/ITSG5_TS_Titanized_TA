package org.etsi.its.tool.elvior;

import org.etsi.ttcn.tci.Type;

public class LibItsCALMllsap_asn1 extends LibIts_asn1 {
    
    /**
     * Constructor
     */
    public LibItsCALMllsap_asn1() {
        _logger.entering("LibItsCALMllsap_asn1", "LibItsCALMllsap_asn1");
        _encodingName = "CALMllsap";
    }
    
    protected Type getTypeForName(final String type) { 
        _logger.entering("LibItsCALMllsap_asn1", "getTypeForName", type);
        
        String type_ = type;
        if (type.endsWith("mnRequestRequest")) {
            type_ = String.format("%s.%s", _encodingName, "MN_Request_request");
        } else if (type.endsWith("msgIn_in")) {
            type_ = String.format("%s.%s", _encodingName, "INsapPrimitivesDown");
        }
        
        Type asnOriginalType = super.getTypeForName(type_);
        
        _logger.exiting("LibItsCALMllsap_asn1", "getTypeForName", asnOriginalType.getName());
        return asnOriginalType;
    } // End of method getTypeForName
    
} // End of class LibItsCALMllsap_asn1 
