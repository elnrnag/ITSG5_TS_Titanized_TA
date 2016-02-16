/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/EcdsaSignature.java $
 *              $Id: EcdsaSignature.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

/**
 * @desc Codec for ECDSA based signature
 * @see Draft ETSI TS 103 097 V1.1.14 Clause 4.2.9    EcdsaSignature
 */
public class EcdsaSignature extends Record {
    
    final byte c_uncompressed = 0x04; /** The y coordinate is encoded in the field y */
    
    /**
     * Constructor
     * @param mainCodec MainCodec reference
     */
    public EcdsaSignature(MainCodec mainCodec) {
        super(mainCodec);
    }
    
    /**
     * @desc Set the variant according to the EcdsaSignature type
     * @see See Draft ETSI TS 103 097 V1.1.14 Clause 4.2.9    EcdsaSignature
     */
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> EcdsaSignature.postEncodeField: " + fieldName);
        
        if (fieldName.equals("s")) {
            int type = Integer.parseInt(mainCodec.getHint("EccPointType"));
            // TODO Store first the octetstring length as specified in Draft ETSI TS 103 097 V1.1.14 Clause 4.2
//            if (type != c_uncompressed) { // Do not add length because of it is fixed to 32 
//                CodecBuffer bufLen = new CodecBuffer(TlsHelper.getInstance().size2tls(buf.getNbBytes()));
//                bufLen.append(buf);
//                buf.overwriteWith(bufLen);
//            }
        }
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> EcdsaSignature.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("s")) {
            int type = Integer.parseInt(mainCodec.getHint("EccPointType"));
            int len = 32;
            // TODO Store first the octetstring length as specified in Draft ETSI TS 103 097 V1.1.14 Clause 4.2
//            if (type != c_uncompressed) {
//                len = (int) TlsHelper.getInstance().tls2size(buf);
//            }
            mainCodec.setHint("octetstringLen", Integer.toString(len));
        }
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> EcdsaSignature.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
    }
    
} // End of class EcdsaSignature