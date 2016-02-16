/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/RecordOf.java $
 *              $Id: RecordOf.java 1917 2015-01-12 13:08:04Z berge $
 */
package org.etsi.ttcn.codec.its.security;

import java.util.ArrayList;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.tci.RecordOfValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class RecordOf extends org.etsi.ttcn.codec.generic.RecordOf {
    
    public RecordOf(MainCodec mainCodec) {
        super(mainCodec);
    }
    
    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> security.RecordOf.decode: " + decodingHypothesis.getName());
        
        int len = (int) TlsHelper.getInstance().tls2size(buf);// buf.readBytes(1)[0]; // Get number of items into the RecordOf
//        System.out.println("security.RecordOf.decode: length=" + len);
        if (len == 0) {
            RecordOfValue rov = (RecordOfValue)decodingHypothesis.newInstance();
            rov.setLength(0);
            return rov;
        }
        CodecBuffer newBuf = new CodecBuffer(buf.readBits(len * Byte.SIZE));
        
        ArrayList<Value> recordOf = new ArrayList<Value>();
        RecordOfValue rov = (RecordOfValue)decodingHypothesis.newInstance();
        while (newBuf.getNbBits() != 0) {
            recordOf.add(mainCodec.decode(newBuf, rov.getElementType()));
        }
        
        // Fill it
        rov.setLength(recordOf.size());
        for (int position = 0; position < recordOf.size(); position++) {
            rov.setField(position, recordOf.get(position));
        } // End of 'for' statement
        
        return rov;
    }
    

    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println(">>> security.RecordOf.encode: " + value.getType().getName());
        
        CodecBuffer buf = super.encode(value);
        CodecBuffer bufLen = new CodecBuffer(TlsHelper.getInstance().size2tls(buf.getNbBytes()));
        bufLen.append(buf);
        return bufLen;
    }
    
} // End of class RecordOf
