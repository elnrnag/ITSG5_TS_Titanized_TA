package org.etsi.its.tool.elvior;

import org.etsi.ttcn.tci.Type;

public class LibItsFntp_asn1 extends LibIts_asn1 {
    
    /**
     * Constructor
     */
    public LibItsFntp_asn1() {
        _logger.entering("LibItsFntp_asn1", "LibItsFntp_asn1");
        _encodingName = "CALMfntp";
    }
    
    protected Type getTypeForName(final String type) { 
        _logger.entering("LibItsFntp_asn1", "getTypeForName", type);
        
        String type_ = type;
        if (type.endsWith("nfSapPrimitivesUp")) {
            type_ = String.format("%s.%s", _encodingName, "NFsapPrimitivesUp");
        } else if (type.endsWith("mnRequestRequest")) {
            type_ = String.format("%s.%s", _encodingName, "MN_Request_request");
        } else if (type.endsWith("msgIn_nf")) {
            type_ = String.format("%s.%s", _encodingName, "NFsapPrimitivesDown");
        }
        
        Type asnOriginalType = super.getTypeForName(type_);
        
        _logger.exiting("LibItsFntp_asn1", "getTypeForName", asnOriginalType.getName());
        return asnOriginalType;
    } // End of method getTypeForName
    
} // End of class LibItsFntp_asn1 
