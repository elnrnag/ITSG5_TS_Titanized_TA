package org.etsi.ttcn.codec.lowpan;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class CompressedTrafficClass extends Record {

    public CompressedTrafficClass(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {

        int tc = Integer.parseInt(mainCodec.getHint("trafficClass"));

        switch(tc) {
        case 1:
            mainCodec.setPresenceHint("dscp", false);
            mainCodec.setPresenceHint("reserved2", false);
            break;
        case 2:
            mainCodec.setPresenceHint("flowLabel", false);
            mainCodec.setPresenceHint("reserved1", false);
            mainCodec.setPresenceHint("reserved2", false);
            break;
        }	    
    }
}