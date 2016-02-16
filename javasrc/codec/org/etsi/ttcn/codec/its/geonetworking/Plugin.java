/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/geonetworking/Plugin.java $
 *              $Id: Plugin.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.its.geonetworking;

import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.codec.CodecFactory;

public class Plugin {

    public static void init() {

        CodecFactory cf = CodecFactory.getInstance();
        cf.setCodec(TciTypeClass.RECORD, "LibItsGeoNetworking", "BasicHeader", BasicHeader.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsGeoNetworking", "TrafficClass", TrafficClass.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsGeoNetworking", "LongPosVector", LongPosVector.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsGeoNetworking", "GN_Address", GN_Address.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsGeoNetworking", "CommonHeader", CommonHeader.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsGeoNetworking", "GeoNetworkingPacket", GeoNetworkingPacket.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsGeoNetworking", "GnNonSecuredPacket", GnNonSecuredPacket.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsGeoNetworking", "HeaderTST", HeaderTST.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsGeoNetworking", "Payload", Payload.class);
        cf.setCodec(TciTypeClass.RECORD, "LibIts_Interface", "GeoNetworkingInd", GnIndReq.class);
        cf.setCodec(TciTypeClass.RECORD, "LibIts_Interface", "GeoNetworkingReq", GnIndReq.class);
    }
}