/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/Time64WithStandardDeviation.java $
 *              $Id: Time64WithStandardDeviation.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;

public class Time64WithStandardDeviation extends Record {
	
    public Time64WithStandardDeviation(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }
    
    /**
     * @desc Predefined field lengths
     */
    private void setLengths() {
        mainCodec.setHint("Time64Len", "64");
    }
    
} // End of Time64WithStandardDeviation
