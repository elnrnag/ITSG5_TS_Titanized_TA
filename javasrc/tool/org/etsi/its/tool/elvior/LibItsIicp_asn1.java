package org.etsi.its.tool.elvior;

import org.etsi.ttcn.tci.Type;

public class LibItsIicp_asn1 extends LibIts_asn1 {
    
    /**
     * Constructor
     */
    public LibItsIicp_asn1() {
        _logger.entering("LibItsIicp_asn1", "LibItsIicp_asn1");
        _encodingName = "CALMiitsscu";
    }
    
    protected Type getTypeForName(final String type) { 
        _logger.entering("LibItsIicp_asn1", "getTypeForName", type);
        
        String type_ = type;
        if (type.endsWith("msg_req")) {
            type_ = String.format("%s.%s", _encodingName, "IIC_Request");
        } else if (type.endsWith("msg_resp")) {
            type_ = String.format("%s.%s", _encodingName, "IIC_Response");
        } else  if (type.endsWith("mcmdRq")) {
            type_ = String.format("%s.%s", _encodingName, "McmdRq");
        }
        
        Type asnOriginalType = super.getTypeForName(type_);
        
        _logger.exiting("LibItsIicp_asn1", "getTypeForName", asnOriginalType.getName());
        return asnOriginalType;
    } // End of method getTypeForName
    
} // End of class LibItsIicp_asn1 
