/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/adapter/Plugin.java $
 *              $Id: Plugin.java 1432 2014-06-03 08:29:27Z garciay $
 */
package org.etsi.ttcn.codec.its.adapter;

import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.codec.CodecFactory;

public class Plugin {

    public static void init() {

        CodecFactory cf = CodecFactory.getInstance();
        cf.setCodec(TciTypeClass.UNION, "AdapterControl", "AcGnPrimitive", AcGnPrimitive.class);
        cf.setCodec(TciTypeClass.UNION, "AdapterControl", "AcGnResponse", AcGnResponse.class);
        cf.setCodec(TciTypeClass.UNION, "AdapterControl", "AcFsapPrimitive", AcFsapPrimitive.class);
    }
}