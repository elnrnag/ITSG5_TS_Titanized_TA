package org.etsi.ttcn.codec.ipv6;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.IMarkerCallback;

import org.etsi.ttcn.common.ByteHelper;

public class AnyIcmpv6Message extends Record implements IMarkerCallback {

    public AnyIcmpv6Message(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {

        if(fieldName.equals("data")) {
            mainCodec.setHint("octetstringLen", Integer.toString(buf.getNbBytes()));
        }
    }

    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {

        if(fieldName.equals("checksum")) {
            buf.setMarker("zchecksum", 0, this);
        }
    }

    @Override
    public void run(String markerName, CodecBuffer left, CodecBuffer right) {

        if(markerName.equals("zchecksum")) {

            int checksum = 0;
            byte[] src = left.getBits("sourceAddress", 128);
            byte[] dst = left.getBits("destinationAddress", 128);
            byte[] nh = ByteHelper.intToByteArray(java.lang.Integer.parseInt(mainCodec.getHint("Ipv6NextHeader")), 2);
            byte[] pl = ByteHelper.intToByteArray(java.lang.Integer.parseInt(mainCodec.getHint("payloadLength")), 2);

            byte[] pseudo = ByteHelper.concat(src, dst, pl, nh);
            byte[] payload = ByteHelper.concat(left.getBits(left.getNbBits() - 16, 16), right.getBits());

            byte[] all = ByteHelper.concat(pseudo, payload, new byte[]{(byte)0x00});

            for(int i=0; i < (all.length - 1); i+=2) {
                checksum += ((all[i] & 0x000000ff) << 8) + (all[i+1] & 0x000000ff);
                checksum = ((checksum >> 16) & 0x0000ffff) + (checksum & 0x0000ffff);
            }

            checksum = (~ checksum) & 0x0000ffff;

            CodecBuffer preserveRight = right.getBuffer(16, right.getNbBits() - 16);
            right.setBytes(ByteHelper.intToByteArray(checksum, 2));
            right.append(preserveRight);
        }
    }
}