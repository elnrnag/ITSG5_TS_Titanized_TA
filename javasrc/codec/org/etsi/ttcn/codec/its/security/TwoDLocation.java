/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/TwoDLocation.java $
 *              $Id: TwoDLocation.java 1665 2014-10-03 08:27:31Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Record;

public class TwoDLocation extends Record {

    public TwoDLocation(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("WGSLatitudeLen", "32"); 
        mainCodec.setHint("WGSLongitudeLen", "32"); 
    }
    
} // End of class TwoDLocation