/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/Time64.java $
 *              $Id: Time64.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Integer;

public class Time64 extends Integer {
    
    public Time64(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("Time64Len", "64"); 
        mainCodec.setHint("HeaderFieldContainer.generation_timeLen", "64"); 
    }
    
} // End of class Time64