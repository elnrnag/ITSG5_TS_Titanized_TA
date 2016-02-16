/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: $
 *              $Id: $
 */
package org.etsi.ttcn.codec.its.uppertester;

import org.etsi.ttcn.codec.MainCodec;

public class UtDenmTermination extends UtRecord {

    public UtDenmTermination(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }
    
    private void setLengths() {
        mainCodec.setHint("StationIDLen", "32");
        mainCodec.setHint("SequenceNumberLen", "16");
    }
    
}
