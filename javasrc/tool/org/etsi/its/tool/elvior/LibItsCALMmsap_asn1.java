package org.etsi.its.tool.elvior;

import org.etsi.ttcn.tci.Type;

public class LibItsCALMmsap_asn1 extends LibIts_asn1 {
    
    /**
     * Constructor
     */
    public LibItsCALMmsap_asn1() {
        _logger.entering("LibItsCALMmsap_asn1", "LibItsCALMmsap_asn1");
        _encodingName = "CALMmsap";
    }
    
    protected Type getTypeForName(final String type) { 
        _logger.entering("LibItsCALMmsap_asn1", "getTypeForName", type);
        
        String type_ = type;
        if (type.endsWith("mfRequestRequest")) {
            type_ = String.format("%s.%s", _encodingName, "MF_Request_request");
        } else if (type.endsWith("mnRequestRequest")) {
            type_ = String.format("%s.%s", _encodingName, "MN_Request_request");
        } else if (type.endsWith("miRequestRequest")) {
            type_ = String.format("%s.%s", _encodingName, "MI_Request_request");
        } else if (type.endsWith("mf_Command_request")) {
            type_ = String.format("%s.%s", _encodingName, "MF_Command_request");
        } else if (type.endsWith("mn_Command_request")) {
            type_ = String.format("%s.%s", _encodingName, "MN_Command_request");
        } else if (type.endsWith("mi_Command_request")) {
            type_ = String.format("%s.%s", _encodingName, "MI_Command_request");
        } else if (type.endsWith("faSapPrimitivesUp")) {
            type_ = String.format("%s.%s", _encodingName, "FAsapPrimitivesUp");
        } 
        
        Type asnOriginalType = super.getTypeForName(type_);
        
        _logger.exiting("LibItsCALMmsap_asn1", "getTypeForName", asnOriginalType.getName());
        return asnOriginalType;
    } // End of method getTypeForName
    
} // End of class LibItsCALMmsap_asn1 
