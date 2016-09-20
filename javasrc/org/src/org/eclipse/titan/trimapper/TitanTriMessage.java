/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import org.etsi.ttcn.tri.TriMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class TitanTriMessage extends JSONObject implements TriMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public  static String JSONKEY_DATA                = "data";
	private static String JSONKEY_ENCODED_DATA        = "encoded_data";
	private static String JSONKEY_TV_DATA             = "tv_data";
	
	private byte[] message;
	private int numberOfBits;
	
	
	public TitanTriMessage(byte[] encodedMessage) {
		this.message = encodedMessage;
		numberOfBits = encodedMessage.length * 8;
	}
	
	//Called in Southbound direction
	public TitanTriMessage(JSONObject jmessage) 
			throws JSONException, UnsupportedEncodingException {
		JSONObject jdata = jmessage.getJSONObject(JSONKEY_DATA);
		if (jdata.has(JSONKEY_ENCODED_DATA)) {
			String octetstring = jdata.getString(JSONKEY_ENCODED_DATA);
			message = DatatypeConverter.parseHexBinary(octetstring);
			//message = octetstring.getBytes("UTF-8");
			numberOfBits = message.length * 8;
		} else {
			jmessage.getString(JSONKEY_TV_DATA);
			//TODO: Handle TypeValue pair unencoded messages
		}
		
	}
	
	@Override
	public byte[] getEncodedMessage() {
		return this.message;
	}

	@Override
	public void setEncodedMessage(byte[] message) {
		this.message = message;
	}

	@Override
	public boolean equals(TriMessage message) {
		return Arrays.equals(this.message, message.getEncodedMessage());
	}

	@Override
	public int getNumberOfBits() {
		return this.numberOfBits;
	}

	@Override
	public void setNumberOfBits(int amount) {
		this.numberOfBits = amount;
	}

	@Override
	public String toString() {
		try {
			String hexString = DatatypeConverter.printHexBinary(message);
			put(JSONKEY_ENCODED_DATA,hexString);
		} catch (JSONException e) {
			TitanTriLogger.error(e.getMessage());
			e.printStackTrace();
		}
		return super.toString();
	}

}
