/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/CodecBuffer.java $
 *              $Id: CodecBuffer.java 1822 2014-11-18 09:18:17Z berge $
 */
package org.etsi.ttcn.codec;

import java.util.Map;
import java.util.TreeMap;

import org.etsi.ttcn.common.ByteHelper;

public class CodecBuffer {

    /**
     * Raw byte storage array. Last byte may be a "partial byte", i.e. some of its bits may not be significants
     * @see     bits
     */
    protected byte[] buffer;
        
    /**
     * Number of significant bits in the "partial byte"
     * Significant bits are always stored "left-aligned" (i.e. MSBs) in the "partial byte"
     * @see     buffer 
     */
    protected int bits;
        
    /**
     * Marker storage
     */
    protected Map<String, Marker> markers = new TreeMap<String, Marker>();

    /**
     * Some useful byte masks
     */
    private byte[] masks = new byte[]{(byte)0x00, (byte)0x80, (byte)0xC0, (byte)0xE0, (byte)0xF0, (byte)0xF8, (byte)0xFC, (byte)0xFE};
    
    /**
     * Some useful byte masks
     */
    private byte[] nomasks = new byte[]{(byte)0xFF, (byte)0x7F, (byte)0x3F, (byte)0x1F, (byte)0x0F, (byte)0x07, (byte)0x03, (byte)0x01};

    /**
     * Main constructor. Initialises an empty buffer
     */
    public CodecBuffer() {
        bits = 0;
        buffer = new byte[]{};
    }

    /**
     * Constructor. Initialises the buffer using provided byte array
     * @param bytes     Initial content of the buffer
     */
    public CodecBuffer(byte[] bytes) {
        bits = 0;
        buffer = bytes.clone();
    }

    /**
     * Overwrite content of current buffer using data of newBuffer
     * @param newBuffer CodecBuffer containing new data
     */
    public void overwriteWith(CodecBuffer newBuffer) {
        
        bits = newBuffer.bits;
        buffer = newBuffer.buffer.clone();
        markers = new TreeMap<String, Marker>(newBuffer.markers);       
    }
    
    /**
     * Retrieves the number of significant bits in the buffer.
     * Warning: getNbBytes() != (getNbBits() * 8) 
     * @return  The number of significant bits in the buffer
     * @see     getNbBytes()
     */
    public int getNbBits() {

        if(bits > 0) {
            return (buffer.length - 1) * 8 + bits;
        }
        return buffer.length * 8;
    }

    /**
     * Retrieves the number of bytes used to store the buffer.
     * Warning: getNbBytes() != (getNbBits() * 8) 
     * @return  The number of bytes used to store the buffer
     * @see     getNbBits()
     */
    public int getNbBytes() {
        return buffer.length;
    }

    /**
     * Concatenates current CodecBuffer and the CodecBuffer passed as parameter.
     * Markers of buffer2 are preserved and integrated into the current CodecBuffer.
     * @param buffer2   The CodecBuffer to be appended
     */
    public void append(CodecBuffer buffer2) {
        
        // copy buffer content
        int nbBits = getNbBits();
        if(buffer2.getNbBits() > 0) {
            appendBits(buffer2.getBits(), buffer2.getNbBits());
        }

        // integrate markers
        for(Map.Entry<String, Marker> entry : buffer2.markers.entrySet()) {
            String key = entry.getKey();
            Marker marker = entry.getValue();
            marker.move(nbBits);
            markers.put(key, marker);
        }
    }

    /**
     * Associates a new marker to current CodecBuffer.
     * Inserting marker with name identical to a previously inserted one will overwrite it.
     * @param key       Name of the marker
     * @param pos       Position of the marker relative to the current position
     * @param callback  Optional callback object to be executed later, or null
     * @see             runCallbacks()
     */
    public void setMarker(String key, int pos, IMarkerCallback callback) {
        markers.put(key, new Marker(pos, callback));
    }

    /**
     * Executes all the callbacks associated to current CodecBuffer's markers
     * @see     setMarkers()
     */
    public void runCallbacks() {
//        System.out.println("Running callbacks...");
        for(Map.Entry<String, Marker> entry : markers.entrySet()) {
            String key = entry.getKey();
//            System.out.println("Running callback: " + key);
            Marker marker = entry.getValue();
            IMarkerCallback callback = marker.getCallback();
            if(callback != null) {
                CodecBuffer Left = getBuffer(0, marker.getPos());
                CodecBuffer Right = getBuffer(marker.getPos(), getNbBits() - marker.getPos());

                callback.run(key, Left, Right);

                // Overwrite self with Left+Right
                // TODO: take care if Right or Left have change to much.
                Left.append(Right);
                buffer = Left.buffer;
                bits = Left.bits;
            }
        }	
    }

