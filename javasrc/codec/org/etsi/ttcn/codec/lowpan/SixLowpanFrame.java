package org.etsi.ttcn.codec.lowpan;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class SixLowpanFrame extends Record {

    public SixLowpanFrame(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {

        byte[] next = buf.getBytes(0, 1);

        if(fieldName.equals("meshAddressingHdr")) {
            if((next[0] & 0xC0) != 0x80) {
                mainCodec.setPresenceHint("meshAddressingHdr", false);
            }	    
        }

        else if(fieldName.equals("broadcastHdr")) {
            if((next[0] & 0xFF) != 0x50) {
                mainCodec.setPresenceHint("broadcastHdr", false);
            }	    
        }

        else if(fieldName.equals("fragmentationHdr")) {
            if(((next[0] & 0xF8) != 0xC0) && ((next[0] & 0xF8) != 0xE0)) {
                mainCodec.setPresenceHint("fragmentationHdr", false);
            }	    
            else {
                if((next[0] & 0xF8) == 0xC0) {
                    mainCodec.setHint("fragmentationHdr", "Frag1");
                }
                if((next[0] & 0xF8) == 0xE0) {
                    mainCodec.setHint("fragmentationHdr", "FragN");
                }
            }
        }

        else if(fieldName.equals("extHdrList")) {
            String nhHint = mainCodec.getHint("Ipv6NextHeader");

            if(nhHint != null) {
                int nh = java.lang.Integer.parseInt(nhHint);
                if(nh == 0 || nh == 60 || nh == 43 || nh == 44) { 
                    mainCodec.setPresenceHint("extHdrList", true);
                }
                else {
                    mainCodec.setPresenceHint("extHdrList", false);
                }
            }
            else {
                mainCodec.setPresenceHint("extHdrList", false);
                mainCodec.setPresenceHint("payload", false);
            }
        }
    }
}
