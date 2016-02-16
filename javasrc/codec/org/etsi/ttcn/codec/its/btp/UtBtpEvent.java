package org.etsi.ttcn.codec.its.btp;

import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Union;

public class UtBtpEvent extends Union {
    
    public UtBtpEvent(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("BtpPortIdLen", "16"); 
        mainCodec.setHint("BtpPortInfoLen", "16"); 
    }
    
}
