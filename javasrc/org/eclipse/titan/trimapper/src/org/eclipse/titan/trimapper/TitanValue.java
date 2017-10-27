/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class TitanValue implements Value {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Type type;
	private boolean notPresent;
	
	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean notPresent() {
		return notPresent;
	}

	@Override
	public String getValueEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValueEncodingVariant() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Value value) {
		// TODO Auto-generated method stub
		return false;
	}

}
