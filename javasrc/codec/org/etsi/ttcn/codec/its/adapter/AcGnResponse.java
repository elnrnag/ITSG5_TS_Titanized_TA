/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/adapter/AcGnResponse.java $
 *              $Id: AcGnResponse.java 1143 2013-08-12 13:48:10Z berge $
 */
package org.etsi.ttcn.codec.its.adapter;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.UnionValue;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.common.ByteHelper;

public class AcGnResponse extends Union {

    public AcGnResponse(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {

        // Read message id (AcGnResponse)
        if(0x00 != (0x00FF & buf.readBytes(1)[0])) {
            System.out.println("Bad hypothesis");
            return;
        }

        // Read response id
        int responseId = 0x00FF & buf.readBytes(1)[0];
        String variant = "";

        switch(responseId) {
        case 0:
            variant = "failure";
            break;
        case 6:
            variant = "getLongPosVector";
            break;  
        }

        mainCodec.setHint(decodingHypothesis.getName(), variant);
    }

    @Override
    protected void preEncode(CodecBuffer buf, UnionValue uv) {
        String variant = uv.getPresentVariantName();
        int responseId = -1;

        // Append AcGnResponse message id
        buf.appendBytes(ByteHelper.intToByteArray(0, 1));

        // Append response id
        if(variant.equals("failure")) {
            responseId = 0;
        }
        else if(variant.equals("getLongPosVector")) {
            responseId = 6;
        }
        buf.appendBytes(ByteHelper.intToByteArray(responseId, 1));
    }
}
