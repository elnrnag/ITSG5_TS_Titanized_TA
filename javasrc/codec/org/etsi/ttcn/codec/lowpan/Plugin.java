package org.etsi.ttcn.codec.lowpan;

import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.codec.CodecFactory;

public class Plugin {

    public static void init() {

        CodecFactory cf = CodecFactory.getInstance();

        cf.setCodec(TciTypeClass.RECORD, "6LoWPAN", "CompressedIpv6ExtHeader", CompressedIpv6ExtHeader.class);
        cf.setCodec(TciTypeClass.RECORD, "6LoWPAN", "CompressedTrafficClass", CompressedTrafficClass.class);
        cf.setCodec(TciTypeClass.RECORD, "6LoWPAN", "IphcHeader", IphcHeader.class);
        cf.setCodec(TciTypeClass.RECORD, "6LoWPAN", "LowpanFragmentationHeader", LowpanFragmentationHeader.class);
        cf.setCodec(TciTypeClass.UNION, "6LoWPAN", "LowpanEncapsulatedHeader", LowpanEncapsulatedHeader.class);
        cf.setCodec(TciTypeClass.RECORD, "6LoWPAN", "SixLowpanFrame", SixLowpanFrame.class);
    }
}