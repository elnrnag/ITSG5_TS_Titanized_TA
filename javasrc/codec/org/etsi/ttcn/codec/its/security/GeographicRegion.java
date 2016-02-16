/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/GeographicRegion.java $
 *              $Id: GeographicRegion.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;

public class GeographicRegion extends Record {
    
    final byte c_none       = 0x00;
    final byte c_circle     = 0x01;
    final byte c_rectangle  = 0x02;
    final byte c_polygon    = 0x03;
    final byte c_id         = 0x04;
    
    /**
     * Constructor
     * @param mainCodec MainCodec reference
     */
    public GeographicRegion(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    /**
     * @desc Predefined field lengths
     */
    private void setLengths() {
        mainCodec.setHint("RegionTypeLen", "8");
    }
    
    /**
     * @desc Set the variant according to the GeographicRegion type
     * @see See Draft ETSI TS 103 097 V1.1.14 Clause 5.7    TrailerFieldType
     */
    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {
//        System.out.println(">>> GeographicRegion.preDecodeField: " + fieldName + ", " + decodingHypothesis.getName() + ", " + rv.getType().getName());
        if (fieldName.equals("region_type")) {
            byte type_ = buf.getBytes(0, 1)[0];
            mainCodec.setPresenceHint("region", true);
            switch (type_) {
                case (byte)c_circle:
                    mainCodec.setHint("GeographicRegionContainer", "circular_region"); // Set variant 
                    break;
                case (byte)c_rectangle:
                    mainCodec.setHint("GeographicRegionContainer", "rectangular_region"); // Set variant 
                    break;
                case (byte)c_polygon:
                    mainCodec.setHint("GeographicRegionContainer", "polygonal_region"); // Set variant 
                    break;
                case (byte)c_id:
                    mainCodec.setHint("GeographicRegionContainer", "id_region"); // Set variant 
                    break;
                case (byte)c_none:
                    mainCodec.setPresenceHint("region", false);
                    break;
                default:
                    mainCodec.setHint("GeographicRegionContainer", "other_region"); // Set variant 
                    break;
            } // End of 'switch' statement
        }
    }
    
} // End of class GeographicRegion