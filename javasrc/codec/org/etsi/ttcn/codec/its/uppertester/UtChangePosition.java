/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: $
 *              $Id: $
 */
package org.etsi.ttcn.codec.its.uppertester;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class UtChangePosition extends UtRecord {

    public UtChangePosition(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
        setLengths();
        return super.decode(buf, decodingHypothesis);
    }

    @Override
    public CodecBuffer preEncode(Value value) {
        setLengths();
        return super.preEncode(value);
    } 
    
    private void setLengths() {
        mainCodec.setHint("integerLen", "32");
    }

}
