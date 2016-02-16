/**
 * @author      ETSI / STF484 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/facilities/Plugin.java $
 *              $Id: Plugin.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec.its.facilities;

import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.codec.CodecFactory;

public class Plugin {

    public static void init() {

        CodecFactory cf = CodecFactory.getInstance();
        cf.setCodec(TciTypeClass.RECORD, "LibIts_Interface", "CamInd", FacilitiesInd.class);
        cf.setCodec(TciTypeClass.RECORD, "LibIts_Interface", "DenmInd", FacilitiesInd.class);
        cf.setCodec(TciTypeClass.RECORD, "LibIts_Interface", "MapInd", FacilitiesInd.class);
        cf.setCodec(TciTypeClass.RECORD, "LibIts_Interface", "SpatInd", FacilitiesInd.class);
    }
}