/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/TriMessageImpl.java $
 *              $Id: TriMessageImpl.java 1133 2013-08-09 09:26:40Z berge $
 */
package org.etsi.ttcn.codec; // TODO: use TriMessage implementation provided by tool

import org.etsi.ttcn.tri.TriMessage;

public class TriMessageImpl implements TriMessage {

    /**
     * 
     */
    private static final long serialVersionUID = -2657880550149993668L;
    private byte[] message;

    public TriMessageImpl(byte[] message) {
        this.message = message;
    }

    @Override
    public boolean equals(TriMessage message) {
        return java.util.Arrays.equals(this.message, message.getEncodedMessage()) && getNumberOfBits() == message.getNumberOfBits();
    }

    @Override
    public byte[] getEncodedMessage() {
        return this.message;
    }

    @Override
    public int getNumberOfBits() {
        return this.message.length * Byte.SIZE;
    }

    @Override
    public void setEncodedMessage(byte[] message) {
        this.message = message;
    }

    @Override
    public void setNumberOfBits(int amount) {
        int length = amount / 8;
        if(this.message == null) {
            if ((amount % 8) != 0) {
                length += 1;
            }
            this.message = new byte[length];
        } 
        else if(this.message.length > length) {
            throw new UnsupportedOperationException();
        } 
        else if (this.message.length < length) {
            throw new UnsupportedOperationException();
        }
    }
} 
