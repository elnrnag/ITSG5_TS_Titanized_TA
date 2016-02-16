/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/ServiceSpecificPermissions.java $
 *              $Id: ServiceSpecificPermissions.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class ServiceSpecificPermissions extends Record {
    
    public ServiceSpecificPermissions(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("Oct1Len", "1"); 
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> ServiceSpecificPermissions.postEncodeField: " + fieldName);
        
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> ServiceSpecificPermissions.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("version")) {
            byte version = buf.getBytes(0, 1)[0];
            switch (version) {
                case (byte)0x01:
                    mainCodec.setHint("version", String.valueOf(version)); 
                    break;
            } // End of 'switch' statement
        } else if (fieldName.equals("sspContainer")) {
            if (mainCodec.getHint("version") != null) {
                mainCodec.setPresenceHint(fieldName, true);
            } else {
                mainCodec.setPresenceHint(fieldName, false);
                
            }
        }
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> ServiceSpecificPermissions.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
    }
    
} // End of class ServiceSpecificPermissions
