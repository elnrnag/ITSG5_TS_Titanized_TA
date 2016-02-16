/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $$URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/v2g/Sdp_Payload.java $$
 *              $$Id: Sdp_Payload.java 1139 2013-08-09 14:20:11Z berge $$
 */
package org.etsi.ttcn.codec.its.v2g;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.tci.Type;

public class Sdp_Payload extends Union {

    public Sdp_Payload(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
        
        String variant = "";
        switch(buf.getNbBytes()) {
        case 2:
            variant = "sdpRequest";
            break;
        case 20:
            variant = "sdpResponse";
            break;
        default:
            System.err.println("Unable to decode " + decodingHypothesis.getName());
            return;
        }
        mainCodec.setHint(decodingHypothesis.getName(), variant);
    }    
}
