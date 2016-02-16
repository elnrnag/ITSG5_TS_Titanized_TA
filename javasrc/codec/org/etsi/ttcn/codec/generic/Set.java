/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Set.java $
 *              $Id: Set.java 1133 2013-08-09 09:26:40Z berge $
 */
package org.etsi.ttcn.codec.generic;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class Set extends ICodec {

    public Set(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {

        RecordValue rv = (RecordValue)decodingHypothesis.newInstance();
        String[] fields = rv.getFieldNames(); 

        for(int i=0; i < fields.length; i++) {
            rv.setFieldOmitted(fields[i]);
        }
        return rv;
    }

    @Override
    public CodecBuffer encode(Value value) {

        RecordValue rv = (RecordValue)value;
        String[] fields = rv.getFieldNames();
        CodecBuffer buf = new CodecBuffer();

        for (int i=0; i < fields.length; i++) {
            Value fieldValue = rv.getField(fields[i]);
            if(!fieldValue.notPresent()) {
                buf.append(mainCodec.encode(fieldValue));
            }
        }

        return buf;
    }
}