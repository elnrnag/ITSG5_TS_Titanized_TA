/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/uppertester/UtUnion.java $
 *              $Id: UtUnion.java 1133 2013-08-09 09:26:40Z berge $
 */
package org.etsi.ttcn.codec.its.uppertester;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.UnionValue;
import org.etsi.ttcn.tci.Value;

public class UtUnion extends Union {

    public UtUnion(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
        
        String variant = "";
        Byte type = new Byte((byte)(0x00FF & buf.readBytes(1)[0]));        
        Pattern variantNamePattern = Pattern.compile(decodingHypothesis.getName() + "_(\\S+)");
        
        Matcher matcher = variantNamePattern.matcher(UtPduId.name(type));
        if (matcher.find()) {
            variant = matcher.group(1);
        }   
        else {
            return null;
        }
        
        mainCodec.setHint(decodingHypothesis.getName(), variant);
        return super.decode(buf, decodingHypothesis);
    }

    @Override
    public CodecBuffer preEncode(Value value) {
        
        UnionValue uv = (UnionValue)value;
        
        Byte id = UtPduId.value(value.getType().getName() + "_" + uv.getPresentVariantName());
        if(id != null) {
            return new CodecBuffer(new byte[] {id.byteValue()});
        }
        return null;
    }
}
