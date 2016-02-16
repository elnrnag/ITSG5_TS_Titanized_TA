package org.etsi.ttcn.codec.generic;
/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Octetstring.java $
 *              $Id: Octetstring.java 1822 2014-11-18 09:18:17Z berge $
 */


import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tci.OctetstringValue;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class Octetstring extends ICodec {

    public Octetstring(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> Octetstring.decode: " + decodingHypothesis.getName());

        OctetstringValue ov = (OctetstringValue)decodingHypothesis.newInstance();
        byte[] value = null;
        int lengthInBytes = 0;

        // Get length
        String hint = mainCodec.getHint(decodingHypothesis.getName() + "Len");
        if(hint == null) {
            lengthInBytes = getEncodingLength(decodingHypothesis.getTypeEncoding());
            if (lengthInBytes == 0) {
//                System.out.println("Octetstring.decode: Decode full buffer");
                lengthInBytes = buf.getNbBytes();
            }
        }
        else {
            lengthInBytes = java.lang.Integer.parseInt(hint);
        }

        value = buf.readBytes(lengthInBytes);
        ov.setLength(lengthInBytes);
        for(int i=0; i < value.length; i++) {
            ov.setOctet(i, value[i]);
        }

        return ov;
    }

    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println(">>> Octetstring.encode: " + value.getType().getName());

        OctetstringValue ov = (OctetstringValue)value;
        byte[] encoded = new byte[ov.getLength()];

        for(int i=0; i < ov.getLength(); i++) {
            encoded[i] = (byte)ov.getOctet(i);
        }

        return new CodecBuffer(encoded);
    }
}
