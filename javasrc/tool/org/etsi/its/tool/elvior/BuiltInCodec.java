package org.etsi.its.tool.elvior;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.elvior.ttcn.tritci.TciProvider;
import org.elvior.ttcn.tritci.TriMessageEx;
import org.elvior.ttcn.tritci.TriProvider;
import org.etsi.common.ByteHelper;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.TciCDProvided;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriMessage;

/**
 * 
 * Note that "Enable Internal Codec" field shall be set to true
 *
 */
public class BuiltInCodec implements TciCDProvided {
    
    /**
     * Logger instance
     */
    protected final static Logger _logger = Logger.getLogger("org.etsi.its");
    
    private String _encodingName;
    
    /**
     * Constructor
     */
    public BuiltInCodec(final String encodingName) {
        _logger.entering("BuiltInCodec", "BuiltInCodec", encodingName);
        
        if (encodingName.equals("LibItsDenm_asn1")) {
            _encodingName = "DENM_PDU_Descriptions";
        } else if (encodingName.equals("LibItsCam_asn1")) {
            _encodingName = "CAM_PDU_Descriptions";
        } else if (encodingName.equals("LibItsMapSpat_asn1")) {
            _encodingName = "MAP_SPAT_ETSI";
        } else if (encodingName.equals("LibItsCALMmsap_asn1")) {
            _encodingName = "CALMmsap";
        } else if (encodingName.equals("LibItsCALMllsap_asn1")) {
            _encodingName = "CALMllsap";
        } else if (encodingName.equals("LibItsFntp_asn1")) {
            _encodingName = "CALMfntp";
        } else if (encodingName.equals("LibItsFsap_asn1")) {
            _encodingName = "CALMfsap";
        } else if (encodingName.equals("LibItsIicp_asn1")) {
            _encodingName = "CALMiitsscu";
        } else if (encodingName.equals("LibItsMapSpat_asn1")) {
            _encodingName = "MAP_SPAT_ETSI";
        } else {
            _encodingName = "";
        }
    }
    
    /** 
     * This operation decodes message according to the encoding rules and returns a TTCN-3 value. 
     * The decodingHypothesis shall be used to determine whether the encoded value can be decoded. 
     * If an encoding rule is not self-sufficient, i.e. if the encoded message does not inherently 
     * contain its type decodingHypothesis shall be used. If the encoded value can be decoded without 
     * the decoding hypothesis, the distinct null value shall be returned if the type determined from 
     * the encoded message is not compatible with the decoding hypothesis
     * 
     * @param message The encoded message to be decoded
     * @param decodingHypothesis The hypothesis the decoding can be based on
     * @return Returns the decoded value, if the value is of a compatible type as the decodingHypothesis, else the distinct value null
     * 
     * See ETSI ES 201 873-6 V4.2.1 - 7.3.2.2.1 decode
     */
    @Override
    public Value decode(final TriMessage message, final Type decodingHypothesis) {
        _logger.entering("BuiltInCodec", "decode", decodingHypothesis.getName());
        
        TriMessageEx msg = TriProvider.getInstance().getTriFactory().createMessageEx();
        msg.setEncodedMessage(message.getEncodedMessage());
        if (_logger.isLoggable(Level.ALL)) ByteHelper.dump("BuiltInCodec.decode: ", msg.getEncodedMessage());
        
        String type = String.format("%s.%s", _encodingName, decodingHypothesis.getName());
        _logger.info("Type: " + type);
        Type asnOriginalType = getTypeForName(type);
        // Check which type class the decoding hypothesis is of
        Value value = TciProvider.getInstance().getSystemInterface().internalDecode(msg, asnOriginalType);
        _logger.exiting("BuiltInCodec", "decode", value.toString());
        return value;
    }
    
