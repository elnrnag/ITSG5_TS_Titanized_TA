/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/ValidityRestriction.java $
 *              $Id: ValidityRestriction.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class ValidityRestriction extends Record {
    
    final byte c_time_end                 = 0x00;
    final byte c_time_start_and_end       = 0x01;
    final byte c_time_start_and_duration  = 0x02;
    final byte c_region                   = 0x03;
    
    public ValidityRestriction(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("ValidityRestrictionTypeLen", "8"); 
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> ValidityRestriction.postEncodeField: " + fieldName);
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> ValidityRestriction.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("type_")) {
            byte type_ = buf.getBytes(0, 1)[0];
            switch (type_) {
                case (byte)c_time_end:
                    mainCodec.setHint("ValidityRestrictionContainer", "end_validity"); // Set variant 
                    break;
                case (byte)c_time_start_and_end:
                    mainCodec.setHint("ValidityRestrictionContainer", "time_start_and_end"); // Set variant 
                    break;
                case (byte)c_time_start_and_duration:
                    mainCodec.setHint("ValidityRestrictionContainer", "time_start_and_duration"); // Set variant 
                    break;
                case (byte)c_region:
                    mainCodec.setHint("ValidityRestrictionContainer", "region"); // Set variant 
                    break;
                default:
                    mainCodec.setHint("SubjectAttributeContainer", "data"); // Set variant 
                    break;
            } // End of 'switch' statement
        } 
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> ValidityRestriction.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
    }
    
} // End of class ValidityRestriction