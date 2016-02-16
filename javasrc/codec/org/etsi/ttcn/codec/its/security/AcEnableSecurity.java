/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/AcEnableSecurity.java $
 *              $Id: AcEnableSecurity.java 1803 2014-11-12 14:14:16Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class AcEnableSecurity extends Record {
    
    public AcEnableSecurity(MainCodec mainCodec) {
        super(mainCodec);
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> AcEnableSecurity.postEncodeField: " + fieldName);
        
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> AcEnableSecurity.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("certificateId")) {
            mainCodec.setHint(decodingHypothesis.getName() + "Len", String.valueOf(buf.getNbBytes())); 
        }
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> AcEnableSecurity.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
    }
    
} // End of class AcEnableSecurity