    /**
     * Creates a new CodecBuffer from current CodecBuffer's content
     * Markers of current CodecBuffer are preserved and integrated into the new CodecBuffer.
     * @param start     Start point. Bit offset from current position
     * @param length    Amount of bits to be copied
     * @return          The new CodecBuffer
     */
    public CodecBuffer getBuffer(int start, int length) {
        // TODO: check param validity

        CodecBuffer res = new CodecBuffer();
        res.setBits(getBits(start, length), length);

        for(Map.Entry<String, Marker> entry : markers.entrySet()) {
            String key = entry.getKey();
            Marker marker = entry.getValue();
            int pos = marker.getPos();
            if(pos >= start && pos < (start + length)) {
                res.setMarker(key, pos - start, marker.getCallback());
            }
        }	
        return res;
    }

    /**
     * Appends some raw bytes at the end of the current CodecBuffer
     * @param origBytes     Bytes to be appended
     */
    public void appendBytes(byte[] origBytes) {

        byte[] bytes = origBytes.clone();

        int lastByte = buffer.length - 1;
        buffer = ByteHelper.concat(buffer, bytes);

        if(bits != 0) {
            for(int i=0; i < bytes.length; i++) {
                buffer[lastByte] &= masks[bits];
                buffer[lastByte] |= ((bytes[i] >>> bits) & nomasks[bits]);
                lastByte++;
                buffer[lastByte] = (byte)(buffer[lastByte] << (8 - bits));
            }

            buffer[buffer.length - 1] &= masks[bits];
        }
    }

    /**
     * Appends some raw bits at the end of the current CodecBuffer
     * @param origBytes     Byte array used to store the bits to be appended.   
     *                      It MUST be right-aligned. First byte (origBytes[0]) may be a 
     *                      partial byte if 'nbBits' is not a multiple of 8. 
     *                      In this case MSBs of this byte are ignored and not copied
     * @param nbBits        Number of significant bits in 'origBytes'
     */
    public void appendBits(byte[] origBytes, int nbBits) {

        byte[] bytes = origBytes.clone();
        int rbits = nbBits % 8;
        int nbBytes = nbBits / 8 + ((rbits > 0)?1:0);
        int lastByte = buffer.length - 1;

        // Left-align received bytes
        if(rbits !=0) {
            int i;
            for(i=(bytes.length - nbBytes); i < (nbBytes - 1); i++) {
                bytes[i] = (byte)(bytes[i] << (8 - rbits));
                bytes[i] |= ((bytes[i+1] >>> rbits) & nomasks[rbits]);
            }
            bytes[i] = (byte)(bytes[i] << (8 - rbits));
            bytes[i] &= masks[rbits];
        }

        buffer = ByteHelper.concat(buffer, ByteHelper.extract(bytes, (bytes.length - nbBytes), nbBytes));
        if(bits != 0) {
            int i;
            for(i=lastByte; i < (lastByte + nbBytes); i++) {
                buffer[i] &= masks[bits];
                buffer[i] |= ((buffer[i+1] >>> bits) & nomasks[bits]);
                buffer[i+1] = (byte)(buffer[i+1] << (8 - bits));
            }
            buffer[i] &= masks[bits];

            if((rbits > 0) && (rbits + bits <= 8)) {
                // Remove trailing byte (garbage)
                buffer = ByteHelper.extract(buffer, 0, buffer.length - 1);
            }

        }

        bits += nbBits;
        bits %= 8;
    }

    /**
     * Overwrite the content of CodecBuffer using the provided bytes
     * @param bytes     New content of the CodecBuffer
     * @see             setBits()
     */
    public void setBytes(byte[] bytes) {
        buffer = bytes.clone();
        bits = 0;
        markers.clear();
    }

