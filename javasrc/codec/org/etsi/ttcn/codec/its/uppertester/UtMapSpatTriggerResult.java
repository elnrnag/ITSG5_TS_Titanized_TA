package org.etsi.ttcn.codec.its.uppertester;

import org.etsi.ttcn.codec.MainCodec;

public class UtMapSpatTriggerResult extends UtRecord {
    
    public UtMapSpatTriggerResult(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }
    
    private void setLengths() {
        mainCodec.setHint("MsgCountLen", "8");
    }
    
} // End of class UtMapSpatTriggerResult
