/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/btp/BtpHeader.java $
 *              $Id: BtpHeader.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec.its.btp;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.UnionValue;

public class BtpHeader extends Union {

    private static final String BTP_A_HEADER = "btpAHeader";
    private static final String BTP_B_HEADER = "btpBHeader";

    public BtpHeader(MainCodec mainCodec) {
        super(mainCodec);
        setLengths();
    }

    private void setLengths() {
        mainCodec.setHint("BtpPortIdLen", "16"); 
        mainCodec.setHint("BtpPortInfo", "16"); 
    }
    

    @Override
    protected void preEncode(CodecBuffer buf, UnionValue uv) {
        //FIXME to support BTP standalone we need to know for GN later if it is BTP A or BTP B
        boolean isIncludedInGn = false;
        StackTraceElement[] stackTrace =      new Exception().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            if (className.contains("GeoNetworkingPacket") || className.contains("GnNonSecuredPacket") || methodName.contains("encvalue")) {
                isIncludedInGn = true;
                break;
            }
        }
        if (!isIncludedInGn) {
            String variant = uv.getPresentVariantName();
            if (variant.equals(BTP_A_HEADER)) {
                buf.appendBytes(new byte[] {0x1});
            }
            else if (variant.equals(BTP_B_HEADER)) {
                buf.appendBytes(new byte[] {0x2});
            }
            else {
                buf.appendBytes(new byte[] {0x0});
            }
        }
        super.preEncode(buf, uv);
    }
    
    @Override
    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {

        String nhHint = mainCodec.getHint("GnNextHeader");
        String variant = BTP_B_HEADER;

        int nh;
        if(nhHint != null) {
            nh = Integer.parseInt(nhHint);
        }
        else {
            byte[] result = buf.readBytes(1);
            nh = result[0];
        }
        switch (nh) {
            case 1:
                variant = BTP_A_HEADER;
                break;
            case 2:
                variant = BTP_B_HEADER;
                break;
            default:
                variant = BTP_B_HEADER;
        }
        mainCodec.setHint(decodingHypothesis.getName(), variant);
    }
}