    /**
     * Overwrite the content of CodecBuffer using the provided bits
     * @param bytes     Byte array used to store the bits to be used.   
     *                      It MUST be right-aligned. First byte (origBytes[0]) may be a 
     *                      partial byte if 'nbBits' is not a multiple of 8. 
     *                      In this case MSBs of this byte are ignored and not copied
     * @param nbBits        Number of significant bits in 'bytes'
     * @see             setBytes()
     */
    public void setBits(byte[] bytes, int nbBits) {

        if(nbBits == 0) {
            bits = 0;
            buffer = new byte[]{};
        }
        else {
            int i;
            int rbits = nbBits % 8;
            int nbBytes = nbBits / 8 + ((rbits > 0)?1:0);
            bits = rbits;
            buffer = bytes.clone();

            if(bits !=0) {
                for(i=(bytes.length - nbBytes); i < (nbBytes - 1); i++) {
                    buffer[i] = (byte)(buffer[i] << (8 - bits));
                    buffer[i] |= ((buffer[i+1] >>> bits) & nomasks[bits]);
                }
                buffer[i] = (byte)(buffer[i] << (8 - bits));
                buffer[i] &= masks[bits];
            }
        }
        markers.clear();
    }

    /**
     * Extracts some bytes at the beginning of the buffer. Read bytes are discarded from the buffer.
     * @param nbBytes   Number of bytes to be read
     * @return          Byte array containing the 'nbBytes' first bytes of the buffer.
     *                  Byte array's length may be shorter than requested if buffer does not 
     *                  contain enough bytes
     * @see             getBytes()                 
     */
    public byte[] readBytes(int nbBytes) {
        byte[] result = getBytes(0, nbBytes);
        int newLength = getNbBits() - (nbBytes * 8);

        if(result != null) {
            if(newLength > 0) {
                setBits(getBits(nbBytes * 8, newLength), newLength);
            }
            else {
                bits = 0;
                buffer = new byte[] {};
            }
        }
        return result;
        // TODO: move markers
    }

    /**
     * Extracts some bits at the beginning of the buffer. Read bits are discarded from the buffer.
     * @param nbBytes   Number of bits to be read
     * @return          Byte array containing the 'nbBits' first bits of the buffer.
     *                  Number of returned bits may be smaller than requested if buffer does not 
     *                  contain enough bits. Returned byte array is right-aligned. 
     *                  First byte may be a partial byte if 'nbBits' is not a multiple of 8. 
     *                  In this case MSBs of this byte are not significants and padded with '0's
     * @see             getBits()                 
     */
    public byte[] readBits(int nbBits) {
        byte[] result = getBits(0, nbBits);
        int newLength = getNbBits() - nbBits;
        byte[] newBuffer = getBits(nbBits, newLength);

        if(result != null) {
            setBits(newBuffer, newLength);
        }
        return result;
        // TODO: move markers
    }

    /**
     * Retrieves the raw content of the CodecBuffer
     * @return  Raw byte array used to store CodecBuffer's content
     *          Returned byte array is left-aligned. 
     *          Last byte may be a partial byte if 'bits' is not null. 
     *          In this case LSBs of this byte are not significants and their value is undetermined
     */
    public byte[] getBytes() {
        return buffer;
    }

    /**
     * Retrieves some bytes from the CodecBuffer. Returned bytes are not removed from the buffer
     * @param start     Start point (octet index)
     * @param nbBytes   Number of bytes to be returned
     * @return          Extracted bytes.
     *                  Returned byte array is left-aligned. 
     *                  Last byte may be a "partial byte" if it is the last byte of CodecBuffer and if 'bits' is not null. 
     *                  In this case LSBs of this byte are not significants and their value is undetermined
     * @see             ReadBytes()
     */
    public byte[] getBytes(int start, int nbBytes) {

        if(start > buffer.length) {
            System.err.println("bad start: " + start);
            return null;
        }
        if((start + nbBytes) > buffer.length) {
            System.err.println("bad length: " + (start + nbBytes) + " (" + buffer.length + " bytes remaining)");
            return null;
        }

        if(nbBytes < 0) {
            System.err.println("bad length: " + (nbBytes) + " (" + buffer.length + " bytes remaining)");
            return null;
        }

        return ByteHelper.extract(buffer, start, nbBytes);
    }

    /**
     * Retrieves all the bits from the CodecBuffer. Returned bits are not removed from the buffer
     * @return          Extracted bits stored in a byte array.
     *                  Returned byte array is right-aligned. 
     *                  First byte may be a "partial byte" if 'nbBits' is not a multiple of 8. 
     *                  In this case MSBs of this byte are not significants and their value is '0'
     * @see             ReadBits()
     */
    public byte[] getBits() {
        return getBits(0, getNbBits());
    }

