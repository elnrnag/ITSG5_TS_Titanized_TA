package org.etsi.ttcn.codec.its.uppertester;

import org.etsi.common.ByteHelper;
import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class UtEventInd extends UtRecord {
	
	public UtEventInd(MainCodec mainCodec) {
		super(mainCodec);
	}
	
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
        int length = ByteHelper.byteArrayToInt(buf.readBytes(Short.SIZE / Byte.SIZE));
        // TODO Check payload length
    }
    
}
