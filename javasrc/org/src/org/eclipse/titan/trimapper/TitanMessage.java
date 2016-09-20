/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import java.io.Serializable;

import org.etsi.ttcn.tri.TriAddress;
import org.etsi.ttcn.tri.TriException;
import org.etsi.ttcn.tri.TriMessage;
import org.etsi.ttcn.tri.TriParameter;
import org.etsi.ttcn.tri.TriParameterList;
import org.etsi.ttcn.tri.TriSignatureId;
import org.etsi.ttcn.tri.TriStatus;

public class TitanMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static enum MsgType { 
					ENQUEUE_MSG,
					ENQUEUE_CALL,
					ENQUEUE_REPLY,
					ENQUEUE_EXCEPTION,
					QUIT,
					SEND_ERROR,
					SOUTHBOUND_MESSAGE
							   }
	
	private MsgType type;
	
	private TriMessage       receivedMessage;
	private TriSignatureId   signatureId;
	private TriParameterList parameterList;
	private TriParameter     returnValue;
	private TriException     exception;
	private TriStatus        triStatus;
	private TriAddress       triAddress;
	private byte[]           fromTE;
	
	public TitanMessage(MsgType type) {
		this.type = type;
	}
	
	public TriMessage getReceivedMessage() {
		return receivedMessage;
	}
	
	public void setReceivedMessage(TriMessage receivedMessage) {
		this.receivedMessage = receivedMessage;
	}
	
	public TriSignatureId getTriSignatureId() {
		return signatureId;
	}
	
	public void setTriSignatureId(TriSignatureId triSignatureId) {
		this.signatureId = triSignatureId;
	}
	
	public TriParameterList getTriParameterList() {
		return parameterList;
	}
	
	public void setTriParameterList(TriParameterList triParameterList) {
		this.parameterList = triParameterList;
	}
	
	public TriParameter getReturnValue() {
		return returnValue;
	}
	
	public void setReturnValue(TriParameter returnValue) {
		this.returnValue = returnValue;
	}
	
	public TriException getException() {
		return exception;
	}

	public void setException(TriException exception) {
		this.exception = exception;
	}

	public MsgType getType() {
		return type;
	}

	public TriStatus getTriStatus() {
		return triStatus;
	}

	public void setTriStatus(TriStatus triStatus) {
		this.triStatus = triStatus;
	}

	public TriAddress getTriAddress() {
		return triAddress;
	}

	public void setTriAddress(TriAddress triAddress) {
		this.triAddress = triAddress;
	}

	public byte[] getFromTE() {
		return fromTE;
	}

	public void setFromTE(byte[] fromTE) {
		this.fromTE = fromTE;
	}
	

}
