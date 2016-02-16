/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/geonetworking/HeaderTST.java $
 *              $Id: HeaderTST.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.its.geonetworking;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.tci.Type;

public class HeaderTST extends Union {

    public HeaderTST(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> HeaderTST.preDecode: " + decodingHypothesis);

        int hdrType = 0x00FF & buf.getBits(0, 4)[0];
        String variant = "";
        String extHeader = "";

        switch(hdrType) {
        case 0:
            variant = "anyHdr";
            extHeader = "anyHeader";
            break;
        case 1:
            variant = "beaconHdr";
            extHeader = "beaconHeader";
            break;
        case 2:
            variant = "geoUnicastHdr";
            extHeader = "geoUnicastHeader";
            break;
        case 3:
            variant = "geoAnycastHdr";
            extHeader = "geoAnycastHeader";
            break;
        case 4:
            variant = "geoBroadcastHdr";
            extHeader = "geoBroadcastHeader";
            break;
        case 5:
            variant = "tsbHdr";
            if((0x00FF & buf.getBits(4, 4)[0]) == 1) {
                extHeader = "tsbHeader";
            }
            else {
                extHeader = "shbHeader";
            }
            break;
        case 6:
            variant = "lsHdr";
            if((0x00FF & buf.getBits(4, 4)[0]) == 0) {
                extHeader = "lsRequestHeader";
            }
            else {
                extHeader = "lsReplyHeader";
            }
            break;
        case 7:
            variant = "saHdr";
            break;
        default:
            variant = "reserved";
        }

        mainCodec.setHint(decodingHypothesis.getName(), variant);
        if(extHeader.equals("")) {
            mainCodec.setPresenceHint("ExtendedHeader", false);
        }
        else {
            mainCodec.setHint("ExtendedHeader", extHeader);
        }
    }
}
