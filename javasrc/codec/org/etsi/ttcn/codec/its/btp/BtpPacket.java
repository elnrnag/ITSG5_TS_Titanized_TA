package org.etsi.ttcn.codec.its.btp;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class BtpPacket extends Record {

    public BtpPacket(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }
    
    private void setLengths() {
        mainCodec.setHint("BtpPortIdLen", "16"); 
        mainCodec.setHint("BtpPortInfoLen", "16"); 
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> BtpPacket.preDecodeField: " + fieldName + ", " + decodingHypothesis);
        
        if(fieldName.equals("payload")) {
            if (buf.getNbBytes() == 0) {
                mainCodec.setPresenceHint(fieldName, false);
            }
        }
    }

}
