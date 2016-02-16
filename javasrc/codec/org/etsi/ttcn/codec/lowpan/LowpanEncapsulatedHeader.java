package org.etsi.ttcn.codec.lowpan;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class LowpanEncapsulatedHeader extends Union {

    public LowpanEncapsulatedHeader(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {

        byte[] dispatch = null;

        dispatch = buf.getBits(0, 3);
        if(dispatch[0] == 0x03) {
            mainCodec.setHint(decodingHypothesis.getName(), "ipch");
            return;
        }

        dispatch = buf.getBits(0, 8);
        if(dispatch[0] == 0x41 ) {
            mainCodec.setHint(decodingHypothesis.getName(), "uncompressedIpv6");
            return;
        }   

        mainCodec.setHint(decodingHypothesis.getName(), "unknown");
        mainCodec.setHint("octetstringLen", Integer.toString(buf.getNbBytes()));
        mainCodec.setHint(".unknownLen", Integer.toString(buf.getNbBytes()));
        return;
    }
}
