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

import org.etsi.ttcn.tri.TriAddress;
import org.json.JSONException;
import org.json.JSONObject;

public class TitanTriAddress extends JSONObject implements TriAddress{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] address;
	private int numberOfBits = 0;
	
	private static String JSONKEY_SUT_ADDRESS = "sut_address";

	public TitanTriAddress(JSONObject jo) throws JSONException, UnsupportedEncodingException {
		if (jo != null) {
			this.address = jo.getString(JSONKEY_SUT_ADDRESS).getBytes("UTF-8");
			this.numberOfBits = this.address.length * 8;
		}
		
	}
	
	public TitanTriAddress(TriAddress addr) {
		address = addr.getEncodedAddress();
		numberOfBits = addr.getNumberOfBits();
	}
	
	public TitanTriAddress(byte[] addr) {
		address = addr;
		numberOfBits = addr.length*8;
	}
	
	
	@Override
	public byte[] getEncodedAddress() {
		return this.address;
	}

	@Override
	public void setEncodedAddress(byte[] address) {
		this.address = address;
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
	public boolean equals(TriAddress address) {
		return Arrays.equals(this.address, address.getEncodedAddress());
	}

	@Override
	public String toString() {
		try {
			put(JSONKEY_SUT_ADDRESS, new String(address, "UTF-8"));
		} catch (JSONException je) {
			TitanTriLogger.error(je.getMessage());
		} catch (UnsupportedEncodingException uee) {
			TitanTriLogger.error(uee.getMessage());
		}
		return super.toString();
	}

}
