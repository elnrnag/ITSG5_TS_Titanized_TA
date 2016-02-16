/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Bitstring.java $
 *              $Id: Bitstring.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.generic;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tci.BitstringValue;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;


public class Bitstring extends ICodec {

    public Bitstring(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> Bitstring.decode: " + decodingHypothesis.getTypeEncoding());
        
        BitstringValue bv = (BitstringValue)decodingHypothesis.newInstance();
        int lengthInBits = getEncodingLength(decodingHypothesis.getTypeEncoding());
//        System.out.println("Bitstring.decode: lengthInBits=" + lengthInBits);
        CodecBuffer value = new CodecBuffer();
        value.setBits(buf.readBits(lengthInBits), lengthInBits);

        bv.setLength(lengthInBits);

        for(int i=0; i < lengthInBits; i++) {
            bv.setBit(i, value.readBits(1)[0]);
        }

        return bv;
    }

    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println(">>> Bitstring.encode: " + value);

        BitstringValue bv = (BitstringValue)value;
        CodecBuffer encoded = new CodecBuffer();

        for(int i=0; i < bv.getLength(); i++) {
            encoded.appendBits(new byte[]{(byte)bv.getBit(i)}, 1);
        }

        return encoded;
    }
}
