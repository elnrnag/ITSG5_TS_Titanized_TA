/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/adapter/AcFsapPrimitive.java $
 *              $Id: AcFsapPrimitive.java 1835 2014-11-20 09:47:04Z berge $
 */
package org.etsi.ttcn.codec.its.adapter;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.tci.UnionValue;

public class AcFsapPrimitive extends Union {

    public AcFsapPrimitive(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preEncode(CodecBuffer buf, UnionValue uv) {
        String variant = uv.getPresentVariantName();
        int primitiveId = -1;

        // Append AcGnPrimitive message id
        buf.appendBytes(ByteHelper.intToByteArray(2, 1)); // AdapterControl Primitive identifer for AcFsapPrimitive

        // Append primitive command identifier
        if(variant.equals("inSapPrimitivesUp")) {
            primitiveId = 0;
        }
        else if(variant.equals("stopTransmission")) {
            primitiveId = 1;
        }
        buf.appendBytes(ByteHelper.intToByteArray(primitiveId, 1));
    }
}
