/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/uppertester/UtPduId.java $
 *              $Id: UtPduId.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec.its.uppertester;

import java.util.HashMap;
import java.util.Map;

public enum UtPduId {
    
    /* From LibItsCommon_TypesAndValues */
    UtInitialize(0x00),
    UtInitializeResult(0x01),
    UtChangePosition(0x02),
    UtChangePositionResult(0x03),
    UtChangePseudonym(0x04),
    UtChangePseudonymResult(0x05),
    UtResult(0x24), // FIXME: obsolete
    
    /* From LibItsDenm_TypesAndValues */
    UtDenmTrigger(0x10),
    UtDenmTriggerResult(0x11),
    UtDenmUpdate(0x12),
    UtDenmUpdateResult(0x13),
    UtDenmTermination(0x14),
    UtDenmTerminationResult(0x15),
    //reserved(0x16),
    UtDenmEventInd(0x17),
    
    /* From LibItsCam_TypesAndValues */
    //reserved(0x20),
    UtCamTriggerResult(0x21),
    //reserved(0x22),
    UtCamEventInd(0x23),
    UtCamTrigger_changeCurvature(0x30),
    UtCamTrigger_changeSpeed(0x31),
    UtCamTrigger_setAccelerationControlStatus(0x32),
    UtCamTrigger_setExteriorLightsStatus(0x33),
    UtCamTrigger_changeHeading(0x34),
    UtCamTrigger_setDriveDirection(0x35),
    UtCamTrigger_changeYawRate(0x36),
    UtCamTrigger_setStationType(0x39),
    UtCamTrigger_setVehicleRole(0x3a),
    UtCamTrigger_setEmbarkationStatus(0x3b),
    UtCamTrigger_setPtActivation(0x3c),
    UtCamTrigger_setDangerousGoods(0x3d),
    UtCamTrigger_setDangerousGoodsExt(0x3e),
    UtCamTrigger_setLightBarSireneStatus(0x3f),
    
    /* From LibItsGeoNetworking_TypesAndValues */
    //reserved(0x40),
    UtGnTriggerResult(0x41),
    //reserved(0x42),
    UtGnTrigger_geoUnicast(0x50),
    UtGnTrigger_geoBroadcast(0x51),
    UtGnTrigger_geoAnycast(0x52),
    UtGnTrigger_shb(0x53),
    UtGnTrigger_tsb(0x54),
    UtGnEventInd(0x55),
    
    /* From LibItsBtp_TypesAndValues */
    //reserved(0x60),
    UtBtpTriggerResult(0x61),
    //reserved(0x62),
    UtBtpEventInd(0x63),
    UtBtpTrigger_btpA(0x70),
    UtBtpTrigger_btpB(0x71),
    
    /* From LibItsIpv6OverGeoNetworking_TypesAndValues */
    UtGn6Trigger(0x80),
    UtGn6TriggerResult(0x81),
    //reserved(0x82),
    UtGn6EventInd(0x83),
    UtGn6GetInterfaceInfo(0x84),
    UtGn6GetInterfaceInfoResult(0x85),
    
    /* */
    //reserved(0x90),
    UtSecResult(0x91),
    //reserved(0x92),
    UtSec_setCertificate(0x93),
    UtSec_setPrivateKey(0x94),
    UtSec_setTrustPoint(0x95),

    /* From LibItsMapSpat_TypesAndValues */
    UtMapSpatTrigger(0xA0),
    UtMapSpatTriggerResult(0xA1),
    UtMapEventInd(0xA2),
    UtSpatEventInd(0xA3),
    
    /* Reserved */
    reserved(0xFF);
    
    private byte value;
    private static final Map<String, Byte> UtPduIds = new HashMap<String, Byte>();
    private static final Map<Byte, String> UtPduNames = new HashMap<Byte, String>();
    
    private UtPduId(int value) {
        this.value = (byte)value;
    }
    
    public static Byte value(String name) {
        return UtPduIds.get(name);
    }
    
    public static String name(Byte value) {
        return UtPduNames.get(value);
    }
    
    static {
        for (UtPduId item : UtPduId.values()) {
            UtPduIds.put(item.name(), new Byte(item.value));
            UtPduNames.put(new Byte(item.value), item.name());
        }
    }
}