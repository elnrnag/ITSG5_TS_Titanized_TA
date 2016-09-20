/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import org.etsi.adapter.ITERequired;
import org.etsi.adapter.TERFactory;
import org.etsi.ttcn.tri.TriActionTemplate;
import org.etsi.ttcn.tri.TriAddress;
import org.etsi.ttcn.tri.TriAddressList;
import org.etsi.ttcn.tri.TriCommunicationSA;
import org.etsi.ttcn.tri.TriCommunicationTE;
import org.etsi.ttcn.tri.TriComponentId;
import org.etsi.ttcn.tri.TriException;
import org.etsi.ttcn.tri.TriMessage;
import org.etsi.ttcn.tri.TriParameter;
import org.etsi.ttcn.tri.TriParameterList;
import org.etsi.ttcn.tri.TriPortId;
import org.etsi.ttcn.tri.TriPortIdList;
import org.etsi.ttcn.tri.TriSignatureId;
import org.etsi.ttcn.tri.TriStatus;
import org.etsi.ttcn.tri.TriTestCaseId;

public class FakeTestAdapter implements TriCommunicationSA {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ITERequired ITEreq;
	private TriStatus statusOK;
	private TriCommunicationTE TEimpl;
	
	
	private static String STATUS_STRING_OK = "FakeTestAdapter is happy!";
	
	public FakeTestAdapter() {
		ITEreq = TERFactory.getInstance();
		TEimpl = ITEreq.getCommunicationTE();
		statusOK = ITEreq.getTriStatus(TriStatus.TRI_OK, STATUS_STRING_OK);
	}
	
	@Override
	public TriStatus triSAReset() {
		return statusOK;
	}

	@Override
	public TriStatus triExecuteTestcase(TriTestCaseId testCaseId, TriPortIdList tsiPorts) {
		return statusOK;
	}

	@Override
	public TriStatus triMap(TriPortId compPortId, TriPortId tsiPortId) {
		TitanTriLogger.info("triMap called");
		return statusOK;
	}

	@Override
	public TriStatus triMapParam(TriPortId compPortId, TriPortId tsiPortId, TriParameterList paramList) {
		TitanTriLogger.info("triMapParam called");
		return statusOK;
	}

	@Override
	public TriStatus triUnmap(TriPortId compPortId, TriPortId tsiPortId) {
		TitanTriLogger.info("triUnmap called");
		return statusOK;
	}

	@Override
	public TriStatus triUnmapParam(TriPortId compPortId, TriPortId tsiPortId, TriParameterList paramList) {
		TitanTriLogger.info("triUnmapParam called");
		return statusOK;
	}

	@Override
	public TriStatus triEndTestCase() {
		return statusOK;
	}

	@Override
	public TriStatus triSend(TriComponentId componentId, TriPortId tsiPortId, TriAddress address,
			TriMessage sendMessage) {
		TitanTriLogger.info("triSend called");
		TEimpl.triEnqueueMsg(tsiPortId, address, componentId, ITEreq.getTriMessage(sendMessage.getEncodedMessage()));
		
		return statusOK;
	}

	@Override
	public TriStatus triSendBC(TriComponentId componentId, TriPortId tsiPortId, TriMessage sendMessage) {
		return statusOK;
	}

	@Override
	public TriStatus triSendMC(TriComponentId componentId, TriPortId tsiPortId, TriAddressList addresses,
			TriMessage sendMessage) {
		return statusOK;
	}

	@Override
	public TriStatus triCall(TriComponentId componentId, TriPortId tsiPortId, TriAddress sutAddress,
			TriSignatureId signatureId, TriParameterList parameterList) {
		return statusOK;
	}

	@Override
	public TriStatus triCallBC(TriComponentId componentId, TriPortId tsiPortId, TriSignatureId signatureId,
			TriParameterList parameterList) {
		return statusOK;
	}

	@Override
	public TriStatus triCallMC(TriComponentId componentId, TriPortId tsiPortId, TriAddressList sutAddresses,
			TriSignatureId signatureId, TriParameterList parameterList) {
		return statusOK;
	}

	@Override
	public TriStatus triReply(TriComponentId componentId, TriPortId tsiPortId, TriAddress sutAddress,
			TriSignatureId signatureId, TriParameterList parameterList, TriParameter returnValue) {
		return statusOK;
	}

	@Override
	public TriStatus triReplyBC(TriComponentId componentId, TriPortId tsiPortId, TriSignatureId signatureId,
			TriParameterList parameterList, TriParameter returnValue) {
		return statusOK;
	}

	@Override
	public TriStatus triReplyMC(TriComponentId componentId, TriPortId tsiPortId, TriAddressList sutAddresses,
			TriSignatureId signatureId, TriParameterList parameterList, TriParameter returnValue) {
		return statusOK;
	}

	@Override
	public TriStatus triRaise(TriComponentId componentId, TriPortId tsiPortId, TriAddress sutAddress,
			TriSignatureId signatureId, TriException exception) {
		return statusOK;
	}

	@Override
	public TriStatus triRaiseBC(TriComponentId componentId, TriPortId tsiPortId, TriSignatureId signatureId,
			TriException exc) {
		return statusOK;
	}

	@Override
	public TriStatus triRaiseMC(TriComponentId componentId, TriPortId tsiPortId, TriAddressList sutAddresses,
			TriSignatureId signatureId, TriException exc) {
		return statusOK;
	}

	@Override
	public TriStatus triSutActionInformal(String description) {
		return statusOK;
	}

	@Override
	public TriStatus triSutActionTemplate(TriActionTemplate templateValue) {
		return statusOK;
	}

}
