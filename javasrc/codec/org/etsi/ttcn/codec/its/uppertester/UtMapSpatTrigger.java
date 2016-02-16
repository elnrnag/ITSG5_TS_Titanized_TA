package org.etsi.ttcn.codec.its.uppertester;

import org.etsi.ttcn.codec.MainCodec;

public class UtMapSpatTrigger extends UtRecord {
    
    public UtMapSpatTrigger(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }
    
    private void setLengths() {
        mainCodec.setHint("EventLen", "8");
    }
    
} // End of class UtMapSpatTrigger
