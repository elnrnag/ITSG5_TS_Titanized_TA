/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import org.etsi.ttcn.tri.TriComponentId;
import org.etsi.ttcn.tri.TriPortId;
import org.json.JSONException;
import org.json.JSONObject;

public class TitanTriPortId extends JSONObject implements TriPortId {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String         portName;
	private String         portTypeName;
	private TriComponentId component;
	private boolean        isArray      = false;
	private int            portIndex    = -1;
	
	private static String JSONKEY_PORT_NAME           = "portName";
	private static String JSONKEY_PORT_TYPE_NAME      = "portTypeName";
	private static String JSONKEY_COMPONENT           = "component_";
	private static String JSONKEY_PORTINDEX           = "portindex";
	
	public TitanTriPortId(String portName, String portTypeName, TriComponentId component) {
		this.portName     = portName;
		this.portTypeName = portTypeName;
		this.component    = component;
	}
	
	public TitanTriPortId(String portName, String portTypeName, TriComponentId component, int portIndex) {
		this.portName     = portName;
		this.portTypeName = portTypeName;
		this.component    = component;
		this.isArray      = true;      //If portIndex is specified we treat it as member of a portArray 
		this.portIndex    = portIndex;
	}
	
	public TitanTriPortId(TriPortId portId) {
		this.portName     = portId.getPortName();
		this.portTypeName = portId.getPortTypeName();
		this.component    = portId.getComponent();
		this.isArray      = portId.isArray(); 
		this.portIndex    = portId.getPortIndex();
	}
	
	public TitanTriPortId(JSONObject jPortId) throws JSONException {
		this.portName                 = jPortId.getString(JSONKEY_PORT_NAME);
		this.portTypeName             = jPortId.getString(JSONKEY_PORT_TYPE_NAME);
		this.component   = new TitanTriComponent(jPortId.getJSONObject(JSONKEY_COMPONENT));
		this.portIndex                = jPortId.optInt(JSONKEY_PORTINDEX, -1);
		if (portIndex != -1) {
			this.isArray      = true;
		}
	}
	
	@Override
	public String getPortName() {
		return portName;
	}

	@Override
	public TriComponentId getComponent() {
		return component;
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	@Override
	public int getPortIndex() {
		return portIndex;
	}

	@Override
	public String getPortTypeName() {
		return portTypeName;
	}

	@Override
	public String toString() {
		try {
			put(JSONKEY_PORT_NAME, this.portName);
			put(JSONKEY_PORT_TYPE_NAME, this.portTypeName);
			put(JSONKEY_COMPONENT, this.component);
			if (this.isArray) {
				put(JSONKEY_PORTINDEX,this.portIndex);
			}
		} catch (JSONException e) {
			TitanTriLogger.error(e.getMessage());
			e.printStackTrace();
		}
		return super.toString();
	}
}
