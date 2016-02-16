package org.etsi.its.tool.elvior;

import org.elvior.ttcn.tritci.TriProvider;
import org.etsi.common.ByteHelper;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriMessage;

/**
 * 
 * Note that "Enable Internal Codec" field shall be set to true
 *
 */
public class LibItsContainer_asn1 extends LibIts_asn1 {
    
    /**
     * Constructor
     */
    public LibItsContainer_asn1() {
        _logger.entering("LibItsContainer_asn1", "LibItsContainer_asn1");
        _encodingName = "ITS_Container";
    }
    
    @Override
    public Value decode(final TriMessage message, final Type decodingHypothesis) {
        _logger.entering("LibItsContainer_asn1", "decode", decodingHypothesis.getName());
        
        Value value = null;
        if (decodingHypothesis.getName().equals("TimestampIts")) {
        	int ival = ByteHelper.byteArrayToInt(message.getEncodedMessage());
        	long lval = ((long)ival) << 6;
            TriMessage msg = TriProvider.getInstance().getTriFactory().createMessage();
            msg.setEncodedMessage(ByteHelper.longToByteArray(lval, 6));
            value = super.decode(msg, decodingHypothesis); 
        } else {
            value = super.decode(message, decodingHypothesis);
        }
        
        _logger.exiting("LibIts_asn1", "decode", value.toString());
        return value;
    }
    
} // End of class BuiltInCodec 
