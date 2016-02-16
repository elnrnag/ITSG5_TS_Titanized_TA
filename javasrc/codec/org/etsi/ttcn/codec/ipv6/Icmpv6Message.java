package org.etsi.ttcn.codec.ipv6;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class Icmpv6Message extends Union {

    public Icmpv6Message(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {

        int icmpType = 0x00FF & buf.getBytes(0, 1)[0];
        String icmpMsg = "";

        switch(icmpType) {
        case 144:
            icmpMsg = "homeAgentAddressDiscoveryRequest";
            break;
        case 145:
            icmpMsg = "homeAgentAddressDiscoveryReply";
            break;
        case 146:
            icmpMsg = "mobilePrefixSolicitation";
            break;
        case 147:
            icmpMsg = "mobilePrefixAdvertisement";
            break;
        case 133:
            icmpMsg = "routerSolicitation";
            break;
        case 134:
            icmpMsg = "routerAdvertisement";
            icmpMsg = "mipRouterAdvertisement";
            break;
        case 135:
            icmpMsg = "neighborSolicitation";
            break;
        case 136:
            icmpMsg = "neighborAdvertisement";
            break;
        case 137:
            icmpMsg = "redirect";
            break;
        case 138:
            icmpMsg = "routerRenumbering";
            break;
        case 1:
            icmpMsg = "destinationUnreachable";
            break;
        case 2:
            icmpMsg = "packetTooBig";
            break;
        case 3:
            icmpMsg = "timeExceeded";
            break;
        case 4:
            icmpMsg = "parameterProblem";
            break;
        case 128:
            icmpMsg = "echoRequest";
            break;
        case 129:
            icmpMsg = "echoReply";
            break;
        default:
            icmpMsg = "other";
        }

        mainCodec.setHint(decodingHypothesis.getName(), icmpMsg);
    }
}
