/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/HeaderField.java $
 *              $Id: HeaderField.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

/**
 * @desc Information of interest to the security layer
 * @see Draft ETSI TS 103 097 V1.1.14 Clause 5.4    HeaderField
 */
public class HeaderField extends Record {
    
    final byte c_generation_time                        = 0x00;                /** A timestamp of type Time64 */
    final byte c_generation_time_with_standard_deviation= 0x01;                /** A timestamp of type Time64WithStandardDeviation */
    final byte c_expiration                             = 0x02;                /** The point in time the validity of this message expires */
    final byte c_generation_location                    = 0x03;                /** The location where this message was created  */
    final byte c_request_unrecognized_certificate       = 0x04;                /** A request for certificates */
    final byte c_its_aid                                = 0x05;                /** Its AID valued used to identify CAM/DENM secured messages */
    final byte c_signer_info                            = (byte)0x80;          /** Information about the message's signer */
    final byte c_encryption_parameters                  = (byte)0x81;          /** Information specific for certain recipients */
    final byte c_recipient_info                         = (byte)0x82;          /** Additional parameters necessary for encryption purposes  */
    
    /**
     * Constructor
     * @param mainCodec MainCodec reference
     */
    public HeaderField(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }
    
    /**
     * @desc Predefined field lengths
     */
    private void setLengths() {
        mainCodec.setHint("HeaderFieldTypeLen", "8");
        mainCodec.setHint("Oct2Len", "2");
    }
    
    /**
     * @desc Set the variant according to the HeaderField type
     * @see See Draft ETSI TS 103 097 V1.1.14 Clause 5.5    HeaderFieldType
     */
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> HeaderField.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        if (fieldName.equals("type_")) {
            byte type_ = buf.getBytes(0, 1)[0];
            switch (type_) {
                case (byte)c_generation_time:
                    mainCodec.setHint("HeaderFieldContainer", "generation_time"); // Set variant 
                    break;
                case (byte)c_generation_time_with_standard_deviation:
                    mainCodec.setHint("HeaderFieldContainer", "generation_time_with_standard_deviation"); // Set variant 
                    break;
                case (byte)c_expiration:
                    mainCodec.setHint("HeaderFieldContainer", "expiry_time"); // Set variant 
                    break;
                case (byte)c_generation_location:
                    mainCodec.setHint("HeaderFieldContainer", "generation_location"); // Set variant 
                    break;
                case (byte)c_request_unrecognized_certificate:
                    mainCodec.setHint("HeaderFieldContainer", "digests"); // Set variant 
                    mainCodec.setHint("HashedId3Len", "3"); // FIXME TCT3 returns 12/length(3)/HashedId3 instead of 12/LibItsSecurity/HashedId3
                    break; 
                case (byte)c_its_aid:
                    mainCodec.setHint("HeaderFieldContainer", "its_aid"); // Set variant 
                    break;
                case (byte)c_signer_info:
                    mainCodec.setHint("HeaderFieldContainer", "signer"); // Set variant 
                    break;
                case (byte)c_encryption_parameters:
                    mainCodec.setHint("HeaderFieldContainer", "enc_params"); // Set variant 
                    break;
                case (byte)c_recipient_info:
                    mainCodec.setHint("HeaderFieldContainer", "recipients"); // Set variant 
                    break;
                default:
                    mainCodec.setHint("HeaderFieldContainer", "other_header"); // Set variant 
                    break;
            } // End of 'switch' statement
        }
    }
    
} // End of class HeaderField