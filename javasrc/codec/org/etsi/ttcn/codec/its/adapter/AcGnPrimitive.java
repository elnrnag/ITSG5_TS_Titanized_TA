/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/adapter/AcGnPrimitive.java $
 *              $Id: AcGnPrimitive.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.its.adapter;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.UnionValue;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.common.ByteHelper;

public class AcGnPrimitive extends Union {

    public AcGnPrimitive(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {

        // Read message id (AcGnPrimitive)
        if(0x00 != (0x00FF & buf.readBytes(1)[0])) {
            return;
        }

        // Read primitive id
        int primitiveId = 0x00FF & buf.readBytes(1)[0];
        String primitive = "";

        switch(primitiveId) {
        case 0:
            primitive = "startBeaconing";
            break;
        case 1:
            primitive = "stopBeaconing";
            break;
        case 2:
            primitive = "startPassBeaconing";
            break;    
        case 3:
            primitive = "stopPassBeaconing";
            break;
        case 4:
            primitive = "startBeaconingMultipleNeighbour";
            break;
        case 5:
            primitive = "stopBeaconingMultipleNeighbour";
            break;  
        case 6:
            primitive = "getLongPosVector";
            break;  
        case 7:
            primitive = "enableSecurity";
            break;  
        case 8:
            primitive = "disableSecurity";
            break;  
        }

        mainCodec.setHint(decodingHypothesis.getName(), primitive);
    }

    @Override
    protected void preEncode(CodecBuffer buf, UnionValue uv) {
        String variant = uv.getPresentVariantName();
        int primitiveId = -1;

        // Append AcGnPrimitive message id
        buf.appendBytes(ByteHelper.intToByteArray(0, 1));

        // Append primitive id
        if(variant.equals("startBeaconing")) {
            primitiveId = 0;
        }
        else if(variant.equals("stopBeaconing")) {
            primitiveId = 1;
        }
        else if(variant.equals("startPassBeaconing")) {
            primitiveId = 2;
        }
        else if(variant.equals("stopPassBeaconing")) {
            primitiveId = 3;
        }
        else if(variant.equals("startBeaconingMultipleNeighbour")) {
            primitiveId = 4;
        }
        else if(variant.equals("stopBeaconingMultipleNeighbour")) {
            primitiveId = 5;
        }
        else if(variant.equals("getLongPosVector")) {
            primitiveId = 6;
        }
        else if(variant.equals("acEnableSecurity")) {
            primitiveId = 7;
        }
        else if(variant.equals("acDisableSecurity")) {
            primitiveId = 8;
        }
        buf.appendBytes(ByteHelper.intToByteArray(primitiveId, 1));
    }
}
