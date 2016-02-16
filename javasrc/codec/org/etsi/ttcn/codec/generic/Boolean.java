package org.etsi.ttcn.codec.generic;
/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Boolean.java $
 *              $Id: Boolean.java 1133 2013-08-09 09:26:40Z berge $
 */


import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.tci.BooleanValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class Boolean extends ICodec {

    public Boolean(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
        
        BooleanValue bv = (BooleanValue)decodingHypothesis.newInstance();
        byte[] value = buf.readBits(8);

        if(value[0] > 0) {
            bv.setBoolean(true);
        }
        else {
            bv.setBoolean(false);
        }
        return bv;
    }

    @Override
    public CodecBuffer encode(Value value) {
        
        BooleanValue bv = (BooleanValue)value;
        CodecBuffer encoded = new CodecBuffer();

        if(bv.getBoolean() == true) {
            encoded.appendBytes(new byte[] {(byte)0x01});
        }
        else {
            encoded.appendBytes(new byte[] {(byte)0x00});
        }

        return encoded;
    }
    
    
}