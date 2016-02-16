package org.etsi.ttcn.codec.generic;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tci.UnionValue;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Union.java $
 *              $Id: Union.java 1822 2014-11-18 09:18:17Z berge $
 */
public class Union extends ICodec {

    public Union(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {

        UnionValue uv = (UnionValue)decodingHypothesis.newInstance();
        String variant = "";

        variant = mainCodec.getHint(decodingHypothesis.getName());
//        System.out.println("Union: Looking for variant " + decodingHypothesis.getName() + "/" + variant);

        if(variant != null) {
            Value value = uv.getVariant(variant);
            if (value == null) {
                value = mainCodec.getTciCDRequired().getUnionValue(decodingHypothesis, variant);
                if (value != null) {
//                    System.out.println("Union (TCT3): " + value.getType().getName());
                    uv.setVariant(variant, mainCodec.decode(buf, value.getType()));
                    return uv;
                }
            } else {
//                System.out.println("Union (TTWB): " + value.getType().getName());
                uv.setVariant(variant, mainCodec.decode(buf, value.getType()));
                return uv;
            }
        }

        // no hint. Try all variants...
        String[] variants = uv.getVariantNames();
        for(int i=0; i < variants.length; i++) {
            // Copy buf to preserve it in case of failure
            CodecBuffer variantBuf = buf.getBuffer(0, buf.getNbBits());

            // Try to decode this variant
            Value vv = null;
            Value value = uv.getVariant(variants[i]);
            if (value == null) {
                value = uv.getVariant(mainCodec.getHint(variants[i]));
                if (value == null) {
                    value = mainCodec.getTciCDRequired().getUnionValue(decodingHypothesis, mainCodec.getHint(variants[i]));
                    if (value != null) {
//                        System.out.println("Union (TCT3): " + value.getType().getName());
                        vv = mainCodec.decode(buf, value.getType());
                    }
                } else {
//                    System.out.println("Union (TTWB): " + value.getType().getName());
                    vv = mainCodec.decode(buf, value.getType());
                }
            } else {
                vv = mainCodec.decode(buf, value.getType());
            }
            if(vv != null) {
                buf.overwriteWith(variantBuf); 
                uv.setVariant(variants[i], vv);
                return uv;
            }
        }
        return null;
    }

    @Override
    public CodecBuffer encode(Value value) {

        UnionValue uv = (UnionValue)value;

        CodecBuffer buf = new CodecBuffer();
        preEncode(buf, uv);
        CodecBuffer variantBuf = mainCodec.encode(uv.getVariant(uv.getPresentVariantName()));
        buf.append(variantBuf);

        return buf;
    }

    protected void preEncode(CodecBuffer buf, UnionValue uv) {

    }
}
