/**
 * @author      ETSI / STF462
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/common/org/etsi/common/ByteHelper.java $
 *              $Id: ByteHelper.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteHelper {

    public static byte[] changeEndianness(byte [] in) {
        byte[] out = new byte[in.length];
        for(int i=0; i < in.length; i++) {
            out[in.length - i -1] = in[i];
        }
        return out;
    }
    
    public static byte[] intToByteArray(int value, int length, ByteOrder byteOrder) {
        if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return changeEndianness(intToByteArray(value, length));
        }
        return intToByteArray(value, length);
    }
    
    public static byte[] intToByteArray(int value, int length) {
        byte[] b = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    public static byte[] floatToByteArray(final float value) {
        return ByteBuffer.allocate(Float.SIZE / Byte.SIZE).putFloat(value).array();
    }

    public static byte[] longToByteArray(long value, int length, ByteOrder byteOrder) {
        if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return changeEndianness(longToByteArray(value, length));
        }
        return longToByteArray(value, length);
    }
    
    public static byte[] longToByteArray(long value, int length) {
        byte[] b = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }
    
    /** Convert a byte array into a boolean
     * 
     * @param b The byte array to convert
     * @return The boolean value on success, false otherwise
     */
    public static Boolean byteArrayToBoolean(byte[] b) {
        // Sanity check
        if ((b == null) || (b.length != 1)) {
            return null;
        }

        return new Boolean(b[0] == 0x01);
    } // End of method byteArrayToBoolean
    
    public static Short byteArrayToShort(final byte[] b, ByteOrder byteOrder) {
        if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return byteArrayToShort(changeEndianness(b));
        }
        return byteArrayToShort(b);
    }
    
    /** Convert a byte array into a short value assuming that the first byte is the most significant
     * 
     * @param b The byte array to convert
     * @return The short value on success, 'Integer.MAX_VALUE' otherwise
     */
    public static Short byteArrayToShort(final byte[] b) {
        // Sanity check
        if ((b == null) || ((b.length * Byte.SIZE) > Short.SIZE)) {
            return Short.MAX_VALUE;
        }

        short value = 0;
        for (Short i = 0; i < b.length; i++) {
            value = (short)((value << 8) + (b[i] & 0xff));
        }

        return new Short(value);
    } // End of method byteArrayToInt
    
    public static Integer byteArrayToInt(final byte[] b, ByteOrder byteOrder) {
        if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return byteArrayToInt(changeEndianness(b));
        }
        return byteArrayToInt(b);
    }
    
    /** Convert a byte array into an integer assuming that the first byte is the most significant
     * 
     * @param b The byte array to convert
     * @return The integer value on success, 'Integer.MAX_VALUE' otherwise
     */
    public static Integer byteArrayToInt(final byte[] b) {
        // Sanity check
        if ((b == null) || ((b.length * Byte.SIZE) > Integer.SIZE)) {
            return Integer.MAX_VALUE;
        }

        int value = 0;
        for (int i = 0; i < b.length; i++) {
            value = (value << 8) + (b[i] & 0xff);
        }

        return new Integer(value);
    } // End of method byteArrayToInt
    
    /** Convert a byte array into a signed integer assuming that padding bits are in first byte
     * 
     * @param b The byte array to convert
     * @param significantBits number of significant bits in the array
     * @return The integer value
     */
    public static int byteArrayToSignedInt(final byte[] b, final int significantBits) {

        int value = 0;
        for (int i = 0; i < b.length; i++) {
            value = (value << 8) + (b[i] & 0xff);
        }
        
        int shift = 32 - significantBits;
        
        // Restore sign bit by shifting left and right
        if(shift > 0) {
            value <<= shift;
            value >>= shift;
        }

        return new Integer(value);
    }

    public static Long byteArrayToLong(final byte[] b, ByteOrder byteOrder) {
        if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return byteArrayToLong(changeEndianness(b));
        }
        return byteArrayToLong(b);
    }
    
    /** Convert a byte array into a Long assuming that the first byte is the most significant
     * 
     * @param b The byte array to convert
     * @return The Long value on success, 'Long.MAX_VALUE' otherwise
     */
    public static Long byteArrayToLong(final byte[] b) {
        // Sanity check
        if ((b == null) || ((b.length * Byte.SIZE) > Long.SIZE)) {
            return Long.MAX_VALUE;
        }

        long value = 0;
        for (int i = 0; i < b.length; i++) {
            value = (value << 8) + (b[i] & 0xff);
        }

        return new Long(value);
    } // End of method byteArrayToLong

    public static Float byteArrayToFloat(final byte[] b) {
        return ByteBuffer.wrap(b).getFloat();
    }

    public static byte[] hexStringToByteArray(final String s) {
        String str = "";
        for(String ss : s.split("[^0-9A-Fa-f]")) {
            str = str + ss;
        }
        int len = str.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4)
                                 + Character.digit(str.charAt(i+1), 16));
        }
        return data;
    }
    
    public static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            if(array != null) {
                length += array.length;
            }
        }
        byte[] result = new byte[length];
        int position = 0;
        for (byte[] array : arrays) {
            if(array != null) {
                System.arraycopy(array, 0, result, position, array.length);
                position += array.length;
            }
        }
        return result;
    }
    
    
    /** Extract a sub part of a byte array
     * @param array The original array
     * @param offset The offset to start the extract operation
     * @param length The number of bytes to extract
     * @return The sub part of a provided byte array
     */
    public static byte[] extract(final byte[] array, final int offset, final int length) {
        // Sanity check
        if ((array == null) || (array.length == 0) || (offset > array.length)) {
            return null;
        }

        byte[] result = new byte[length];
        System.arraycopy(array, offset, result, 0, length);
        return result;
    }

    /**
     * This method convert a byte array containing the couple (length + string) into a string
     * @param b The byte array to convert
     * @return A string value
     */
    public static String byteArrayWithLengthToString(final byte[] b) {
        // Sanity check
        if (b == null) {
            return null;
        } else if (b.length == 0) {
            return "";
        }

        // Extract the length of the string
        int length = byteArrayToInt(extract(b, 0, 4));
        // Extract the the string
        String result = "";
        for (int i = 0; i < length; i++) {
            result += (char)(b[4 + i]);
        } // End of 'for' statement

        return result;
    }

    public static String byteArrayToString(final byte[] buffer) {
        
        String result = "";
        if (buffer != null) {
            for(int i=0; i < buffer.length; ++i) {
                result += String.format("%02X", (byte)buffer[i]);
            }
        }
        return result;
    }
    
    /**
     * Dump a byte array in hex/ascii mode.
     * @param label The dump label
     * @param buffer The byte array to dump
     */
    public synchronized static void dump(final String label, final byte[] buffer) 
    {
        if ((buffer != null) && (buffer.length != 0))
        {
            System.out.println(label);
            StringBuilder finalHexLine = new StringBuilder();
            StringBuilder finalCharLine = new StringBuilder();
            int nCounter = 0;
            int nOffset = 0;
            // Flush header.
            System.out.println(" HEX | 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F  : 0 1 2 3 4 5 6 7 8 9 A B C D E F ");
            System.out.println("-----|+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+-:--+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
            for (int i = 0; i < buffer.length; ++i)
            {
                byte c = (byte)buffer[i];

                String fmtHex = String.format("%02x ", c);
                String fmtChar = String.format("%c ", Character.isISOControl((char)c) || c < 0 ? '.' : (char)c);

                if (nOffset % 16 == 0)
                {
                    finalHexLine.append((String)String.format("%05x| ", nOffset));
                }

                finalHexLine.append(fmtHex);
                finalCharLine.append(fmtChar);
                if (nCounter == 15)
                {
                    // Flush line.
                    System.out.println(String.format("%s : %s", finalHexLine.toString(), finalCharLine.toString()));
                    // Reset buffer.
                    finalHexLine.delete(0, finalHexLine.length());
                    finalCharLine.delete(0, finalCharLine.length());

                    nCounter = 0;
                }
                else
                {
                    nCounter++;
                }
                nOffset++;
            }
            if (nCounter < 16)
            {
                // Pad till 15.
                for (int i = nCounter; i < 16; i++)
                {
                    finalHexLine.append("   ");
                    finalCharLine.append("  ");
                }
                // Flush line.
                System.out.println(String.format("%s : %s", finalHexLine.toString(), finalCharLine.toString()));
            }
        }
    }
}
