/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import java.nio.ByteBuffer;

public class TPKTHeader {
	
	public static int TPKT_HDR_LENGTH = 4;
	
	private static byte VERSION  = (byte)3;
	private static byte RESERVED = (byte)0;
	
	//TPKT header (RFC 1006, section 6)
	private TPKTHeader() {
	}
	
	private static short fromByteArray(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getShort();
	}
	
	public static short getPayloadLength(byte[] tpktHdr) {
		byte[] msgLength = new byte[2];
		msgLength[0] = tpktHdr[2];
		msgLength[1] = tpktHdr[3];
		
		return fromByteArray(msgLength);
	}
	
	public static byte[] getHeaderAsByteArray(short payloadLength) {
		byte[] hdr = new byte[TPKT_HDR_LENGTH];
		hdr[0] = VERSION;
		hdr[1] = RESERVED;
		hdr[2] = (byte)(payloadLength>>>8);
		hdr[3] = (byte)(payloadLength);
		return hdr;
	}
	
}
