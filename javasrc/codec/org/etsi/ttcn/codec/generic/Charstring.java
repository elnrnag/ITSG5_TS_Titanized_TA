/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Charstring.java $
 *              $Id: Charstring.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.generic;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class Charstring extends ICodec {

    public Charstring(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {

        CharstringValue cv = (CharstringValue)decodingHypothesis.newInstance();
        int lengthInBytes = 0;

        // Get length
        String hint = mainCodec.getHint(decodingHypothesis.getName() + "Len");
        if(hint == null) {
            lengthInBytes = getEncodingLength(decodingHypothesis.getTypeEncoding());
        }
        else {
            lengthInBytes = java.lang.Integer.parseInt(hint);
        }

        byte[] value = buf.readBytes(lengthInBytes);

        cv.setLength(lengthInBytes);
        for(int i=0; i < value.length; i++) {
            cv.setChar(i, (char)value[i]);
        }

        return cv;
    }

    @Override
    public CodecBuffer encode(Value value) {

        CharstringValue cv = (CharstringValue)value;
        byte[] encoded = new byte[] {};
        if (cv.getString() != null) {
            encoded = cv.getString().getBytes();
        }

        return new CodecBuffer(encoded);
    }
}
