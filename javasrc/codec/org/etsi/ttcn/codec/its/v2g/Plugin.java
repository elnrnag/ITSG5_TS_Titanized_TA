/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/v2g/Plugin.java $
 *              $Id: Plugin.java 1143 2013-08-12 13:48:10Z berge $
 */
package org.etsi.ttcn.codec.its.v2g;

import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.codec.CodecFactory;

public class Plugin {

    public static void init() {

        CodecFactory cf = CodecFactory.getInstance();
        cf.setCodec(TciTypeClass.UNION, "LibItsV2G", "Sdp_Payload", Sdp_Payload.class);
        cf.setCodec(TciTypeClass.UNION, "LibIts_Interface", "V2Gind", ExiUnion.class);
        cf.setCodec(TciTypeClass.UNION, "LibIts_Interface", "V2Greq", ExiUnion.class);
    }
}