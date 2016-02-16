package org.etsi.its.tool.testingtech;

import org.etsi.adapter.TERFactory;
import org.etsi.its.adapter.TestAdapter;
import org.etsi.tool.testingtech.TeRequiredImpl;
import org.etsi.ttcn.tri.TriActionTemplate;
import org.etsi.ttcn.tri.TriAddress;
import org.etsi.ttcn.tri.TriAddressList;
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

import com.testingtech.ttcn.tri.ISAPlugin;
import com.testingtech.ttcn.tri.PluginIdentifier;
import com.testingtech.ttcn.tri.PluginIdentifierContainer;
import com.testingtech.ttcn.tri.TriStatusImpl;
import com.testingtech.ttcn.tri.extension.PortPluginProvider;

import de.tu_berlin.cs.uebb.muttcn.runtime.RB;

@SuppressWarnings("deprecation")
public class PluginAdapter implements ISAPlugin, PortPluginProvider, PluginIdentifierContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3032642487407372318L;
	
	/**
	 * Instance of tool independent test adapter implementation 
	 */
	protected TestAdapter ta;
	
	/**
	 * TTwb runtime reference 
	 */
	protected RB rb;
	
	/**
	 * TTwb plugin identifier 
	 */
	protected PluginIdentifier pluginId = new PluginIdentifier("org.etsi.its.testingtech.port");
	
	@Override
	public ISAPlugin getPortPlugin() {
		return this;
	}

	@Override
	public void setPluginIdentifier(PluginIdentifier pluginId) {
		this.pluginId  = pluginId;
	}

	@Override
	public void setRB(RB rb) {
		this.rb = rb;
		TERFactory.setImpl(new TeRequiredImpl(this.rb, pluginId));
		ta = new TestAdapter();
	}

	@Override
	public TriStatus setUp() {
		return new TriStatusImpl();
	}

	@Override
	public TriStatus tearDown() {
		return new TriStatusImpl();
	}

	@Override
	public TriStatus triCall(TriComponentId componentId, TriPortId tsiPortId,
			TriAddress sutAddress, TriSignatureId signatureId,
			TriParameterList parameterList) {
		return ta.triCall(componentId, tsiPortId, sutAddress, signatureId, parameterList);
	}

	@Override
	public TriStatus triCallBC(TriComponentId componentId, TriPortId tsiPortId,
			TriSignatureId signatureId, TriParameterList parameterList) {
		return ta.triCallBC(componentId, tsiPortId, signatureId, parameterList);
	}

	@Override
	public TriStatus triCallMC(TriComponentId componentId, TriPortId tsiPortId,
			TriAddressList sutAddresses, TriSignatureId signatureId,
			TriParameterList parameterList) {
		return ta.triCallMC(componentId, tsiPortId, sutAddresses, signatureId, parameterList);
	}

	@Override
	public TriStatus triEndTestCase() {
		return ta.triEndTestCase();
	}

	@Override
	public TriStatus triExecuteTestcase(TriTestCaseId testCaseId,
			TriPortIdList tsiPorts) {
		return ta.triExecuteTestcase(testCaseId, tsiPorts);
	}

	@Override
	public TriStatus triMap(TriPortId compPortId, TriPortId tsiPortId) {
		return ta.triMap(compPortId, tsiPortId);
	}

	@Override
	public TriStatus triMapParam(TriPortId compPortId, TriPortId tsiPortId,
			TriParameterList paramList) {
		return ta.triMapParam(compPortId, tsiPortId, paramList);
	}

	@Override
	public TriStatus triRaise(TriComponentId componentId, TriPortId tsiPortId,
			TriAddress sutAddress, TriSignatureId signatureId,
			TriException exception) {
		return ta.triRaise(componentId, tsiPortId, sutAddress, signatureId, exception);
	}

	@Override
	public TriStatus triRaiseBC(TriComponentId componentId,
			TriPortId tsitPortId, TriSignatureId signatureId, TriException exc) {
		return ta.triRaiseBC(componentId, tsitPortId, signatureId, exc);
	}

	@Override
	public TriStatus triRaiseMC(TriComponentId componentId,
			TriPortId tsitPortId, TriAddressList sutAddresses,
			TriSignatureId signatureId, TriException exc) {
		return ta.triRaiseMC(componentId, tsitPortId, sutAddresses, signatureId, exc);
	}

	@Override
	public TriStatus triReply(TriComponentId componentId, TriPortId tsiPortId,
			TriAddress sutAddress, TriSignatureId signatureId,
			TriParameterList parameterList, TriParameter returnValue) {
		return ta.triReply(componentId, tsiPortId, sutAddress, signatureId, parameterList, returnValue);
	}

	@Override
	public TriStatus triReplyBC(TriComponentId componentId,
			TriPortId tsiPortId, TriSignatureId signatureId,
			TriParameterList parameterList, TriParameter returnValue) {
		return ta.triReplyBC(componentId, tsiPortId, signatureId, parameterList, returnValue);
	}

	@Override
	public TriStatus triReplyMC(TriComponentId componentId,
			TriPortId tsiPortId, TriAddressList sutAddresses,
			TriSignatureId signatureId, TriParameterList parameterList,
			TriParameter returnValue) {
		return ta.triReplyMC(componentId, tsiPortId, sutAddresses, signatureId, parameterList, returnValue);
	}

	@Override
	public TriStatus triSAReset() {
		return ta.triSAReset();
	}

	@Override
	public TriStatus triSend(TriComponentId componentId, TriPortId tsiPortId,
			TriAddress address, TriMessage sendMessage) {
		return ta.triSend(componentId, tsiPortId, address, sendMessage);
	}

	@Override
	public TriStatus triSendBC(TriComponentId componentId, TriPortId tsiPortId,
			TriMessage sendMessage) {
		return ta.triSendBC(componentId, tsiPortId, sendMessage);
	}

	@Override
	public TriStatus triSendMC(TriComponentId componentId, TriPortId tsiPortId,
			TriAddressList addresses, TriMessage sendMessage) {
		return ta.triSendMC(componentId, tsiPortId, addresses, sendMessage);
	}

	@Override
	public TriStatus triSutActionInformal(String description) {
		return ta.triSutActionInformal(description);
	}

	@Override
	public TriStatus triSutActionTemplate(TriActionTemplate templateValue) {
		return ta.triSutActionTemplate(templateValue);
	}

	@Override
	public TriStatus triUnmap(TriPortId compPortId, TriPortId tsiPortId) {
		return ta.triUnmap(compPortId, tsiPortId);
	}

	@Override
	public TriStatus triUnmapParam(TriPortId compPortId, TriPortId tsiPortId,
			TriParameterList paramList) {
		return ta.triUnmapParam(compPortId, tsiPortId, paramList);
	}

}
