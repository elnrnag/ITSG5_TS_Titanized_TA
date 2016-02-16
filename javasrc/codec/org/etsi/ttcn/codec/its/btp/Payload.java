/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/btp/Payload.java $
 *              $Id: Payload.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.its.btp;

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

        if(fieldName.equals("decodedPayload")) {
            mainCodec.setHint(fieldName + "IgnoreErrorOnOptionalField", "true");            
        }
        else if(fieldName.equals("rawPayload")) {
            int pl = buf.getNbBytes();
            if (pl != 0) {
                buf.overwriteWith(rawBuffer); 
            } else {
                mainCodec.setPresenceHint(fieldName, false);
            }
        }
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {

        int pl = buf.getNbBytes();
        
        // Save complete buf for 'rawPayload'        
        rawBuffer = buf.getBuffer(0, pl * 8);        
    }

    private CodecBuffer rawBuffer = null;
}
