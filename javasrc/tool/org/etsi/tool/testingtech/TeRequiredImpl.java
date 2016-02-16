package org.etsi.tool.testingtech;

import org.etsi.adapter.ITERequired;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriAddress;
import org.etsi.ttcn.tri.TriCommunicationTE;
import org.etsi.ttcn.tri.TriMessage;
import org.etsi.ttcn.tri.TriStatus;

import com.testingtech.ttcn.logging.RTLoggingConstants;
import com.testingtech.ttcn.tri.PluginIdentifier;
import com.testingtech.ttcn.tri.TAParameterIdImpl;
import com.testingtech.ttcn.tri.TAParameterServer;
import com.testingtech.ttcn.tri.TriAddressImpl;
import com.testingtech.ttcn.tri.TriMessageImpl;
import com.testingtech.ttcn.tri.TriStatusImpl;

import de.tu_berlin.cs.uebb.muttcn.runtime.RB;

public class TeRequiredImpl implements ITERequired {

	private RB RB;
	private final PluginIdentifier pluginId;

	public TeRequiredImpl(RB rB, PluginIdentifier pluginId) {
		RB = rB;
		this.pluginId = pluginId;
	}

	@Override
	public TriCommunicationTE getCommunicationTE() {
		return RB.getTriCommunicationTE();
	}

	@Override
	public Value getTaParameter(String param) {
		if (RB.TestAdapter instanceof TAParameterServer) {
			TAParameterServer paramServer = (TAParameterServer)RB.TestAdapter;
			return paramServer.getTAParameter(pluginId, TAParameterIdImpl.valueOf(param));
		}
		RB.tciTMProvided.tciError("Could not retrieve TA parameter: " + param);
		return null;
	}

	@Override
	public TriAddress getTriAddress(byte[] message) {
		return new TriAddressImpl(message);
	}

	@Override
	public TriMessage getTriMessage(byte[] message) {
		return new TriMessageImpl(message); 
	}

	@Override
	public TriStatus getTriStatus(int statusCode) {
		return new TriStatusImpl(statusCode);
	}

	@SuppressWarnings("deprecation")
    @Override
	public TriStatus getTriStatus(int statusCode, String message) {
		switch(statusCode) {
		case TriStatus.TRI_OK:
			RB.getTciTLProvidedV321TT().tliRT("", System.nanoTime(), "", -1, null, RTLoggingConstants.RT_LOG_INFO, message);
			return  new TriStatusImpl();
		default:
			return  new TriStatusImpl(message);
		}
		
	}
}
