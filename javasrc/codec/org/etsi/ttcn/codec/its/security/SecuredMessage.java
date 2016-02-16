/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/SecuredMessage.java $
 *              $Id: SecuredMessage.java 1731 2014-10-16 14:27:51Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;

public class SecuredMessage extends Record {

    public SecuredMessage(MainCodec mainCodec) {
        super(mainCodec);
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> SecuredMessage.postEncodeField: " + fieldName);
        
    }
    
} // End of class SecuredMessage