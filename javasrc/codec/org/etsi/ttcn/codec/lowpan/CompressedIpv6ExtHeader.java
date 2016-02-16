package org.etsi.ttcn.codec.lowpan;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.EnumeratedValue;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class CompressedIpv6ExtHeader extends Record {

    public CompressedIpv6ExtHeader(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {

        if(fieldName.equals("inlineNextHeader")) {
            if(((EnumeratedValue)(rv.getField("nextHeader"))).getInt() != 0) {
                mainCodec.setPresenceHint(fieldName, false);
            }	    
        }
    }
}
