package org.etsi.ttcn.codec.its.geonetworking;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class TrafficClass extends Record {
    
    public TrafficClass(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("SCFLen", "1"); 
        mainCodec.setHint("ChannelOffloadLen", "1"); 
        mainCodec.setHint("TcIdLen", "6"); 
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> TrafficClass.postEncodeField: " + fieldName);
        
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> TrafficClass.preDecodeField: " + fieldName + ", " + decodingHypothesis);
    }

    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> TrafficClass.postDecodeField: " + fieldName + ", " + decodingHypothesis);

    }
}
