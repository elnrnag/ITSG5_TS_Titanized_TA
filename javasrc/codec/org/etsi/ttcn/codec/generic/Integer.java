/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/generic/Integer.java $
 *              $Id: Integer.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec.generic;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.common.ByteHelper;
import org.etsi.ttcn.codec.ICodec;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.CodecBuffer;

public class Integer extends ICodec {

    protected final static Pattern UNSIGNED_VARIANT = Pattern.compile("\\w*unsigned\\w*|UInt");
    
    public Integer(MainCodec mainCodec) {
        super(mainCodec);
    }

    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> Integer.decode: " + decodingHypothesis.getName());

        IntegerValue iv = null;
        int lengthInBits;
        byte[] value = null;

        // Get length
        String hint = mainCodec.getHint(decodingHypothesis.getName() + "Len");
        if(hint == null) {
            if (decodingHypothesis.getTypeEncodingVariant() == null) {
                lengthInBits = getVariantBitLength(decodingHypothesis.getName()); // TCT3
            } else {
                lengthInBits = getVariantBitLength(decodingHypothesis.getTypeEncodingVariant());
            }
        }
        else {
            lengthInBits = java.lang.Integer.parseInt(hint);
        }
        
        value = buf.readBits(lengthInBits);
        
        try {
            Matcher matcher = UNSIGNED_VARIANT.matcher(decodingHypothesis.getTypeEncodingVariant());
            if(matcher.find()) {
                // Unsigned integer
                if(lengthInBits >= java.lang.Integer.SIZE) { 
                    iv = mainCodec.getTciCDRequired().setInteger(new BigInteger(1, value)); 
                }
                else {
                    iv = mainCodec.getTciCDRequired().setInteger(ByteHelper.byteArrayToInt(value));
                }
            }
            else {
                // Signed integer 
                iv = mainCodec.getTciCDRequired().setInteger(ByteHelper.byteArrayToSignedInt(value, lengthInBits));
            }
        }
        catch(Exception e) {
//            System.out.println("Integer.encode: " + decodingHypothesis.getTypeEncoding() + ", " + decodingHypothesis.getTypeEncodingVariant());
            // Assume unsigned integer
            if(lengthInBits >= java.lang.Integer.SIZE) { 
                iv = mainCodec.getTciCDRequired().setInteger(new BigInteger(1, value)); 
            }
            else {
                iv = mainCodec.getTciCDRequired().setInteger(ByteHelper.byteArrayToInt(value));
            } // else, empty on purpose
        }
        
        return iv;
    }

    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println(">>> Integer.encode: " + value.getType().getName());

        IntegerValue iv = (IntegerValue)value;
        byte[] encoded = null;
        CodecBuffer res = new CodecBuffer();
        int lengthInBits = 0;
        int lengthInBytes = 0;

        // Get length
        String hint = mainCodec.getHint(value.getType().getName() + "Len");
        if(hint == null) {
//            System.out.println("Integer.encode: " + value.getValueEncodingVariant() + ", " + value.getValueEncoding());
//            System.out.println("Integer.encode: " + value.getType().getTypeEncodingVariant() + ", " + value.getType().getTypeEncoding());
            if (value.getType().getTypeEncodingVariant() != null) {
                lengthInBits = getVariantBitLength(value.getType().getTypeEncodingVariant());
            } else {
                lengthInBits = getVariantBitLength(value.getType().getName()); // TCT3
            }
        }
        else {
            lengthInBits = java.lang.Integer.parseInt(hint);
        }
        lengthInBytes = lengthInBits / 8 + (((lengthInBits % 8) > 0)?1:0);
        
//        System.out.println(String.format("Integer.encode: length: %d", lengthInBytes));
        if (lengthInBits > java.lang.Integer.SIZE) {
            encoded = ByteHelper.longToByteArray(mainCodec.getTciCDRequired().getBigInteger(iv), lengthInBytes);
        } else {
            encoded = ByteHelper.intToByteArray(mainCodec.getTciCDRequired().getInteger(iv), lengthInBytes);
        }
        res.setBits(encoded, lengthInBits);

        return res;
    }

}
