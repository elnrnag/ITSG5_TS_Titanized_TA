/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import org.etsi.ttcn.tri.TriStatus;
import org.json.JSONException;
import org.json.JSONObject;

public class TitanTriStatus extends JSONObject implements TriStatus {
	private int status;
	private String resultString;
	
	private static String JSONKEY_RESULT              = "result";
	private static String JSONKEY_RESULT_VALUE        = "result";
	private static String JSONKEY_RESULT_STRING       = "result_string";
	
	private final static String TRI_OK = "TRI_ok";
	private final static String TRI_ERROR = "TRI_error";
	
	public TitanTriStatus(int status) {
		this.status = status;
	}
	
	public TitanTriStatus(int status, String resultString) {
		this.status = status;
		this.resultString = resultString;
	}
	
	public TitanTriStatus(TriStatus fromStatus) {
		this.status = fromStatus.getStatus();
		this.resultString = fromStatus.toString();
	}
	
	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public boolean equals(TriStatus status) {
		return this.status == status.getStatus();
	}

	public String getResultString() {
		return resultString;
	}

	public void setResultString(String resultString) {
		this.resultString = resultString;
	}
	
	@Override
	public String toString() {
		try {
			JSONObject container = new JSONObject();
			container.put(JSONKEY_RESULT_VALUE,statusToString());
			if (this.resultString != null) {
				container.put(JSONKEY_RESULT_STRING,this.resultString);
			}
			put(JSONKEY_RESULT,container);
		} catch (JSONException e) {
			TitanTriLogger.error(e.getMessage());
			e.printStackTrace();
		}
		return super.toString();
	}
	
	private String statusToString() {
		return this.status == TriStatus.TRI_OK ? TRI_OK : TRI_ERROR;
	}
}
