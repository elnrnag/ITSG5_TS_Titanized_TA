/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/btp/Plugin.java $
 *              $Id: Plugin.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.its.btp;

import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.codec.CodecFactory;

public class Plugin {

    public static void init() {

        CodecFactory cf = CodecFactory.getInstance();
        cf.setCodec(TciTypeClass.RECORD, "LibItsBtp", "BtpPacket", BtpPacket.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsBtp", "BtpPayload", Payload.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsBtp", "BtpHeader", BtpHeader.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsBtp", "DecodedBtpPayload", DecodedBtpPayload.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsBtp", "UtBtpEvent", UtBtpEvent.class);
    }
}