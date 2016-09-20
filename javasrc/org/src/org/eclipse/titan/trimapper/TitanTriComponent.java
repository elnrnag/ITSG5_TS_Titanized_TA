/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import org.etsi.ttcn.tri.TriComponentId;
import org.etsi.ttcn.tri.TriPortIdList;
import org.json.JSONException;
import org.json.JSONObject;

public class TitanTriComponent extends JSONObject implements TriComponentId {
	
	private static final long serialVersionUID = 1L;
	
	private String componentId; 
	private String componentName;
	private String componentTypeName;
		
	private static String JSONKEY_COMPONENTID         = "componentId";
	private static String JSONKEY_COMPONENT_NAME      = "componentName";
	private static String JSONKEY_COMPONENT_TYPE_NAME = "componentTypeName";
	
	public TitanTriComponent(String componentId, String componentName, String componentTypeName) {
		this.componentId       = componentId;
		this.componentName     = componentName;
		this.componentTypeName = componentTypeName;
	}
	
	public TitanTriComponent(JSONObject jo) throws JSONException {
		componentId       = jo.getString(JSONKEY_COMPONENTID);
		componentName     = jo.getString(JSONKEY_COMPONENT_NAME);
		componentTypeName = jo.getString(JSONKEY_COMPONENT_TYPE_NAME);
	}
	
	public TitanTriComponent(TriComponentId triCompId) {
		this.componentId       = triCompId.getComponentId();
		this.componentName     = triCompId.getComponentName();
		this.componentTypeName = triCompId.getComponentTypeName();
	}
	
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
	
	@Override
	public String getComponentId() {
		return componentId;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	@Override
	public String getComponentName() {
		return componentName;
	}

	public void setComponentTypeName(String componentTypeName) {
		this.componentTypeName = componentTypeName;
	}
	
	@Override
	public String getComponentTypeName() {
		return componentTypeName;
	}

	/**
	 * This function always returns null. 
	 * In the RFC only the java interface contains this list, 
	 * hence it is considered an error in the RFC
	 **/
	@Override
	public TriPortIdList getPortList() {
		return null;
	}

	@Override
	public boolean equals(TriComponentId component) {
		return componentId.equals(component.getComponentId());
	}

	@Override
	public String toString() {
		try {
			put(JSONKEY_COMPONENTID, componentId);
			put(JSONKEY_COMPONENT_NAME, this.componentName);
			put(JSONKEY_COMPONENT_TYPE_NAME, this.componentTypeName);
		} catch (JSONException e) {
			TitanTriLogger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return super.toString();
	}
}
