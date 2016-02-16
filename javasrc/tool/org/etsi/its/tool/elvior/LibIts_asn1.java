package org.etsi.its.tool.elvior;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.elvior.ttcn.tritci.IntegerValueEx;
import org.elvior.ttcn.tritci.TciProvider;
import org.elvior.ttcn.tritci.TriMessageEx;
import org.elvior.ttcn.tritci.TriProvider;
import org.etsi.common.ByteHelper;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.TciCDProvided;
import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriMessage;

/**
 * 
 * Note that "Enable Internal Codec" field shall be set to true
 *
 */
public class LibIts_asn1 implements TciCDProvided {
    
    /**
     * Logger instance
     */
    protected final static Logger _logger = Logger.getLogger("org.etsi.its");
    
    protected String _encodingName;
    
    /**
     * Constructor
     */
    public LibIts_asn1() {
        _logger.entering("LibIts_asn1", "LibIts_asn1");
        _encodingName = "";
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
        _logger.entering("LibIts_asn1", "decode", decodingHypothesis.getName());
        
        TriMessageEx msg = TriProvider.getInstance().getTriFactory().createMessageEx();
        msg.setEncodedMessage(message.getEncodedMessage());
        if (_logger.isLoggable(Level.ALL)) ByteHelper.dump("LibIts_asn1.decode: ", msg.getEncodedMessage());
        
        String type = String.format("%s.%s", _encodingName, decodingHypothesis.getName());
        _logger.info("Type: " + type);
        Type asnOriginalType = getTypeForName(type);
        // Check which type class the decoding hypothesis is of
        _logger.info("asnOriginalType: " + asnOriginalType.getName());
        Value value = TciProvider.getInstance().getSystemInterface().internalDecode(msg, asnOriginalType);
        _logger.exiting("LibIts_asn1", "decode", value.toString());
        return value;
    }
    
    @Override
    public TriMessage encode(final Value template) {
        _logger.entering("LibIts_asn1", "encode", template.getType().getName());
        
        String type = String.format("%s.%s", _encodingName, template.getType().getName());
        _logger.info("Type: " + type);
        Type asnOriginalType = getTypeForName(type);
        if (asnOriginalType != null) {
            Value internalASNEncodecValue = null;
            switch (template.getType().getTypeClass()) {
                case TciTypeClass.RECORD:
                    internalASNEncodecValue = (RecordValue)asnOriginalType.newInstance();
                    String[] fields = ((RecordValue)internalASNEncodecValue).getFieldNames();
                    RecordValue asn1Value = (RecordValue)template;
            for(String field: fields) {
                _logger.info(String.format("Process field %s", field));
                Value fieldValue = asn1Value.getField(field);
                if(fieldValue.notPresent()) { 
                    _logger.info(String.format("Field %s was omitted", field));
                            ((RecordValue)internalASNEncodecValue).setFieldOmitted(field);
                } else { 
                    _logger.info(String.format("Field %s was added", field));
                            ((RecordValue)internalASNEncodecValue).setField(field, fieldValue);
                }
            } // End of 'for' statement
                    break;
                case TciTypeClass.INTEGER:
                    internalASNEncodecValue = (IntegerValueEx)asnOriginalType.newInstance();
                    ((IntegerValueEx)internalASNEncodecValue).setInt64(((IntegerValueEx)template).getInt64());
                    break;
                default:
                    throw new RuntimeException("Unimplemented type " + template.getType().getTypeClass());
            } // End of 'switch' statement
            _logger.info("Call internal codec");
            TriMessage msg = TciProvider.getInstance().getSystemInterface().internalEncode(internalASNEncodecValue);
            ByteHelper.dump("LibIts_asn1.encode: ", msg.getEncodedMessage());
            _logger.exiting("LibIts_asn1", "encode");
            return msg;
        }
        
        _logger.exiting("LibIts_asn1", "encode", "null");
        return null;
    }
    
    protected Type getTypeForName(final String type) { 
        _logger.entering("LibIts_asn1", "getTypeForName", type);
        
        Type asnOriginalType = TciProvider.getInstance().getTciCDRequired().getTypeForName(type);
        
        _logger.exiting("LibIts_asn1", "getTypeForName", asnOriginalType.getName());
        return asnOriginalType;
    } // End of method getTypeForName
    
} // End of class LibIts_asn1 
