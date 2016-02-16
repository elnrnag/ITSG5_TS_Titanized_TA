/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/SubjectAttributeContainer.java $
 *              $Id: SubjectAttributeContainer.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.tci.OctetstringValue;
import org.etsi.ttcn.tci.UnionValue;

public class SubjectAttributeContainer extends Union {

    public SubjectAttributeContainer(MainCodec mainCodec) {
        super(mainCodec);
    }
    
    @Override
    protected void preEncode(CodecBuffer buf, UnionValue uv) {
//        System.out.println(">>> SubjectAttributeContainer.preEncode: " + uv.getType().getName() + ", " + uv.getPresentVariantName());
        
        if (uv.getPresentVariantName().equals("other_attribute")) {
            buf.appendBytes(new byte[] { (byte)((OctetstringValue)(uv.getVariant(uv.getPresentVariantName()))).getLength() } );
        }
    }
    
} // End of class SubjectAttributeContainer
