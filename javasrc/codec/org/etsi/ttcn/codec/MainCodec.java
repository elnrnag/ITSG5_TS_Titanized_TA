/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/MainCodec.java $
 *              $Id: MainCodec.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec;

import java.util.Map;
import java.util.TreeMap;

import org.etsi.codec.ITciCDWrapper;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.tci.TciCDProvided;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriMessage;

public class MainCodec extends ICodec {

    public MainCodec(ITciCDWrapper _cdReq) {
        super(null);
        this.cdReq = _cdReq;
    }

    public Value triDecode(TriMessage message, Type decodingHypothesis) {
//        System.out.println("############################################################################################");
        
        return decode(new CodecBuffer(message.getEncodedMessage()), decodingHypothesis);
    }

    public TriMessage triEncode(Value value) {
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        
        CodecBuffer encoded = encode(value);
        encoded.runCallbacks();
        return new TriMessageImpl(encoded.getBytes());
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> MainCodec.decode: " + decodingHypothesis.getName());
//        ByteHelper.dump(">>> MainCodec.decode: ", buf.getBytes());
        
        CodecFactory cf = CodecFactory.getInstance();
        try {
            TciCDProvided extCodec = cf.getExternalCodec(decodingHypothesis.getTypeEncoding());
            if(extCodec != null) {
                return extCodec.decode(new TriMessageImpl(buf.getBytes()), decodingHypothesis);
            } else {
                ICodec codec = cf.getCodec(
                        this,
                        decodingHypothesis.getTypeClass(),
                        decodingHypothesis.getTypeEncoding(),
                        decodingHypothesis.getName()
                        );
                codec.preDecode(buf, decodingHypothesis);
                
                // TODO To be removed, for debug purpose only
//                Value decValue = codec.decode(buf, decodingHypothesis);
//                System.out.println("<<< MainCodec.decode: " + decValue);
//                return decValue;
                return codec.decode(buf, decodingHypothesis);
            }
        } catch(Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CodecBuffer encode(Value value) {
        
        CodecFactory cf = CodecFactory.getInstance();
        TciCDProvided extCodec = cf.getExternalCodec(value.getValueEncoding());
        if(extCodec != null) {
            return new CodecBuffer(extCodec.encode(value).getEncodedMessage());
        }
        else {
            ICodec codec = CodecFactory.getInstance().getCodec(
                    this,
                    value.getType().getTypeClass(),
                    value.getValueEncoding(),
                    value.getType().getName()
                    );
            CodecBuffer preBuf = codec.preEncode(value);
            CodecBuffer buf = codec.encode(value);
            if(preBuf != null) {
                preBuf.append(buf);
                buf = preBuf;
            }
            
//            ByteHelper.dump("<<< MainCodec.encode: ", buf.getBytes());
            return buf;
        }
    }
    
    public String getHint(String key) {
//        System.out.println("getHint: " + key + ": " + hints.get(key));
        return hints.get(key);
    }
    
    public void setHint(String key, String value) {
//        System.out.println("setHint: " + key + ", " + value);
        hints.put(key, value);
    }
    
    public java.lang.Boolean getPresenceHint(String key) {
//        System.out.println("getPresenceHint: " + key + ": " + presenceHints.get(key));
        return presenceHints.get(key);
    }
    
    public void setPresenceHint(String key, java.lang.Boolean value) {
//        System.out.println("setPresenceHint: " + key + ", " + value);
        presenceHints.put(key, value);
    }
    
    public ITciCDWrapper getTciCDRequired() {
        return cdReq;
    }
    
    protected Map<String, String> hints = new TreeMap<String, String>();
    protected Map<String, java.lang.Boolean> presenceHints = new TreeMap<String, java.lang.Boolean>();
    protected ITciCDWrapper cdReq;
}
