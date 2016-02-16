/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $$URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/v2g/ExiUnion.java $$
 *              $$Id: ExiUnion.java 1139 2013-08-09 14:20:11Z berge $$
 */
package org.etsi.ttcn.codec.its.v2g;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.IMarkerCallback;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.UnionValue;
import org.etsi.ttcn.tci.Value;

public class ExiUnion extends Union implements IMarkerCallback {

    private static final byte V2G_IND = 0x01;
    private static final byte APP_PROTO_IND = 0x02;
    private static final byte SDP_IND = 0x03;
    
    public ExiUnion(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public void preDecode(CodecBuffer buf, Type decodingHypothesis) {
        String variant = "";
        boolean exiProcessing = false;
        byte reqIndId = buf.readBytes(1)[0];
        
        switch(reqIndId) {
        case V2G_IND:
            variant = "v2gIn";
            exiProcessing = true;
            break;
        case APP_PROTO_IND:
            variant = "appProtoIn";
            exiProcessing = true;
            break;
        case SDP_IND:
            variant = "sdpIn";
            break;
        }
        mainCodec.setHint(decodingHypothesis.getName(), variant);
        
        if(exiProcessing) {
            byte[] exi = buf.getBytes();
            byte[] xml = ExiHelper.decode(exi);
            buf.setBytes(xml);
        }
        else {
            super.preDecode(buf, decodingHypothesis);
        }       
    }

    @Override
    public CodecBuffer preEncode(Value value) {
        UnionValue uv = (UnionValue)value;
        CodecBuffer buf = new CodecBuffer();
        byte reqIndId = 0x00;
        
        String variantTypeName = uv.getVariant(uv.getPresentVariantName()).getType().getName();        
        if(variantTypeName.equals("V2G_Message")) {
            reqIndId = V2G_IND;
        }
        else if(variantTypeName.equals("SupportedAppProtocol_Message")) {
            reqIndId = APP_PROTO_IND;            
        }
        else if(variantTypeName.equals("SDP_Message")) {
            reqIndId = SDP_IND;            
        }
        buf.setBytes(new byte[] {reqIndId});
        
        if(reqIndId == V2G_IND || reqIndId == APP_PROTO_IND) {            
            buf.setMarker("exiEncode", 0, this);
            return buf;
        }
        else {
            return super.preEncode(value);
        }
    }

    @Override
    public void run(String markerName, CodecBuffer leftBuf, CodecBuffer rightBuf) {

        if(markerName.equals("exiEncode")) {
            byte[] xml = rightBuf.getBytes();
            byte[] exi = ExiHelper.encode(xml);
            rightBuf.setBytes(exi);
        }
    }    
    
}
