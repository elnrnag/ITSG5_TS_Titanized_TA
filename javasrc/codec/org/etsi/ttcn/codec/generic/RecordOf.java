/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/RecordOf.java $
 *              $Id: RecordOf.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.generic;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tci.RecordOfValue;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class RecordOf extends ICodec {

    public RecordOf(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> RecordOf.decode: " + decodingHypothesis.getName());

        RecordOfValue rov = (RecordOfValue)decodingHypothesis.newInstance();
        String lenHint = mainCodec.getHint(decodingHypothesis.getName() + "Len");
        String moreHint = mainCodec.getHint(decodingHypothesis.getName() + "More");
        int length = 0;

        if(lenHint != null) {
            length = java.lang.Integer.parseInt(lenHint);

            for(int i=0; i < length; i++) {
                rov.appendField(mainCodec.decode(buf, rov.getElementType()));				
            }
        }
        else {
            while(moreHint.equals("true")) {
                rov.appendField(mainCodec.decode(buf, rov.getElementType()));
                length++;
                moreHint = mainCodec.getHint(decodingHypothesis.getName() + "More");
            }
        }

        rov.setLength(length);
        return rov;
    }

    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println(">>> RecordOf.encode: " + value.getType().getName());

        RecordOfValue rov = (RecordOfValue)value;
        CodecBuffer buf = new CodecBuffer();

        for (int i=0; i < rov.getLength(); i++) {
            Value fieldValue = rov.getField(i);
            buf.append(mainCodec.encode(fieldValue));
        }

        return buf;
    }
}
