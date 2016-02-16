/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/EccPoint.java $
 *              $Id: EccPoint.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class EccPoint extends Record {
    
    final byte c_x_coordinate_only  = 0x00; /** Only the x coordinate is encoded */
    final byte c_compressed_lsb_y_0 = 0x02; /** The point is compressed and y's least significant bit is zero */
    final byte c_compressed_lsb_y_1 = 0x03; /** The point is compressed and y's least significant bit is one */
    final byte c_uncompressed       = 0x04; /** The y coordinate is encoded in the field y */
    
    public EccPoint(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("EccPointTypeLen", "8");
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> EccPoint.postEncodeField: " + fieldName);
        
        if (fieldName.equals("type_")) {
            // Store EccPointType value
            mainCodec.setHint("EccPointType", Integer.toString(ByteHelper.byteArrayToInt(buf.getBytes(0, buf.getNbBytes()))));
        } else if (fieldName.equals("x") || fieldName.equals("y")) {
            // TODO Store first the octetstring length as specified in Draft ETSI TS 103 097 V1.1.14 Clause 4.2
            int type = Integer.parseInt(mainCodec.getHint("EccPointType"));
            // TODO Check when octetsting length should be added
//            if (type != c_uncompressed) { // Do not add length because of it is fixed to 32 
//                CodecBuffer bufLen = new CodecBuffer(TlsHelper.getInstance().size2tls(buf.getNbBytes()));
//                bufLen.append(buf);
//                buf.overwriteWith(bufLen);
//            }
        }
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> EccPoint.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        
        if (fieldName.equals("type_")) {
            byte type = buf.getBytes(0, 1)[0];
            // Store EccPointType value
            mainCodec.setHint("EccPointType", Integer.toString(type));
            switch (type) {
                case c_x_coordinate_only:
                    mainCodec.setPresenceHint("y", false);
                    break;
                case c_compressed_lsb_y_0:
                case c_compressed_lsb_y_1:
                    mainCodec.setPresenceHint("y", false);
                    break;
                case c_uncompressed:
                    mainCodec.setPresenceHint("y", true);
                    mainCodec.setHint("EccPointContainer", "y"); // Set variant 
                    break;
            } // End of 'switch' statement
        } else if (fieldName.equals("x")) {
            int type = Integer.parseInt(mainCodec.getHint("EccPointType"));
            int len = 32;
            // TODO Check when octetsting length should be added
//            if (type != c_uncompressed) {
//                len = (int) TlsHelper.getInstance().tls2size(buf);
//            }
            mainCodec.setHint("octetstringLen", Integer.toString(len));
        } else if ((fieldName.equals("y") && mainCodec.getPresenceHint("y") == true)) {
            int type = Integer.parseInt(mainCodec.getHint("EccPointType"));
            int len = 32;
            // TODO Check when octetsting length should be added
//            if (type != c_uncompressed) {
//                len = (int) TlsHelper.getInstance().tls2size(buf);
//            }
            mainCodec.setHint("octetstringLen", Integer.toString(len));
            mainCodec.setHint("EccPointContainer.yLen", Integer.toString(len)); // TCT3 Decoding EciesEncryptedKey, type became EccPointContainer.y 
        }
    }
    
    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> EccPoint.postDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
    }
    
} // End of class EccPoint
