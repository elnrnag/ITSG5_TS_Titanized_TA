/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/EncryptionParameters.java $
 *              $Id: EncryptionParameters.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class EncryptionParameters extends Record {
    
    final byte c_aes_128_ccm = 0x00; /** Symmetric key cryptography algorithm AES-CCM as specified in NIST SP 800-38C */
    
    public EncryptionParameters(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("SymmetricAlgorithmLen", "8"); 
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> EncryptionParameters.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        if (fieldName.equals("symm_algorithm")) {
            byte type_ = buf.getBytes(0, 1)[0];
            mainCodec.setHint("SymmetricAlgorithm", String.valueOf(type_));
            switch (type_) {
                case (byte)c_aes_128_ccm:
                    mainCodec.setHint("EncryptionParametersContainer", "nonce"); // Set variant 
                    break;
                default:
                    mainCodec.setHint("EncryptionParametersContainer", "params"); // Set variant 
            } // End of 'switch' statement
        } else {
            if (mainCodec.getHint("EncryptionParametersContainer").equals("nonce")) {
                mainCodec.setHint("octetstringLen", "12");
                mainCodec.setHint("EncryptionParametersContainer.nonceLen", "12"); // TCT3 Decoding HeaderFieldContainer.enc_params, the type nonce became EncryptionParametersContainer.nonce
            } else {
                int len = buf.readBits(Byte.SIZE)[0]; // field_sizeLen is 1 bytes
                mainCodec.setHint("EncryptionParametersContainer.paramsLen", Integer.toString(len));
            }
        }
    }
    
} // End of class EncryptionParameters