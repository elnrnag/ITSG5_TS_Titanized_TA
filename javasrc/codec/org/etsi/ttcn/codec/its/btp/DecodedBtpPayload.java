package org.etsi.ttcn.codec.its.btp;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.tci.Type;

public class DecodedBtpPayload extends Union {
    
    public DecodedBtpPayload(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }
    
    private void setLengths() {
        mainCodec.setHint("camPacket", "camPacket"); 
        mainCodec.setHint("denmPacket", "denmPacket"); 
    }
    
    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> DecodedBtpPayload.preDecode: " + decodingHypothesis);
    
    }
} // End of class DecodedBtpPayload 
