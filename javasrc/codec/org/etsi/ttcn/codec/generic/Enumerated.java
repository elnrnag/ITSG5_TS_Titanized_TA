/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Enumerated.java $
 *              $Id: Enumerated.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.generic;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tci.EnumeratedValue;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class Enumerated extends ICodec {

    public Enumerated(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> Enumerated.decode: " + decodingHypothesis.getName());

        EnumeratedValue ev = (EnumeratedValue)decodingHypothesis.newInstance();
        int lengthInBits = 0;
        
        // Get length
        String hint = mainCodec.getHint(decodingHypothesis.getName() + "Len");
        if(hint == null) {
            lengthInBits = getVariantBitLength(decodingHypothesis.getTypeEncodingVariant());
        }
        else {
            lengthInBits = java.lang.Integer.parseInt(hint);
        }
        
        byte[] value = buf.readBits(lengthInBits);
        ev.setInt(ByteHelper.byteArrayToInt(value));

        return ev;
    }

    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println(">>> Enumerated.encode: " + value.getType().getName());

        EnumeratedValue ev = (EnumeratedValue)value;
        int lengthInBits = 0;
        int lengthInBytes = 0;
        CodecBuffer encoded = new CodecBuffer();

        // Get length
        String hint = mainCodec.getHint(value.getType().getName() + "Len");
        if(hint == null) {
            lengthInBits = getVariantBitLength(value.getType().getTypeEncodingVariant());
        }
        else {
            lengthInBits = java.lang.Integer.parseInt(hint);
        }
        lengthInBytes = lengthInBits / 8 + (((lengthInBits % 8) > 0)?1:0);
        encoded.setBits(ByteHelper.intToByteArray(ev.getInt(), lengthInBytes), lengthInBits);

        return encoded;
    }
}
