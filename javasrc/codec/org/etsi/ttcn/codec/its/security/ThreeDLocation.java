/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/ThreeDLocation.java $
 *              $Id: ThreeDLocation.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class ThreeDLocation extends Record {

    public ThreeDLocation(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("WGSLatitudeLen", "32"); 
        mainCodec.setHint("WGSLongitudeLen", "32"); 
    }
    
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> ThreeDLocation.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        if (fieldName.equals("elevation")) {
            mainCodec.setHint("Oct2Len", "2");
        }
    }
    
} // End of class ThreeDLocation