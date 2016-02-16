/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/Signature.java $
 *              $Id: Signature.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class Signature extends Record {
    
    /**
     * List of supported algorithms based on public key cryptography
     */
    final byte e_ecdsa_nistp256_with_sha256 = 0x00;
    /**
     * List of supported algorithms based on public key cryptography
     */
    final byte e_ecies_nistp2561 = 0x01;
    
    public Signature(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("PublicKeyAlgorithmLen", "8"); 
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> Signature.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        if (fieldName.equals("algorithm")) {
            if (buf.getBytes(0, 1)[0] == e_ecdsa_nistp256_with_sha256) {
                mainCodec.setHint("SignatureContainer", "ecdsa_signature");
            } else if (buf.getBytes(0, 1)[0] == e_ecies_nistp2561) {
                mainCodec.setHint("SignatureContainer", "signature_");
            }
        }
    }
    
} // End of class Signature
