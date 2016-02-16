/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: https://forge.etsi.org/svn/ITS/branches/STF484_VALIDATION/javasrc/codec/org/etsi/ttcn/codec/its/geonetworking/Plugin.java $
 *              $Id: Plugin.java 1834 2014-11-20 09:19:30Z berge $
 */
package org.etsi.ttcn.codec.its.mapspat;

import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.codec.CodecFactory;

public class Plugin {

    public static void init() {

        CodecFactory cf = CodecFactory.getInstance();
        cf.setCodec(TciTypeClass.RECORD, "LibIts_Interface", "MapReq", MapSpatIndReq.class);
        cf.setCodec(TciTypeClass.RECORD, "LibIts_Interface", "SpatReq", MapSpatIndReq.class);
    }
}