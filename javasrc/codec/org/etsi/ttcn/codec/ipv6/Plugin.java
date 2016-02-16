package org.etsi.ttcn.codec.ipv6;

import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.codec.CodecFactory;

public class Plugin {

    public static void init() {

        CodecFactory cf = CodecFactory.getInstance();

        cf.setCodec(TciTypeClass.UNION, "IPv6", "ExtensionHeader", ExtensionHeader.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", "OtherIpv6ExtHdr", Ipv6ExtHdr.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", ".otherHeader", Ipv6ExtHdr.class);
        cf.setCodec(TciTypeClass.UNION, "IPv6", "Ipv6Payload", Ipv6Payload.class);
        cf.setCodec(TciTypeClass.UNION, "IPv6", "Icmpv6Message", Icmpv6Message.class);
        //	cf.setCodec(TciTypeClass.UNION, "IPv6", ".icmpv6", Icmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", "DestinationUnreachableMessage", AnyIcmpv6Message.class);	
        cf.setCodec(TciTypeClass.RECORD, "IPv6", ".destinationUnreachable", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", "PacketTooBigMessage", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", ".packetTooBig", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", "TimeExceededMessage", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", ".timeExceeded", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", "ParameterProblemMessage", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", ".parameterProblem", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", "EchoRequestMessage", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", ".echoRequest", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", "EchoReplyMessage", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", ".echoReply", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", "OtherIcmpv6Message", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", ".other", AnyIcmpv6Message.class);
        cf.setCodec(TciTypeClass.RECORD, "IPv6", "Ipv6Header", Ipv6Header.class);


    }
}