/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/geonetworking/Payload.java $
 *              $Id: Payload.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.its.geonetworking;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class Payload extends Record {

    public Payload(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> Payload.preDecode" + decodingHypothesis);

        if(fieldName.equals("decodedPayload")) {
            String nhHint = mainCodec.getHint("GnNextHeader");
            String variant = "";
            
            mainCodec.setHint(fieldName + "IgnoreErrorOnOptionalField", "true");

            if(nhHint != null) {
                int nh = Integer.parseInt(nhHint);
                switch(nh) {
                case 1:
                case 2:
                    variant = "btpPacket";
                    break;
                case 3:
                    variant = "ipv6Packet";
                    break;
                default:
                    mainCodec.setPresenceHint("decodedPayload", false);
                    return;
                }
            }
            mainCodec.setHint(decodingHypothesis.getName(), variant);
        }

        else if(fieldName.equals("rawPayload")) {
            buf.overwriteWith(rawBuffer); 
            mainCodec.setHint("GnRawPayloadLen", Integer.toString(buf.getNbBytes()));
        }
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> Payload.preDecode: " + decodingHypothesis);
        
        // Sanity check on pl
        int pl = java.lang.Integer.parseInt(mainCodec.getHint("payloadLength"));
        int remainingBytesForPayload = buf.getNbBytes();

        if(pl != remainingBytesForPayload) {
            System.err.println("Error: bad payload length or message truncated "
                    + "(indicated: " + pl + " - real: " + remainingBytesForPayload + ")");
            pl = Math.min(remainingBytesForPayload, pl);
        }

        // Save complete buf for 'rawPayload'        
        rawBuffer = buf.getBuffer(0, (remainingBytesForPayload) * 8);      
    }

    private CodecBuffer rawBuffer = null;
}
