/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/EciesEncryptedKey.java $
 *              $Id: EciesEncryptedKey.java 1731 2014-10-16 14:27:51Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class EciesEncryptedKey extends Record {
    
    final byte c_aes_128_ccm = 0x00; /** Symmetric key cryptography algorithm AES-CCM as specified in NIST SP 800-38C */
    
    public EciesEncryptedKey(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("Oct16Len", "16"); 
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> EciesEncryptedKey.postEncodeField: " + fieldName);
        
        if (fieldName.equals("c")) {
            int type = Integer.parseInt(mainCodec.getHint("SymmetricAlgorithm"));
            if (type != c_aes_128_ccm) { // Do not add length because of it is fixed to 32 
                CodecBuffer bufLen = new CodecBuffer(TlsHelper.getInstance().size2tls(buf.getNbBytes()));
                bufLen.append(buf);
                buf.overwriteWith(bufLen);
            }
        }
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> EciesEncryptedKey.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("c")) {
            int type = Integer.parseInt(mainCodec.getHint("SymmetricAlgorithm"));
            int len = 32;
            if (type != c_aes_128_ccm) {
                len = (int) TlsHelper.getInstance().tls2size(buf);
            }
            mainCodec.setHint("octetstringLen", Integer.toString(len));
        } else if (fieldName.equals("t")) {
            mainCodec.setHint("octetstringLen", "20");
        }
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> EciesEncryptedKey.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
    }
    
} // End of class EciesEncryptedKey