/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/ICodec.java $
 *              $Id: ICodec.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ICodec {

    public ICodec(MainCodec mainCodec) {
        this.mainCodec = mainCodec;
        this.len = -1;
    }

    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
        return null;
    }

    public CodecBuffer encode(Value value) {
        return null;
    }

    protected int getVariantBitLength(String variant) {
        try {
            Matcher matcher = PATTERN_VARIANT.matcher(variant);
            if(matcher.find()) {
                if (matcher.group(3) != null)
                    return java.lang.Integer.parseInt(matcher.group(3));
                else
                    return java.lang.Integer.parseInt(matcher.group(7));
            }
        }
        catch(Exception e) {
            // Empty on purpose
        }
        return 0;
    }

    protected int getEncodingLength(String encoding) {
        try {
            Matcher matcher = PATTERN_ENCODING.matcher(encoding);
            if(matcher.find()) {
                return java.lang.Integer.parseInt(matcher.group(1));
            }
        }
        catch(Exception e) {
            // Empty on purpose
        }
        return 0;
    }

    protected void setLength(int len) {
        this.len = len;
    }

    protected int getLength() {
        return len;
    }

    protected void preDecode(CodecBuffer buf, Type decodingHypothesis) {
        return;
    }

    public CodecBuffer preEncode(Value value) {
        return null;
    }
    
    protected final static Pattern PATTERN_VARIANT = Pattern.compile("((\\w+)\\s+)?((\\d+))\\s+bit|(\\w{0,1})(Int)(\\d+)");
    protected final static Pattern PATTERN_ENCODING = Pattern.compile("length\\((\\d+)\\)");
    protected MainCodec mainCodec;
    private int len;

}