    @Override
    public TriMessage encode(final Value template) {
        _logger.entering("BuiltInCodec", "encode", template.getType().getName());
        
        RecordValue asn1Value = (RecordValue)template;
        String type = String.format("%s.%s", _encodingName, template.getType().getName());
        _logger.info("Type: " + type);
        Type asnOriginalType = getTypeForName(type);
        if (asnOriginalType != null) {
            RecordValue internalASNEncodecValue = (RecordValue)asnOriginalType.newInstance();
            String[] fields = internalASNEncodecValue.getFieldNames();
            for(String field: fields) {
                _logger.info(String.format("Process field %s", field));
                Value fieldValue = asn1Value.getField(field);
                if(fieldValue.notPresent()) { 
                    _logger.info(String.format("Field %s was omitted", field));
                    internalASNEncodecValue.setFieldOmitted(field);
                } else { 
                    _logger.info(String.format("Field %s was added", field));
                    internalASNEncodecValue.setField(field, fieldValue);
                }
            } // End of 'for' statement
            _logger.info("Call internal codec");
            TriMessage msg = TciProvider.getInstance().getSystemInterface().internalEncode(internalASNEncodecValue);
            ByteHelper.dump("BuiltInCodec.encode: ", msg.getEncodedMessage());
            _logger.exiting("BuiltInCodec", "encode");
            return msg;
        }
        
        _logger.exiting("BuiltInCodec", "encode", "null");
        return null;
    }
    
    private Type getTypeForName(final String type) { 
        _logger.entering("BuiltInCodec", "getTypeForName", type);
        
        Type asnOriginalType = TciProvider.getInstance().getTciCDRequired().getTypeForName(type);
        if (asnOriginalType == null) { // Failed with default mechanism, try with External Attributes mechanism 
//          type = decodingHypothesis.getName(); //FIXME External Attributes mechanism does not work 
            String type_ = "";
            if (type.endsWith("nfSapPrimitivesUp")) {
                type_ = String.format("%s.%s", _encodingName, "NFsapPrimitivesUp");
            } else if (type.endsWith("faSapPrimitivesUp")) {
                    type_ = String.format("%s.%s", _encodingName, "FAsapPrimitivesUp");
            } else if (type.endsWith("mnRequestRequest")) {
                type_ = String.format("%s.%s", _encodingName, "MN_Request_request");
            } else if (type.endsWith("mfCommandRequest")) {
                type_ = String.format("%s.%s", _encodingName, "MF_Command_request");
            } else if (type.endsWith("miCommandRequest")) {
                type_ = String.format("%s.%s", _encodingName, "MI_Command_request");
            } else if (type.endsWith("miCommandConfirm")) {
                type_ = String.format("%s.%s", _encodingName, "MI_Command_confirm");
            } else if (type.endsWith("miRequestRequest")) {
                type_ = String.format("%s.%s", _encodingName, "MI_Request_request");
            } else if (type.endsWith("miRequestConfirm")) {
                type_ = String.format("%s.%s", _encodingName, "MI_Request_confirm");
            } else if (type.endsWith("miSetRequest")) {
                type_ = String.format("%s.%s", _encodingName, "MI_Set_request");
            } else if (type.endsWith("miSetConfirm")) {
                type_ = String.format("%s.%s", _encodingName, "MI_Set_confirm");
            } else if (type.endsWith("miGetRequest")) {
                type_ = String.format("%s.%s", _encodingName, "MI_Get_request");
            } else if (type.endsWith("miGetConfirm")) {
                type_ = String.format("%s.%s", _encodingName, "MI_Get_confirm");
            } if (type.endsWith("fntpNPDU")) {
                type_ = String.format("%s.%s", _encodingName, "INsapPrimitivesUp");
            }
            _logger.info("Type: " + type_);
            asnOriginalType = TciProvider.getInstance().getTciCDRequired().getTypeForName(type_);
        }
        
        _logger.exiting("BuiltInCodec", "getTypeForName", asnOriginalType.getName());
        return asnOriginalType;
    } // End of method getTypeForName
    
} // End of class BuiltInCodec 
