/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: $
 *              $Id: $
 */
package org.etsi.ttcn.codec.its.uppertester;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.IMarkerCallback;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.tci.Value;

public class UtPayload extends Record implements IMarkerCallback {

    public UtPayload(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public CodecBuffer preEncode(Value value) {
        
        CodecBuffer buf = new CodecBuffer(new byte[] {(byte)0x00, (byte)0x00});
        buf.setMarker("payloadLength", 0, this);
        
        return buf;
    }

    @Override
    public void run(String markerName, CodecBuffer leftBuf, CodecBuffer rightBuf) {
        
        if(markerName.equals("payloadLength")) {
            int length = (rightBuf.getNbBits() - 16) / 8;
            CodecBuffer preserveRightBuf = rightBuf.getBuffer(16, rightBuf.getNbBits() - 16);
            rightBuf.setBytes(ByteHelper.intToByteArray(length, 2));
            rightBuf.append(preserveRightBuf);
        }
    }    
}
