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

public class UtCamTrigger extends UtUnion {

    public UtCamTrigger(MainCodec mainCodec) {
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
        mainCodec.setHint("CurvatureValueLen", "16");
        mainCodec.setHint("SpeedValueLen", "16");
        mainCodec.setHint("AccelerationControlLen", "8");
        mainCodec.setHint("ExteriorLightsLen", "8");
        mainCodec.setHint("HeadingValueLen", "16");
        mainCodec.setHint("DriveDirectionLen", "8");
        mainCodec.setHint("YawRateValueLen", "16");
        mainCodec.setHint("VehicleRoleLen", "8");
    }

}
