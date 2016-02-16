/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/geonetworking/GeoNetworkingPacket.java $
 *              $Id: GeoNetworkingPacket.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.its.geonetworking;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.tci.OctetstringValue;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class GeoNetworkingPacket extends Record {
    
    private boolean isSecuredMode = false;
    
    /**
     * Used to decode the secured payload
     */
    private Value packetValue;
    
    public GeoNetworkingPacket(MainCodec mainCodec) {
        super(mainCodec);
    }
    
    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println(">>> GeoNetworkingPacket.encode: " + value);
        
        return super.encode(value);
    }

    @Override
    public CodecBuffer preEncode(Value value) {
//        System.out.println(">>> GeoNetworkingPacket.preEncode: " + value);
        
        String gnNextHeader = mainCodec.getHint("GnNextHeader");
        isSecuredMode = (boolean)((gnNextHeader != null) && gnNextHeader.equals("32"));
        
        return new CodecBuffer();
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> GeoNetworkingPacket.postEncodeField: " + fieldName + ", " + isSecuredMode);
        
        if (isSecuredMode && fieldName.equals("packet")) {
            // Ignore 'packet' encoding by reseting the CodecBuffer content
            buf.overwriteWith(new CodecBuffer());
        }
    }
    
    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> GeoNetworkingPacket.decode: " + decodingHypothesis + ", " + isSecuredMode);
        
        packetValue = null; // Reset value
        Value result = super.decode(buf, decodingHypothesis); // Normal decoding
//        System.out.println("GeoNetworkingPacket.decode: Normal decoding=" + result);
        if(isSecuredMode) {
            // Override 'packet' field
            RecordValue rv = (RecordValue) result;
            rv.setField("packet", packetValue);
        }
        
//        System.out.println("<<< GeoNetworkingPacket.decode: " + result);
        return result;
    }
    
    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> GeoNetworkingPacket.preDecode: " + decodingHypothesis);
        
        String gnNextHeader = mainCodec.getHint("GnNextHeader");
        isSecuredMode = (boolean)((gnNextHeader != null) && gnNextHeader.equals("2"));
//        System.out.println("GeoNetworkingPacket.preDecode: isSecuredMode=" + isSecuredMode);
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> GeoNetworkingPacket.preDecodeField: " + fieldName + ", " + decodingHypothesis + ", " + isSecuredMode);
        
        if(isSecuredMode) {
            if (fieldName.equals("packet")) {
                mainCodec.setPresenceHint(fieldName, false); // Consider this field as omitted to skip decoding
            } else if(fieldName.equals("securedMsg")) {
                // Nothing to do
            }
        } else if(fieldName.equals("securedMsg")) {
            mainCodec.setPresenceHint(fieldName, false);
        }
    }

    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> GeoNetworkingPacket.postDecodeField: " + fieldName + ", " + rv);
        
        if(isSecuredMode) {
            if(fieldName.equals("securedMsg")) {
                RecordValue securedMsg = (RecordValue) rv.getField("securedMsg");
                RecordValue secPayload = (RecordValue) securedMsg.getField("payload_field");
                byte[] payload = new byte[]{};
                OctetstringValue ov = (OctetstringValue) secPayload.getField("data"); // See type record SecPayload
                byte[] v = new byte[ov.getLength()];
                for (int j = 0; j < v.length; j++) {
                    v[j] = (byte) ov.getOctet(j);
                } // End of 'for' statement
                payload = ByteHelper.concat(payload, v);
                
                // Decode payload as GnNoSecured packet
                packetValue = mainCodec.decode(new CodecBuffer(payload), rv.getField("packet").getType());
            }
        }
    }

}
