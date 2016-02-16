package org.etsi.ttcn.codec.its.geonetworking;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class GN_Address extends Record {
    
    public GN_Address(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("TypeOfAddressLen", "1"); 
        mainCodec.setHint("StationTypeLen", "5"); 
        mainCodec.setHint("Oct6Len", "6"); 
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> GN_Address.postEncodeField: " + fieldName);
        
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> GN_Address.preDecodeField: " + fieldName + ", " + decodingHypothesis);
    }

    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> GN_Address.postDecodeField: " + fieldName + ", " + decodingHypothesis);

    }
}
