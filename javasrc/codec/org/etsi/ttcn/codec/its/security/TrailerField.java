/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/TrailerField.java $
 *              $Id: TrailerField.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

/**
 * @desc Information used by the security layer after processing the payload
 * @see Draft ETSI TS 103 097 V1.1.14 Clause 5.6    TrailerField
 */
public class TrailerField extends Record {
    
    final byte c_signature = 0x01;
    
    /**
     * Constructor
     * @param mainCodec MainCodec reference
     */
    public TrailerField(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    /**
     * @desc Predefined field lengths
     */
    private void setLengths() {
        mainCodec.setHint("TrailerFieldTypeLen", "8");
    }
    
    /**
     * @desc Set the variant according to the TrailerField type
     * @see See Draft ETSI TS 103 097 V1.1.14 Clause 5.7    TrailerFieldType
     */
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> TrailerField.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("type_")) {
            byte type_ = buf.getBytes(0, 1)[0];
            switch (type_) {
                case (byte)c_signature:
                    mainCodec.setHint("TrailerFieldContainer", "signature_");
                    break;
                default:
                    mainCodec.setHint("TrailerFieldContainer", "security_field");
                    break;
            } // End of 'switch' statement
        }
    }
    
} // End of class TrailerField