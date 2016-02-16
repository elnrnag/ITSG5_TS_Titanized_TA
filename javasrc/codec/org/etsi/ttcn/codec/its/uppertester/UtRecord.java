/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/uppertester/UtRecord.java $
 *              $Id: UtRecord.java 1183 2013-09-19 14:59:27Z berge $
 */
package org.etsi.ttcn.codec.its.uppertester;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class UtRecord extends Record {

    public UtRecord(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
        
        Byte id = UtPduId.value(decodingHypothesis.getName());
        if(id != null) {
            byte[] readId = buf.readBytes(1);
            if(readId[0] != id) {
                return null;
            }
        }
        return super.decode(buf, decodingHypothesis);
    }
    
    @Override
    public CodecBuffer preEncode(Value value) {
        
        // Set Length for GeoShape
        mainCodec.setHint("GeoShapeLen", "8");
        
        Byte id = UtPduId.value(value.getType().getName());
        if(id != null) {
            return new CodecBuffer(new byte[] {id.byteValue()});
        }
        return null;
    }
    
}
