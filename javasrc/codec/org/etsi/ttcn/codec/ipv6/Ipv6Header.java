package org.etsi.ttcn.codec.ipv6;

import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.IMarkerCallback;

import org.etsi.ttcn.common.ByteHelper;

public class Ipv6Header extends Record implements IMarkerCallback {

    public Ipv6Header(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {

        if(fieldName.equals("payloadLength")) {
            buf.setMarker(fieldName, 0, this);
        }

        if(fieldName.equals("nextHeader")) {
            mainCodec.setHint("Ipv6NextHeader", Integer.toString(ByteHelper.byteArrayToInt(buf.getBytes(0, 1))));
        }

        else if(fieldName.equals("sourceAddress")) {
            buf.setMarker("sourceAddress", 0, null);
        }

        else if(fieldName.equals("destinationAddress")) {
            buf.setMarker("destinationAddress", 0, null);
        }

    }

    @Override
    public void run(String markerName, CodecBuffer leftBuf, CodecBuffer rightBuf) {

        if(markerName.equals("payloadLength")) {
            int length = (rightBuf.getNbBits() - 288) / 8;
            CodecBuffer preserveRightBuf = rightBuf.getBuffer(16, rightBuf.getNbBits() - 16);
            rightBuf.setBytes(ByteHelper.intToByteArray(length, 2));
            rightBuf.append(preserveRightBuf);
            mainCodec.setHint("payloadLength", Integer.toString(length));
        }
    }
}
