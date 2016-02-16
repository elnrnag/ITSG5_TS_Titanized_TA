package org.etsi.ttcn.codec.ipv6;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class ExtensionHeader extends Union {

    public ExtensionHeader(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {

        String nhHint = mainCodec.getHint("Ipv6NextHeader");
        String extHdr = "";

        if(nhHint != null) {
            int nh = Integer.parseInt(nhHint);
            switch(nh) {
            case 0:
                extHdr = "hopByHopHeader";
                break;
            case 43:
                extHdr = "routingHeader";
                break;
            case 44:
                extHdr = "fragmentHeader";
                break;
            case 60:
                extHdr = "destinationOptionHeader";
                break;
            case 41:
                extHdr = "tunnelIPv6";
                break;
            default:
                extHdr = "otherHeader";
            }
        }
        mainCodec.setHint("ExtensionHeader", extHdr);
    }
}
