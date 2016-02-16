/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/ItsAidSsp.java $
 *              $Id: ItsAidSsp.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class ItsAidSsp extends Record {
    
    public ItsAidSsp(MainCodec mainCodec) {
        super(mainCodec);
//        setLengths();
    }

//    private void setLengths() {
//        mainCodec.setHint("Bit2Len", "2"); 
//        mainCodec.setHint("Bit3Len", "3"); 
//    }
//    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> ItsAidSsp.postEncodeField: " + fieldName);
        
        if (fieldName.equals("service_specific_permissions")) {
            CodecBuffer bufLen = new CodecBuffer(new byte[] { (byte)buf.getNbBytes() } );
//            System.out.println("ItsAidSsp.postEncodeField: bufLen = " + bufLen);
            bufLen.append(buf);
            buf.overwriteWith(bufLen);
        }
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> ItsAidSsp.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("service_specific_permissions")) {
            int len = buf.readBits(Byte.SIZE)[0]; // FIXME It depends of the len value : <= 127 or > 127
            // FIXME Check for opaque<var> length encoding
            //int len = ByteHelper.byteArrayToInt(buf.readBits(Integer.SIZE)); // field_sizeLen is 1 bytes
//            System.out.println("ItsAidSsp.preDecodeField: len = " + len);
            mainCodec.setHint("Oct1to31Len", Integer.toString(len));
        }
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> ItsAidSsp.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("its_aid")) {
            //int itsaid = ((IntegerValue)(rv.getField(fieldName))).getInt();
            int itsaid = mainCodec.getTciCDRequired().getInteger(((IntegerValue)(rv.getField(fieldName))));
            if (itsaid == 36) {
                mainCodec.setHint("ServiceSpecificPermissionsContainer", "sspCAM");
            } else if (itsaid == 37) {
                mainCodec.setHint("ServiceSpecificPermissionsContainer", "sspDENM");
            } else {
                mainCodec.setHint("ServiceSpecificPermissionsContainer", "opaque");
            }
        }
    }
    
} // End of class ItsAidSsp
