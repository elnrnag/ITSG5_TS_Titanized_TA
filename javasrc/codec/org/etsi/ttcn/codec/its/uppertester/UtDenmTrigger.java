/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: $
 *              $Id: $
 */
package org.etsi.ttcn.codec.its.uppertester;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.IMarkerCallback;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

import java.util.HashMap;
import java.util.Map;

public class UtDenmTrigger extends UtRecord implements IMarkerCallback {

    private byte flags = 0x00;

    public UtDenmTrigger(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
        setLengths();
        return super.decode(buf, decodingHypothesis);
    }

    @Override
    public CodecBuffer preEncode(Value value) {
        setLengths();
        CodecBuffer buf =  super.preEncode(value);
        CodecBuffer bufFlags = new CodecBuffer();
        bufFlags.setMarker("flags", 0, this);
        buf.append(bufFlags);
        return buf;
    } 
    
    private void setLengths() {
        mainCodec.setHint("TimestampItsLen", "48");
        mainCodec.setHint("ValidityDurationLen", "24");
        mainCodec.setHint("InformationQualityLen", "8");
        mainCodec.setHint("CauseCodeTypeLen", "8");
        mainCodec.setHint("SubCauseCodeTypeLen", "8");
        mainCodec.setHint("RelevanceDistanceLen", "8");
        mainCodec.setHint("RelevanceTrafficDirectionLen", "8");
        mainCodec.setHint("TransmissionIntervalLen", "16");
        mainCodec.setHint("StationIDLen", "32");
        mainCodec.setHint("SequenceNumberLen", "16");
        mainCodec.setHint("integerLen", "8");
        mainCodec.setHint("AlacarteContainerLen", "8");
    }

    @Override
    public CodecBuffer encode(Value value) {
        
        RecordValue rv = (RecordValue)value;
        String[] fields = rv.getFieldNames();
        CodecBuffer buf = new CodecBuffer();

        for(int i=0; i < fields.length; i++) {
            Value fieldValue = rv.getField(fields[i]);
            if(!fieldValue.notPresent()) {
                CodecBuffer fieldBuf = mainCodec.encode(fieldValue);
                postEncodeField(fields[i], fieldBuf);
                buf.append(fieldBuf);
                flags = (byte) (flags | (PresenceFlag.value(fields[i])).byteValue());
            }
            else {
                String hint = mainCodec.getHint(fieldValue.getType().getName() + "Len");
                if(hint != null) {
                    int lengthInBits = java.lang.Integer.parseInt(hint);
                    int lengthInBytes = lengthInBits / 8 + (((lengthInBits % 8) > 0)?1:0);
                    for(int j=0; j < lengthInBytes; j++) {
                        buf.appendBytes(new byte[]{0x00});
                    }
                }
            }
        }
        
        return buf;
    }
    
    @Override
    public void run(String markerName, CodecBuffer leftBuf, CodecBuffer rightBuf) {
        CodecBuffer bufFlags = new CodecBuffer(new byte[] {flags});
        bufFlags.append(rightBuf);
        rightBuf.setBytes(bufFlags.getBytes());
    }

    private enum PresenceFlag {
        
        /* DenmTrigger*/
        validityDuration(0x80),
        repetitionDuration(0x40),        
        transmissionInterval(0x04),
        repetitionInterval(0x02),
        
        /* Reserved */
        reserved(0x00);
        
        private byte value;
        private static final Map<String, Byte> PresenceFlags = new HashMap<String, Byte>();
        
        private PresenceFlag(int value) {
            this.value = (byte)value;
        }
        
        public static Byte value(String name) {
            Byte res = PresenceFlags.get(name);
            if(res == null) {
                return value("reserved");
            }
            return res;
        }
                        
        static {
            for (PresenceFlag item : PresenceFlag.values()) {
                PresenceFlags.put(item.name(), new Byte(item.value));
            }
        }
    }
}
