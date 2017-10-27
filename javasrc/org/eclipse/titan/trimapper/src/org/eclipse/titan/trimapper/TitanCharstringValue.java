/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

public class TitanCharstringValue implements CharstringValue {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String string;
	
	public TitanCharstringValue(String value) {
		this.string = value;
	}
	
	@Override
	public boolean equals(Value v) {
		if (v instanceof TitanCharstringValue) {
			return ((TitanCharstringValue)v).getString().equals(this.string);
		} else {
			return false;
		}
	}
	
	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
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
	public boolean notPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public char getChar(int position) {
		return this.string.charAt(position);
	}

	@Override
	public int getLength() {
		return this.string.length();
	}

	@Override
	public String getString() {
		return this.string;
	}

	@Override
	public void setChar(int position, char newChar) {
		String tmp = this.string.substring(0, position-1);
		tmp += String.valueOf(newChar);
		tmp += this.string.substring(position);
		this.string = tmp;
	}

	@Override
	public void setLength(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setString(String string) {
		this.string = string;
	}

}
