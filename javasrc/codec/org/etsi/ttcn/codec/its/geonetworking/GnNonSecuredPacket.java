/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/geonetworking/GnNonSecuredPacket.java $
 *              $Id: GnNonSecuredPacket.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.its.geonetworking;

import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class GnNonSecuredPacket extends Record {
    
    private static final byte c_protocolVersion = 0x02;
    
    public GnNonSecuredPacket(MainCodec mainCodec) {
        super(mainCodec);
    }
    
    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> GnNonSecuredPacket.preDecode: " + decodingHypothesis);
        
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> GnNonSecuredPacket.preDecodeField: " + fieldName + ", " + decodingHypothesis);
        
        // FIXME Yann if payload length is 0, why payload is not set to omit? 
//        if(fieldName.equals("payload")) {
//            if (buf.getNbBytes() == 0) {
//                mainCodec.setPresenceHint(fieldName, false);
//            }
//        }
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> GnNonSecuredPacket.postDecodeField: " + fieldName + ", " + decodingHypothesis);
        
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> GnNonSecuredPacket.postEncodeField: " + fieldName);
        
        if(fieldName.equals("payload")) {
            // Compute Payload's length and set a hint
            int length = buf.getNbBits() / 8;
            mainCodec.setHint("payloadLength", Integer.toString(length));
        }
    }
    
    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println(">>> GnNonSecuredPacket.encode: " + value);
        
        return super.encode(value);
    }

    @Override
    public CodecBuffer preEncode(Value value) {
//        System.out.println(">>> GnNonSecuredPacket.preEncode: " + value);
        
        mainCodec.setHint("payloadLength", Integer.toString(0));
        return new CodecBuffer();
    }
    
}