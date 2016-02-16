/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/IdentifiedRegion.java $
 *              $Id: IdentifiedRegion.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;

public class IdentifiedRegion extends Record {
    
    /**
     * Constructor
     * @param mainCodec MainCodec reference
     */
    public IdentifiedRegion(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    /**
     * @desc Predefined field lengths
     */
    private void setLengths() {
        mainCodec.setHint("RegionDictionaryLen", "8");
    }
    
} // End of class IdentifiedRegion