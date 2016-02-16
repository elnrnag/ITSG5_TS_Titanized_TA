package org.etsi.ttcn.codec.ipv6;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

import org.etsi.ttcn.common.ByteHelper;

public class Ipv6ExtHdr extends Record {

    public Ipv6ExtHdr(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {

        if(fieldName.equals("data")) {
            mainCodec.setHint("octetstringLen", mainCodec.getHint("Ipv6ExtHdrLen"));
        }
    }

    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {

        if(fieldName.equals("hdrLen")) {
//            int len = ((IntegerValue)(rv.getField(fieldName))).getInteger(); TTWB iterface is getInt, TCI shall be getInteger
            int len = mainCodec.getTciCDRequired().getInteger((IntegerValue)(rv.getField(fieldName)));
            mainCodec.setHint("Ipv6ExtHdrLen", Integer.toString(len));
        }
    }

    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {

        if(fieldName.equals("nextHeader")) {
            mainCodec.setHint("Ipv6NextHeader", Integer.toString(ByteHelper.byteArrayToInt(buf.getBytes(0, 1))));
        }
    }
}