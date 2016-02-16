/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/facilities/FacilitiesInd.java $
 *              $Id: FacilitiesInd.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec.its.facilities;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class FacilitiesInd extends Record {

    public FacilitiesInd(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> FacilitiesInd.preDecode: " + decodingHypothesis);
        
        int msgLen = buf.getNbBits() - 8 - 8 - 8 - 32 - 8 - 16 - 16;
        int offset = 0;
        
        messageBuffer = buf.getBuffer(offset, msgLen); offset += msgLen;
        gnNh = buf.getBuffer(offset, 8); offset += 8;
        gnHt = buf.getBuffer(offset, 8); offset += 8;
        gnHst = buf.getBuffer(offset, 8); offset += 8;
        gnLifetime = buf.getBuffer(offset, 32); offset += 32;
        gnTc = buf.getBuffer(offset, 8); offset += 8;
        btpDestinationPort = buf.getBuffer(offset, 16); offset += 16;
        btpInfo = buf.getBuffer(offset, 16);  offset += 16;
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> FacilitiesInd.preDecodeField: " + fieldName + " - " + decodingHypothesis);
        
        if(fieldName.equals("gnNextHeader")) {
            buf.overwriteWith(gnNh);
        }
        else if(fieldName.equals("gnHeaderType")) {
            buf.overwriteWith(gnHt);
        }
        else if(fieldName.equals("gnHeaderSubtype")) {
            buf.overwriteWith(gnHst);
        }
        else if(fieldName.equals("gnLifetime")) {
            buf.overwriteWith(gnLifetime);
        }
        else if(fieldName.equals("gnTrafficClass")) {
            buf.overwriteWith(gnTc);
        }
        else if(fieldName.equals("btpDestinationPort")) {
            buf.overwriteWith(btpDestinationPort);
        }
        else if(fieldName.equals("btpInfo")) {
            buf.overwriteWith(btpInfo);
        }        
        else {
            buf.overwriteWith(messageBuffer);
        }
    }

    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
        
    }
    
    @Override
    public CodecBuffer encode(Value value) {

        return super.encode(value);
    }

    private CodecBuffer messageBuffer = null;
    private CodecBuffer gnNh = null;
    private CodecBuffer gnHt = null;
    private CodecBuffer gnHst = null;
    private CodecBuffer gnLifetime = null;
    private CodecBuffer gnTc = null;
    private CodecBuffer btpDestinationPort = null;
    private CodecBuffer btpInfo = null;
}
