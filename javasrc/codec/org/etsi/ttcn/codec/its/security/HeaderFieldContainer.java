/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/HeaderFieldContainer.java $
 *              $Id: HeaderFieldContainer.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.tci.OctetstringValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.UnionValue;

/**
 * @desc Container for the information of interest to the security layer
 * @see Draft ETSI TS 103 097 V1.1.14 Clause 5.4    HeaderField
 */
public class HeaderFieldContainer extends Union {

    public HeaderFieldContainer(MainCodec mainCodec) {
        super(mainCodec);
    }
    
    @Override
    protected void preEncode(CodecBuffer buf, UnionValue uv) {
//        System.out.println(">>> HeaderFieldContainer.preEncode: " + uv.getType().getName() + ", " + uv.getPresentVariantName());
        
        if (uv.getPresentVariantName().equals("other_header")) {
            buf.appendBytes(new byte[] { (byte)((OctetstringValue)(uv.getVariant(uv.getPresentVariantName()))).getLength() } );
        }
    }
    
    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> HeaderFieldContainer.preDecode: " + decodingHypothesis.getName());
    }
    
} // End of class HeaderFieldContainer
