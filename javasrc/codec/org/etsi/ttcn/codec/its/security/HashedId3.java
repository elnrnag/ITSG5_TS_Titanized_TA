/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/HashedId3.java $
 *              $Id: HashedId3.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Octetstring;
import org.etsi.ttcn.tci.Type;

public class HashedId3 extends Octetstring {
    
    public HashedId3(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("HashedId3Len", "3"); 
    }
    
    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> HashedId3.preDecode: " + decodingHypothesis.getName());
    }
    
} // End of class HashedId3