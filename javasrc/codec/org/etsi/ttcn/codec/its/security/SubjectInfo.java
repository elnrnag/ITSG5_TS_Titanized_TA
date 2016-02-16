/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/SubjectInfo.java $
 *              $Id: SubjectInfo.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class SubjectInfo extends Record {
    
    public SubjectInfo(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("SubjectTypeLen", "8"); 
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> SubjectInfo.postEncodeField: " + fieldName);
        
        if (fieldName.equals("subject_name")) {
            CodecBuffer bufLen = new CodecBuffer(new byte[] { (byte)buf.getNbBytes() } );
            bufLen.append(buf);
            buf.overwriteWith(bufLen);
        }
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> SubjectInfo.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("subject_name")) {
            int len = buf.readBits(Byte.SIZE)[0]; // FIXME It depends of the len value : <= 127 or > 127
            // FIXME Check for opaque<var> length encoding
            //int len = ByteHelper.byteArrayToInt(buf.readBits(Integer.SIZE)); // field_sizeLen is 1 bytes
//            System.out.println("SubjectInfo.preDecodeField: len = " + len);
            mainCodec.setHint("Oct0to31Len", Integer.toString(len));
        }
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> SubjectInfo.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
    }
    
} // End of class SubjectInfo