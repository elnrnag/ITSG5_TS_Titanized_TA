package org.etsi.ttcn.codec.lowpan;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.EnumeratedValue;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class IphcHeader extends Record {

    public IphcHeader(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    protected void preDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {

        if(fieldName.equals("inlineSourceAddress")) {
            int sac = ((EnumeratedValue)(rv.getField("sac"))).getInt();
            int sam = ((EnumeratedValue)(rv.getField("sam"))).getInt();
            int saLen = 0;
            switch(sam) {
            case 0:
                if(sac == 0) {
                    saLen = 128;
                }
                else {
                    mainCodec.setPresenceHint(fieldName, false);
                    return;
                }
                break;
            case 1:
                saLen = 64;
                break;
            case 2:
                saLen = 16;
                break;
            case 3:
                mainCodec.setPresenceHint(fieldName, false);
                return;
            }

            mainCodec.setHint("CompressedIpv6AddressLen", Integer.toString(saLen / 8));
        }

        else if(fieldName.equals("inlineDestinationAddress")) {
            int dac = ((EnumeratedValue)(rv.getField("dac"))).getInt();
            int dam = ((EnumeratedValue)(rv.getField("dam"))).getInt();
            int m = ((EnumeratedValue)(rv.getField("m"))).getInt();
            int daLen = 0;

            if(m == 0) {
                switch(dam) {
                case 0:
                    if(dac == 0) {
                        daLen = 128;
                    }
                    else {
                        mainCodec.setPresenceHint(fieldName, false);
                        return;
                    }
                    break;
                case 1:
                    daLen = 64;
                    break;
                case 2:
                    daLen = 16;
                    break;
                case 3:
                    mainCodec.setPresenceHint(fieldName, false);
                    return;
                }
            }
            else {
                if(dac == 0) {
                    switch(dam) {
                    case 0:
                        daLen = 128;
                        break;
                    case 1:
                        daLen = 48;
                        break;
                    case 2:
                        daLen = 32;
                        break;
                    case 3:
                        daLen = 8;
                        break;
                    }
                }
                else {
                    if(dam == 0) {
                        daLen = 48;
                    }
                    else {
                        mainCodec.setPresenceHint(fieldName, false);
                        return;
                    }
                }
            }
            mainCodec.setHint("CompressedIpv6AddressLen", Integer.toString(daLen / 8));
        }
    }

    @Override
    protected void postDecodeField(String fieldName, CodecBuffer buf, Type decodingHypothesis, RecordValue rv) {

        if(fieldName.equals("trafficClass")) {
            int tc = ((EnumeratedValue)(rv.getField(fieldName))).getInt();
            if(tc == 3) {
                mainCodec.setPresenceHint("compressedTrafficClass", false);
            }
            mainCodec.setHint("trafficClass", Integer.toString(tc));
        }

        else if(fieldName.equals("nextHeader")) {
            if(((EnumeratedValue)(rv.getField(fieldName))).getInt() != 0) {
                mainCodec.setPresenceHint("inlineNextHeader", false);
                mainCodec.setHint("NhcHeaderListMore", "true");
            }	    
            else {
                mainCodec.setHint("NhcHeaderListMore", "false");
            }
        }

        else if(fieldName.equals("inlineNextHeader")) {
//            int nh = ((IntegerValue)(rv.getField(fieldName))).getInteger(); TTWB iterface is getInt, TCI shall be getInteger
            int nh = mainCodec.getTciCDRequired().getInteger((IntegerValue)(rv.getField(fieldName)));
            mainCodec.setHint("Ipv6NextHeader", Integer.toString(nh));
            if(nh == 0 || nh == 60 || nh == 43 || nh == 44) { 
                mainCodec.setHint("ExtensionHeaderListMore", "true");
            }
            else {
                mainCodec.setHint("ExtensionHeaderListMore", "false");
            }
        }

        else if(fieldName.equals("hopLimit")) {
            if(((EnumeratedValue)(rv.getField(fieldName))).getInt() != 0) {
                mainCodec.setPresenceHint("inlineHopLimit", false);
            }	    
        }

        else if(fieldName.equals("cid")) {
            if(((EnumeratedValue)(rv.getField(fieldName))).getInt() == 0) {
                mainCodec.setPresenceHint("cidExtension", false);
            }	    
        }
    }
}