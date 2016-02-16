/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/RecipientInfo.java $
 *              $Id: RecipientInfo.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class RecipientInfo extends Record {
    
    final byte c_ecies_nistp2561             = 0x01; /** List of supported algorithms based on public key cryptography */
    
    public RecipientInfo(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("HashedId8Len", "8");
        mainCodec.setHint("PublicKeyAlgorithmLen", "8");
        mainCodec.setHint("Oct20Len", "20");
    }
    
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> RecipientInfo.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("pk_encryption")) {
            if (buf.getBytes(0, 1)[0] == c_ecies_nistp2561) {
                mainCodec.setHint("RecipientInfoContainer", "enc_key"); // Set variant 
            } else {
                mainCodec.setHint("RecipientInfoContainer", "enc_key_other"); // Set variant 
            }
        } 
    }

    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> RecipientInfo.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
    }

    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> RecipientInfo.postEncodeField: " + fieldName);
    }
} // End of class RecipientInfo