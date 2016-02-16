/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/geonetworking/BasicHeader.java $
 *              $Id: BasicHeader.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.its.geonetworking;

import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

import org.etsi.ttcn.tci.EnumeratedValue;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class BasicHeader extends Record {

    public BasicHeader(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("LtBaseLen", "2"); 
        mainCodec.setHint("BasicNextHeaderLen", "4"); 
        mainCodec.setHint("HeaderTypeLen", "4"); 
    }
    
    @Override
    protected void postEncodeField(String fieldName, CodecBuffer buf) {
//        System.out.println(">>> BasicHeader.postEncodeField: " + fieldName);
        
        if(fieldName.equals("nextHeader")) {
            mainCodec.setHint("GnNextHeader", Integer.toString(buf.getBytes(buf.getNbBytes() - 1, 1)[0]));
        }
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> BasicHeader.preDecodeField: " + fieldName + ", " + decodingHypothesis);
    }

    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> BasicHeader.postDecodeField: " + fieldName + ", " + decodingHypothesis);

        if(fieldName.equals("nextHeader")) {
            int nh = ((EnumeratedValue)(rv.getField(fieldName))).getInt();
            mainCodec.setHint("GnNextHeader", Integer.toString(nh));
        }
    }
}
