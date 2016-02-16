/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/SubjectAttribute.java $
 *              $Id: SubjectAttribute.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class SubjectAttribute extends Record {
    
    final byte c_verification_key        = 0x00;
    final byte c_encryption_key          = 0x01;
    final byte c_assurance_level         = 0x02;
    final byte c_reconstruction_value    = 0x03;
    final byte c_its_aid_list            = 0x20;
    final byte c_its_aid_ssp_list        = 0x21;
    
    public SubjectAttribute(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("SubjectAttributeTypeLen", "8"); 
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> SubjectAttribute.postEncodeField: " + fieldName);
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> SubjectAttribute.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("type_")) {
            byte type_ = buf.getBytes(0, 1)[0];
            switch (type_) {
                case (byte)c_verification_key:
                    // No break;
                case (byte)c_encryption_key:
                    mainCodec.setHint("SubjectAttributeContainer", "key"); // Set variant 
                    break;
                case (byte)c_reconstruction_value:
                    mainCodec.setHint("SubjectAttributeContainer", "rv"); // Set variant 
                    break;
                case (byte)c_assurance_level:
                    mainCodec.setHint("SubjectAttributeContainer", "assurance_level"); // Set variant 
                    break;
                case (byte)c_its_aid_list:
                    mainCodec.setHint("SubjectAttributeContainer", "its_aid_list"); // Set variant 
                    break; 
                case (byte)c_its_aid_ssp_list:
                    mainCodec.setHint("SubjectAttributeContainer", "its_aid_ssp_list"); // Set variant 
                    break;
                default:
                    mainCodec.setHint("SubjectAttributeContainer", "other_attribute"); // Set variant 
                    break;
            } // End of 'switch' statement
        } 
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> SubjectAttribute.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
    }
    
} // End of class SubjectAttribute