/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/SecPayload.java $
 *              $Id: SecPayload.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class SecPayload extends Record {
    
    final byte c_signed_external  = 0x03; 
    
    public SecPayload(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("SecPayloadTypeLen", "8");
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> SecPayload.postEncodeField: " + fieldName);
        
        if (fieldName.equals("data")) {
            // Store first the octetstring length as specified in Draft ETSI TS 103 097 V1.1.14 Clause 4.2
            CodecBuffer bufLen = new CodecBuffer(new byte[] { (byte)buf.getNbBytes() } );
            bufLen.append(buf);
            buf.overwriteWith(bufLen);
        }
    }
    
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> SecPayload.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("type_")) {
            mainCodec.setPresenceHint("data", buf.getBytes(0, 1)[0] != c_signed_external);
        } else if (fieldName.equals("data") && (mainCodec.getPresenceHint("data") == true)) {
            int len = (int) TlsHelper.getInstance().tls2size(buf);
//            System.out.println("SecPayload.preDecodeField: len = " + len);
            mainCodec.setHint("octetstringLen", Integer.toString(len));
        }
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> SecPayload.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
    }
    
} // End of class SecPayload