    /**
     * Retrieves some bits from the CodecBuffer. Returned bits are not removed from the buffer
     * @param start     Start point (bit index)
     * @param nbBits    Number of bits to be returned
     * @return          Extracted bits stored in a byte array.
     *                  Returned byte array is right-aligned. 
     *                  First byte may be a "partial byte" if 'nbBits' is not a multiple of 8. 
     *                  In this case MSBs of this byte are not significants and their value is '0'
     * @see             ReadBits()
     */
    public byte[] getBits(int start, int nbBits) {

        int byteIndex = start / 8;
        int bitOffset = start % 8;
        int nbBytes = ((bitOffset > 0)?1:0) + (((nbBits - 8 + bitOffset) / 8) + ((bitOffset==0)?1:0)) + ((((nbBits - 8 + bitOffset) % 8) > 0)?1:0);
        //            leading partial byte    complete bytes                                            trailing partial byte  

        if(byteIndex > buffer.length) {
            System.err.println("bad start: " + byteIndex + "(" + start + ")"  + " (" + buffer.length + " bytes remaining)");
            return null;
        }
        if((byteIndex + nbBytes) > buffer.length) {
            System.err.println("bad length: " + (byteIndex + nbBytes) + "(" + nbBits + ")" + " (" + buffer.length + " bytes remaining)");
            return null;
        }

        byte[] tmp = ByteHelper.extract(buffer, byteIndex, nbBytes);

        if(bitOffset != 0) {
            tmp[0] = (byte)(tmp[0] << bitOffset);
            for(int i=1; i < tmp.length; i++) {
                tmp[i-1] &= masks[8 - bitOffset];
                tmp[i-1] |= ((tmp[i] >>> (8 - bitOffset)) & nomasks[8 - bitOffset]);
                tmp[i] = (byte)(tmp[i] << bitOffset);
            }
        }

        tmp = ByteHelper.extract(tmp, 0, nbBits / 8 + (((nbBits % 8) > 0)?1:0));

        if(nbBits % 8 > 0) {
            for(int i=tmp.length-1; i >= 0; i--) {
                tmp[i] = (byte)((tmp[i] >>> (8 - (nbBits % 8))) & nomasks[8 - (nbBits % 8)]);
                if(i > 0) {
                    tmp[i] |= (byte)(tmp[i-1] << (nbBits % 8));
                }
            }
        }

        return tmp;
    }

    /**
     * Retrieves some bits from the CodecBuffer. Returned bits are not removed from the buffer
     * @param markerKey Name of the marker serving as starting point for the extraction
     * @param nbBits    Number of bits to be returned
     * @return          Extracted bits stored in a byte array.
     *                  Returned byte array is right-aligned. 
     *                  First byte may be a "partial byte" if 'nbBits' is not a multiple of 8. 
     *                  In this case MSBs of this byte are not significants and their value is '0'
     * @see             ReadBits()
     */
    public byte[] getBits(String markerKey, int nbBits) {
        Marker marker = markers.get(markerKey);

        if(marker != null) {
            return getBits(marker.getPos(), nbBits);
        }
        return null;
    }
    
    /**
     * Private class used to represent markers that can be insterted in CodecBuffer
     */
    private class Marker {

        /**
         * Constructor.
         * @param pos       Position of the marker relative to the current position
         * @param callback  Optional callback object to be executed later, or null
         */
        public Marker(int pos, IMarkerCallback callback) {
            this.pos = pos;
            this.callback = callback;
        }

        /**
         * Retrieve the position of the marker (bit offset from buffer's start).
         * @return  The position of the marker
         */
        public int getPos() {
            return pos;
        }

        /**
         * Retrieve the callback object associated to the marker.
         * @return  The callback object associated to the marker, or null
         */
        public IMarkerCallback getCallback() {
            return callback;
        }

        /**
         * Changes the position of the marker in the buffer
         * @param nbBits    Position offset from marker's current position (can be negative)
         */
        public void move(int nbBits) {
            pos += nbBits;
        }

        /**
         * Current position of the marker
         */
        private int pos;
        
        
        /**
         * Callback object associated to the marker
         */
        private IMarkerCallback callback;
    }
}
