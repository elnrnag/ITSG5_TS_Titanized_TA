/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/SignerInfo.java $
 *              $Id: SignerInfo.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

/**
 * @desc Information about the signer of a message
 * @see Draft ETSI TS 103 097 V1.1.14 Clause 4.2.10	SignerInfo
 */
public class SignerInfo extends Record {
    
    final byte c_self                                       = 0x00; /** The data is self-signed */
    final byte c_certificate_digest_with_sha256             = 0x01; /** An 8 octet digest of the relevant certificate  */
    final byte c_certificate                                = 0x02; /** The relevant certificate itself  */
    final byte c_certificate_chain                          = 0x03; /** A complete certificate chain */
    final byte c_certificate_digest_with_other_algorithm    = 0x04; /** An 8 octet digest */
    
    /**
     * Constructor
     * @param mainCodec MainCodec reference
     */
    public SignerInfo(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    /**
     * @desc Predefined field lengths
     */
    private void setLengths() {
        mainCodec.setHint("SignerInfoTypeLen", "8");
        mainCodec.setHint("HashedId8Len", "8");
    }
    
    /**
     * @desc Set the variant according to the SignerInfo type
     * @see See Draft ETSI TS 103 097 V1.1.14 Clause 4.2.11	SignerInfoType
     */
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> SignerInfo.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("type_")) {
            byte type_ = buf.getBytes(0, 1)[0];
            mainCodec.setPresenceHint("signerInfo", (boolean)(type_ != c_self));
            switch (type_) {
                case (byte)c_self:
                    // Nothing to do
                    break;
                case (byte)c_certificate_digest_with_sha256:
                    mainCodec.setHint("SignerInfoContainer", "digest");
                    break;
                case (byte)c_certificate:
                    mainCodec.setHint("SignerInfoContainer", "certificate");
                    break;
                case (byte)c_certificate_chain:
                    mainCodec.setHint("SignerInfoContainer", "certificates");
                    break;
                case (byte)c_certificate_digest_with_other_algorithm:
                    mainCodec.setHint("SignerInfoContainer", "certificateWithAlgo");
                    break;
                default:
                    mainCodec.setHint("SignerInfoContainer", "info");
                    break;
            } // End of 'switch' statement
        }
    }
    
} // End of class SignerInfo