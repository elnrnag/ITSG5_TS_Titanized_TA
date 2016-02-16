/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/geonetworking/CommonHeader.java $
 *              $Id: CommonHeader.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.its.geonetworking;

import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.IMarkerCallback;

import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.tci.EnumeratedValue;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class CommonHeader extends Record implements IMarkerCallback {

    public CommonHeader(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("HeaderTypeLen", "4"); 
        mainCodec.setHint("NextHeaderLen", "4"); 
        mainCodec.setHint("Bit8Len", "8"); 
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> CommonHeader.postEncodeField: " + fieldName);
        
        if(fieldName.equals("nextHeader")) {
            int nh = buf.getBits(buf.getNbBits() - 4, 4)[0];
            mainCodec.setHint("NextHeader", Integer.toString(nh));
        } else if(fieldName.equals("plLength")) {
            buf.setMarker("plLength", 0, this);
        }
    }

    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> CommonHeader.preDecodeField: " + fieldName + ", " + decodingHypothesis);
    }

    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> CommonHeader.postDecodeField: " + fieldName + ", " + decodingHypothesis);
        
        if(fieldName.equals("nextHeader")) {
            int nh = ((EnumeratedValue)(rv.getField(fieldName))).getInt();
            mainCodec.setHint("GnNextHeader", Integer.toString(nh));
        }

        else if(fieldName.equals("plLength")) {
//            int pl = ((IntegerValue)(rv.getField(fieldName))).getInteger(); TTWB iterface is getInt, TCI shall be getInteger
            int pl = mainCodec.getTciCDRequired().getInteger((IntegerValue)(rv.getField(fieldName)));
            mainCodec.setHint("payloadLength", Integer.toString(pl));
        }
    }

    @Override
    public void run(String markerName, CodecBuffer leftBuf, CodecBuffer rightBuf) {
//        System.out.println(">>> CommonHeader.run");
        
        if(markerName.equals("plLength")) {
            int pl = java.lang.Integer.parseInt(mainCodec.getHint("payloadLength"));
            CodecBuffer preserveRightBuf = rightBuf.getBuffer(16, rightBuf.getNbBits() - 16);
            rightBuf.setBytes(ByteHelper.intToByteArray(pl, 2));
            rightBuf.append(preserveRightBuf);
        }
    }
}
