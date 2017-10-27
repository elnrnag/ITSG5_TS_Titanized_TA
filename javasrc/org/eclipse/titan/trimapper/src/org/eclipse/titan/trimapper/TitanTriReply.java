/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import org.json.JSONException;
import org.json.JSONObject;

public class TitanTriReply extends JSONObject {
	public static final String ENCODING_ERROR = "Unable to encode reply message";
	
	public TitanTriReply(int msgId, JSONObject message) {
		try {
			put("msg_id",msgId);
			put("msg",message);
		} catch (JSONException je){
			TitanTriLogger.error(ENCODING_ERROR);
		}
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
