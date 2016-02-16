package org.etsi.ttcn.codec.its.mapspat;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class MapSpatIndReq extends Record {

    public MapSpatIndReq(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> MapSpatIndReq.preDecode: " + decodingHypothesis);
        
        int msgLen = buf.getNbBits() - 48;
        
        messageBuffer = buf.getBuffer(0, msgLen);
        macBuffer = buf.getBuffer(msgLen, 48);
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> MapSpatIndReq.preDecodeField: " + fieldName + ", " + decodingHypothesis);
        
        if(fieldName.equals("macDestinationAddress")) {
            buf.overwriteWith(macBuffer);
        }
        else {
            buf.overwriteWith(messageBuffer);
        }
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> MapSpatIndReq.postDecodeField: " + fieldName + ", " + decodingHypothesis);
        
    }
    
    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println("MapSpatIndReq.encode: " + value);
        
        return super.encode(value);
    }
    
    private CodecBuffer messageBuffer = null;
    private CodecBuffer macBuffer = null;
} // End of class MapSpatIndReq
