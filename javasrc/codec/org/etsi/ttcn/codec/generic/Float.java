/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Float.java $
 *              $Id: Float.java 1917 2015-01-12 13:08:04Z berge $
 */
package org.etsi.ttcn.codec.generic;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.tci.FloatValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class Float extends ICodec {
    
    public Float(MainCodec mainCodec) {
        super(mainCodec);
    }
    
    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> Float.decode: " + decodingHypothesis.getName());
        
        FloatValue fv = mainCodec.getTciCDRequired().getFloat();
        fv.setFloat(ByteHelper.byteArrayToFloat(buf.readBits(java.lang.Float.SIZE)));
        
//        System.out.println("<<< Float.decode: " + fv.getFloat());
        return fv;
    }
    
    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println(">>> Float.encode: " + value.getType().getName());
        
        FloatValue fv = (FloatValue)value;
//        System.out.println("Float.encode: " + fv.getFloat());
        byte[] encoded = ByteHelper.floatToByteArray(fv.getFloat());
        CodecBuffer res = new CodecBuffer();
        res.setBits(encoded, encoded.length * Byte.SIZE);
        
        return res;
    }
    
}
