/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/IntX.java $
 *              $Id: IntX.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.codec.generic.Integer;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

/**
 * @desc Codec for an integer of variable length
 * @see Draft ETSI TS 103 097 V1.1.14 Clause 4.2.1   IntX
 */
public class IntX extends Integer {
    
    /**
     * Specialised constructor
     * @param mainCodec The MainCodec reference
     */
    public IntX(MainCodec mainCodec) {
        super(mainCodec);
    }
    
    /**
     * @desc Decoder for an integer of variable length
     */
    @Override
    public Value decode(CodecBuffer buf, Type decodingHypothesis) {
//        System.out.println(">>> IntX.decode: " + decodingHypothesis.getName());
        
        // Read the first byte
        byte msb = buf.getBytes(0, 1)[0];
        if ((msb & 0x80) == 0x00) { // Integer < 128
            mainCodec.setHint("IntXLen", "8");
            return super.decode(buf, decodingHypothesis);
        } else {
            // Decode the length. The encoding of the length shall use at most 7 bits set to 1 (see Draft ETSI TS 103 097 V1.1.14 Clause 4.2.1    IntX)
            byte bit;
            byte byteLen = 1;
            do {
                bit = (byte) ((byte) (msb << byteLen++) & 0x80);
            } while (bit != 0x00);
            // Set the IntX length
            mainCodec.setHint(decodingHypothesis.getName() + "Len", String.valueOf(byteLen * Byte.SIZE ));
            // Remove the length from the real integer value
            byte[] newBuf = buf.getBytes();
            newBuf[0] &= (byte)(Math.pow(2.0, 8 - byteLen + 1) - 1);
            CodecBuffer newBuffer = new CodecBuffer(newBuf);
            buf.overwriteWith(newBuffer);
            // Decode it
            return super.decode(buf, decodingHypothesis);
        }
    }
    
    /**
     * @desc Encoder for an integer of variable length
     */
    @Override
    public CodecBuffer encode(Value value) {
//        System.out.println(">>> IntX.encode: " + value.getType().getName());
        
        if (mainCodec.getTciCDRequired().getInteger((IntegerValue)value) < 128) {
            mainCodec.setHint("IntXLen", "8");
            return super.encode(value);
        } else {
            long iv = mainCodec.getTciCDRequired().getInteger((IntegerValue)value);
            long bitLen = TlsHelper.getInstance().bitLength(iv);
            long byteLen = TlsHelper.getInstance().byteLength(bitLen);
            long flags = (long) ((byteLen | 1) << (byteLen * Byte.SIZE - TlsHelper.getInstance().bitLength(byteLen) - 1));
            long len = (long) (byteLen << (byteLen * Byte.SIZE - TlsHelper.getInstance().bitLength(byteLen) - 1));
            if ((flags & iv) != 0) { // We can encode the length on the MSB part
                byteLen += 1;
                len = (long) (byteLen << (byteLen * Byte.SIZE - TlsHelper.getInstance().bitLength(byteLen)));
            }
            mainCodec.setHint("integerLen", String.valueOf(byteLen * Byte.SIZE ));
            IntegerValue newValue = mainCodec.getTciCDRequired().setInteger((int)(iv | len));
            return super.encode(newValue);
        }
    }
    
} // End of class IntX 
