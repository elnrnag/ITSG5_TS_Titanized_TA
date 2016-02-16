/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/Time32.java $
 *              $Id: Time32.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Integer;

public class Time32 extends Integer {
    
    public Time32(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("Time32Len", "32"); 
        mainCodec.setHint("HeaderFieldContainer.expiry_timeLen", "32");
        mainCodec.setHint("ValidityRestrictionContainer.end_validityLen", "32");
    }
    
} // End of class Time32