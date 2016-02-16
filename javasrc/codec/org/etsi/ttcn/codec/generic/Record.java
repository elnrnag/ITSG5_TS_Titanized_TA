/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Record.java $
 *              $Id: Record.java 1133 2013-08-09 09:26:40Z berge $
 */
package org.etsi.ttcn.codec.generic;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class Record extends ICodec {

    public Record(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {

        RecordValue rv = (RecordValue)decodingHypothesis.newInstance();
        String[] fields = rv.getFieldNames(); 

        for(int i=0; i < fields.length; i++) {
            Type fieldType = rv.getField(fields[i]).getType();
            preDecodeField(fields[i], buf, fieldType, rv);

            java.lang.Boolean presence = mainCodec.getPresenceHint(fields[i]);
            if(presence != null && presence == false) {
                rv.setFieldOmitted(fields[i]);
            }
            else {
                Value fv = mainCodec.decode(buf, rv.getField(fields[i]).getType());
                if(fv == null) {                    
                    if(mainCodec.getHint(fields[i] + "IgnoreErrorOnOptionalField") != null) {
                        // Set to omit + warning if optional
                        System.err.println("Unable to decode optional field '" + fields[i] + "'. Setting to 'omit'");
                        rv.setFieldOmitted(fields[i]);
                    }
                    else {
                        return null;
                    }
                }
                else {
                    rv.setField(fields[i], fv);
                }
            }
            postDecodeField(fields[i], buf, fieldType, rv); 
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
                CodecBuffer fieldBuf = mainCodec.encode(fieldValue);
                postEncodeField(fields[i], fieldBuf);
                buf.append(fieldBuf);
            }
        }

        return buf;
    }

    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {

    }

    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {

    }

    protected void postEncodeField(String fieldName, CodecBuffer buf) {

    }
}
