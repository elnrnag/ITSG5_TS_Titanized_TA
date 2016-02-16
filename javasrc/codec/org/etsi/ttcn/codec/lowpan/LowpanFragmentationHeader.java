package org.etsi.ttcn.codec.lowpan;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class LowpanFragmentationHeader extends Record {

    public LowpanFragmentationHeader(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {

        if(fieldName.equals("meshAddressingHdr")) {
            if(mainCodec.getHint("fragmentationHdr").equals("Frag1")) {
                mainCodec.setPresenceHint("offset", false);
            }	    
        }
    }
}
