package org.etsi.ttcn.codec.ipv6;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class Ipv6Payload extends Union {

    public Ipv6Payload(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {


        String nhHint = mainCodec.getHint("Ipv6NextHeader");
        String extHdr = "";

        if(nhHint != null) {
            int nh = Integer.parseInt(nhHint);
            switch(nh) {
            case 58:
                extHdr = "icmpv6";
                break;
            case 6:
                extHdr = "tcp";
                break;
            case 17:
                extHdr = "udp";
                break;
            default:
                extHdr = "other";
            }
        }
        mainCodec.setHint("Ipv6Payload", extHdr);

    }
}
