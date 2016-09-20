/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import org.etsi.ttcn.tri.TriAddress;
import org.etsi.ttcn.tri.TriMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class TitanEnqueueMessage extends JSONObject {
	private TitanTriAddress address;
	private TitanTriMessage message;
	
	private static String JSONKEY_ENQUEUE_MSG = "enqueue_msg";
	
	public TitanEnqueueMessage(TriAddress addr, TriMessage msg) {
		if (addr instanceof TitanTriAddress) {
			address = (TitanTriAddress)addr;
		} else {
			address = new TitanTriAddress(addr);
		}
		
		if (msg instanceof TitanTriMessage) {
			message = (TitanTriMessage)msg;
		} else {
			message = new TitanTriMessage(msg.getEncodedMessage());
		}
	}
	
	@Override
	public String toString() {
		try {
			JSONObject container = new JSONObject();
			if (address.getEncodedAddress() != null && address.getEncodedAddress().length > 0) {
				container.put(TitanPortHandler.JSONKEY_ADDR, address);
			}
			container.put(TitanTriMessage.JSONKEY_DATA, message);
			put(JSONKEY_ENQUEUE_MSG, container);
			
		} catch (JSONException e) {
			TitanTriLogger.error(e.getMessage()+": Unable to produce JSON string");
			e.printStackTrace();
		}
		
		return super.toString();
	}
}
