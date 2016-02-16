/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/TlsHelper.java $
 *              $Id: TlsHelper.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecBuffer;
import org.etsi.ttcn.common.ByteHelper;

public class TlsHelper {
    
    private static TlsHelper Instance = new TlsHelper();
    
    public static TlsHelper getInstance() { return Instance; }
    
    private TlsHelper() {
    }
    
    public byte[] size2tls(final int length) {
        byte[] result = null;
        if (length < 128) { // One byte length
            result = new byte[] { (byte)length }; 
        } else {
            long lv = length;
            long bitLen = bitLength(lv);
            long byteLen = byteLength(bitLen);
            long flags = (long) ((byteLen | 1) << (byteLen * Byte.SIZE - bitLength(byteLen) - 1));
            long len = (long) (byteLen << (byteLen * Byte.SIZE - bitLength(byteLen) - 1));
            if ((flags & lv) != 0) { // We can encode the length on the MSB part
                byteLen += 1;
                len = (long) (byteLen << (byteLen * Byte.SIZE - bitLength(byteLen)) - 1);
            }
            result = ByteHelper.longToByteArray((long)(lv | len), (int) byteLen);
        }
        
        return result;
    }
    
    public long tls2size(CodecBuffer buf) {
    	// Sanity check
        if (buf.getNbBytes() == 0) {
        	return 0;
        }
    	
        // Read the first byte
        byte msb =  buf.readBits(Byte.SIZE)[0];
        if ((msb & 0x80) == 0x00) { // Integer < 128
            return msb;
        } else {
            // Decode the length. The encoding of the length shall use at most 7 bits set to 1 (see Draft ETSI TS 103 097 V1.1.14 Clause 4.1    Presentation Language Table 1/8)
            byte bit;
            byte byteLen = 1;
            do {
                bit = (byte) ((byte) (msb << byteLen++) & 0x80);
            } while (bit != 0x00);
            // Set the IntX length
            byte[] length = ByteHelper.concat(new byte[] { msb }, buf.readBytes(byteLen - 1));
            length[0] &= (byte)(Math.pow(2.0, 8 - byteLen + 1) - 1);
            long lv = ByteHelper.byteArrayToLong(length);
            return lv;
        }
    }
    
    public long bitLength(final long value) {
        return (long) Math.ceil(Math.log(value) / Math.log(2));
    }
    
    public long byteLength(final long value) {
        double d = value; // Convert int to double
        return (long) Math.ceil(d / Byte.SIZE);
    }
    
} // End of class TlsHelper 
