/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/uppertester/Plugin.java $
 *              $Id: Plugin.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec.its.uppertester;

import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.codec.CodecFactory;
import org.etsi.ttcn.codec.generic.Record;

public class Plugin {

    public static void init() {

        CodecFactory cf = CodecFactory.getInstance();
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "", UtRecord.class);
        cf.setCodec(TciTypeClass.UNION, "UpperTester", "", UtUnion.class);
        cf.setCodec(TciTypeClass.BOOLEAN, "UpperTester", "", UtBoolean.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "SituationContainer", Record.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtDenmTrigger", UtDenmTrigger.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtDenmUpdate", UtDenmUpdate.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "Payload", UtPayload.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtCamEventInd", UtEventInd.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtDenmEventInd", UtEventInd.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtGnEventInd", UtEventInd.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtMapEventInd", UtEventInd.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtSpatEventInd", UtEventInd.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtDenmTermination", UtDenmTermination.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtDenmTriggerResult", UtDenmTermination.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtDenmUpdateResult", UtDenmTermination.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtChangePosition", UtChangePosition.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtMapSpatTrigger", UtMapSpatTrigger.class);
        cf.setCodec(TciTypeClass.RECORD, "UpperTester", "UtMapSpatTriggerResult", UtMapSpatTriggerResult.class);
        cf.setCodec(TciTypeClass.UNION, "UpperTester", "UtCamTrigger", UtCamTrigger.class);
    }
}