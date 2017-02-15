/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.etsi.adapter.ITERequired;
import org.etsi.adapter.TERFactory;
import org.etsi.its.adapter.TestAdapter;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriAddress;
import org.etsi.ttcn.tri.TriCommunicationSA;
import org.etsi.ttcn.tri.TriCommunicationTE;
import org.etsi.ttcn.tri.TriComponentId;
import org.etsi.ttcn.tri.TriException;
import org.etsi.ttcn.tri.TriMessage;
import org.etsi.ttcn.tri.TriParameter;
import org.etsi.ttcn.tri.TriParameterList;
import org.etsi.ttcn.tri.TriPortId;
import org.etsi.ttcn.tri.TriSignatureId;
import org.etsi.ttcn.tri.TriStatus;
import org.json.JSONException;
import org.json.JSONObject;

public final class TitanItsTriMapper implements ITERequired, TriCommunicationTE {
	
	public static String ERROR_STR_NOT_IMPLEMENTED = 
			"Not Implemented";
	
	private static String TIMER_THREADS_MONITOR = "threads_monitor";
	
	private static int THREADS_MONITORING_INTERVAL_MILLIS = 1000;
	
	private static boolean stop = false;
	
	private int port;
	public TriCommunicationSA testAdapter;
	
	private ConcurrentHashMap<TriPortId,TitanPortHandler> tsiPortIdToPortHandler = 
			new ConcurrentHashMap<TriPortId,TitanPortHandler>();
	private ConcurrentHashMap<TitanPortHandler,TriPortId> portHandlerToTsiPortHandler = 
			new ConcurrentHashMap<TitanPortHandler,TriPortId>();
	
	private JSONObject properties = new JSONObject();
	
	private class HandlerMonitor extends TimerTask {
		@Override
		public void run() {
			Iterator<TitanPortHandler> it = portHandlerToTsiPortHandler.keySet().iterator();
			while(it.hasNext()) {
				TitanPortHandler ph = it.next();
				if (!ph.isAlive()) {
					unregisterPortHandler(ph);
				}
			}
		}
	}
	
	public TitanItsTriMapper(int port, String taClass, String taConfigFile) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		this.port = port;
		readConfig(taConfigFile);
		TERFactory.setImpl(this);
		Class<?> TAClass = Class.forName(taClass);
		this.testAdapter = (TriCommunicationSA)TAClass.newInstance();
	}
	
	public TitanItsTriMapper(int port, String taConfigFile) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		this.port = port;
		readConfig(taConfigFile);
		TERFactory.setImpl(this);
		this.testAdapter = new TestAdapter();
	}
	
	
	private void readConfig(String taConfigFile) {
		
		File cfgFile = new File(taConfigFile);
		if (cfgFile.canRead()) {
		
			try (Scanner in = new Scanner(new FileReader(taConfigFile));) {
				StringBuilder sb = new StringBuilder();
				while (in.hasNextLine()) {
					sb.append(in.nextLine());
				}
				 
				this.properties = new JSONObject(sb.toString());
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			} catch (JSONException je) {
				je.printStackTrace();
			}
		} else {
			// No default config, create one
			createDefaultConfig(taConfigFile);
		}
		
	}

	private void createDefaultConfig(String taConfigFile) {
		
		JSONObject config = new JSONObject();
		try (FileOutputStream fos = new FileOutputStream(taConfigFile);
			 PrintWriter pw = new PrintWriter(fos);)
		{
			//Generic test adapter configuration parameters
			config.put("UpperTesterSettings", "192.168.56.129:1501 ");
			config.put("TsLatitude", "7000");
			config.put("TsLongitude", "520000");
			config.put("TsSecuredMode", "false");
			config.put("LocalEthernetMAC", "ecf4bb350b3e");
			config.put("IutEthernetTypeValue", "0x8947");
			
			//GeoNetworking test adapter configuration parameters
			config.put("geoNetworkingPort", "ETH");
			config.put("LinkLayer_MTC", "BABEBABE0000");
			config.put("LinkLayer_NodeA", "BABEBABE0001");
			config.put("LinkLayer_NodeB", "BABEBABE0002");
			config.put("LinkLayer_NodeC", "BABEBABE0003");
			config.put("LinkLayer_NodeD", "BABEBABE0004");
			config.put("LinkLayer_NodeE", "BABEBABE0005");
			config.put("LinkLayer_NodeF", "BABEBABE0006");
			config.put("TsBeaconInterval", "1000");
			
			//BTP test adapter configuration parameters
			config.put("btpPort", "GN/ETH");
			
			//CAM test adapter configuration parameters
			config.put("camPort", "BTP/GN/ETH");
			
			//DENM test adapter configuration parameters
			config.put("denmPort", "BTP/GN/ETH");
			
			
			//GN6 test adapter configuration parameters
			config.put("ipv6OverGeoNetworkingPort", "Debug");
			config.put("Gn6RemoteAdapterIp", "192.168.56.11");
			config.put("Gn6RemoteAdapterPort", "42000");
			
			//GN6 test adapter configuration parameters
			config.put("mapSpatPort", "BTP/GN/ETH");
			
			//Security test adapter configuration parameters
			config.put("UtSecuredMode", "false");
			config.put("TsSecuredMode", "false");
			config.put("TsSecuredRootPath", "data/certificates");
			config.put("TsSecuredConfiId", "vendorA");
			
			//Load to internal storage
			this.properties = config;
			
			//Save to file
			pw.println(config.toString(2));
			pw.flush();
			pw.close();			
			
		} catch (JSONException je) {
			je.printStackTrace();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public int run() {
		TitanTriLogger.info("Waiting for connections...");
		return waitForConnections();
	}
	
	private int waitForConnections() {
		int rc = 0;
		Timer t = new Timer(TIMER_THREADS_MONITOR, true);
		t.scheduleAtFixedRate(new HandlerMonitor(), 0, THREADS_MONITORING_INTERVAL_MILLIS);
		int i = 0;
		try (ServerSocket listener = new ServerSocket(port)){
			while (!stop) {
				TitanTriLogger.info("Wait iteration "+String.valueOf(i++));
				TitanTriLogger.info("Opening socket on port " + String.valueOf(port));
				Socket s = listener.accept();
				TitanTriLogger.info("Starting new handler thread...");
				TitanPortHandler ph = new TitanPortHandler(s, this);
				ph.start();
				portHandlerToTsiPortHandler.put(ph, getNullPortId());
				TitanTriLogger.info("Handler thread started");
			}
		} catch (IOException e) {
			TitanTriLogger.error("Communication error while waiting for new connections");
			rc = 1;
			e.printStackTrace();
			stop = true;
		} catch (NegativeArraySizeException nase) {
			rc = 1;
			TitanTriLogger.info(nase.getMessage());
			stop = true;
		}

		//Stopping, stop all handlers as well
		Iterator<TitanPortHandler> it = portHandlerToTsiPortHandler.keySet().iterator();
		while (it.hasNext()) {
			//it.next().
			LinkedBlockingQueue<TitanMessage> q = it.next().getQueue();
			q.clear();
			q.add(new TitanMessage(TitanMessage.MsgType.QUIT));
		}
		return rc;
	}
	
	public void registerPortHandler(TitanPortHandler ph, TriPortId tsiPortId) {
		this.portHandlerToTsiPortHandler.put(ph, tsiPortId);
		if (tsiPortId != null) {
			this.tsiPortIdToPortHandler.put(tsiPortId, ph);
		}
	}
	
	public void unregisterPortHandler(TitanPortHandler ph) {
		TriPortId tsiPortId = this.portHandlerToTsiPortHandler.remove(ph);
		if (tsiPortId != null) {
			this.tsiPortIdToPortHandler.remove(tsiPortId);
		}
	}
	
	public void unregisterTsiPortId(TriPortId tsiPortId) {
		unregisterTsiPortId(tsiPortId, true);
	}
	
	public void unregisterTsiPortId(TriPortId tsiPortId, boolean removePhFromDb) {
		TitanPortHandler ph = tsiPortIdToPortHandler.remove(tsiPortId);
		if (ph != null) {
			if (removePhFromDb) {
				portHandlerToTsiPortHandler.remove(ph);
			} else {
				portHandlerToTsiPortHandler.put(ph,getNullPortId());
			}
		}
	}
	
	private TitanTriPortId getNullPortId() {
		return new TitanTriPortId(null, null, null);
	}
	
	//ITERequired
	@Override
	public TriCommunicationTE getCommunicationTE() {
		return this;
	}

	@Override
	public TriStatus getTriStatus(int statusCode) {
		return new TitanTriStatus(statusCode);
	}

	@Override
	public TriStatus getTriStatus(int statusCode, String message) {
		return new TitanTriStatus(statusCode, message);
	}

	@Override
	public TriAddress getTriAddress(byte[] message) {
		return new TitanTriAddress(message);
	}

	@Override
	public TriMessage getTriMessage(byte[] message) {
		return new TitanTriMessage(message);
	}

	@Override
	public Value getTaParameter(String param) {
		String val = this.properties.optString(param);
		if (val != null) {
			TitanTriLogger.info("Supplying parameter "+param+" to TA: "+val);
		} else {
			TitanTriLogger.info("Supplying parameter "+param+" to TA: null");
		}
		return new TitanCharstringValue(val);
	}

	//TriCommunicationTE	
	@Override
	public void triEnqueueMsg(TriPortId tsiPortId, TriAddress sutAddress, TriComponentId componentId,
			TriMessage receivedMessage) {
		TitanMessage tmsg = new TitanMessage(TitanMessage.MsgType.ENQUEUE_MSG);
		tmsg.setReceivedMessage(receivedMessage);
		tmsg.setTriAddress(sutAddress);
		sendNorthboundMessage(tsiPortId, tmsg);
		
	}

	@Override
	public void triEnqueueCall(TriPortId tsiPortId, TriAddress SUTaddress, TriComponentId componentId,
			TriSignatureId signatureId, TriParameterList parameterList) {
		TitanMessage tmsg = new TitanMessage(TitanMessage.MsgType.ENQUEUE_CALL);
		tmsg.setTriSignatureId(signatureId);
		tmsg.setTriParameterList(parameterList);
		tmsg.setTriAddress(SUTaddress);
		sendNorthboundMessage(tsiPortId, tmsg);
		
	}

	@Override
	public void triEnqueueReply(TriPortId tsiPortId, TriAddress address, TriComponentId componentId,
			TriSignatureId signatureId, TriParameterList parameterList, TriParameter returnValue) {
		TitanMessage tmsg = new TitanMessage(TitanMessage.MsgType.ENQUEUE_REPLY);
		tmsg.setTriSignatureId(signatureId);
		tmsg.setTriParameterList(parameterList);
		tmsg.setReturnValue(returnValue);
		tmsg.setTriAddress(address);
		sendNorthboundMessage(tsiPortId, tmsg);
		
	}

	@Override
	public void triEnqueueException(TriPortId tsiPortId, TriAddress sutAddress, TriComponentId componentId,
			TriSignatureId signatureId, TriException exception) {
		TitanMessage tmsg = new TitanMessage(TitanMessage.MsgType.ENQUEUE_CALL);
		tmsg.setTriSignatureId(signatureId);
		tmsg.setException(exception);
		tmsg.setTriAddress(sutAddress);
		sendNorthboundMessage(tsiPortId, tmsg);
		
	}

	@Override
	public void triSAErrorReq(String message) {
		TitanTriLogger.info("NOT_IMPLEMENTED: Message received from SA: " + message);
	}

	private void sendNorthboundMessage(TriPortId tsiPortId, TitanMessage tmsg) {
		TitanPortHandler handler = tsiPortIdToPortHandler.get(tsiPortId);
		if (handler != null) {
			LinkedBlockingQueue<TitanMessage> q = handler.getQueue();
			if (q == null || !q.offer(tmsg)) {
				TitanTriLogger.error("Message sending to handler process failed");
			}
		} else { //No handler associated with the tsiPort
			TitanTriLogger.error("No handler is associated with the given port: "+tsiPortId.toString());
			//If there's no handler registered at all reset TA, there may be some stuck settings
			//if (tsiPortIdToPortHandler.isEmpty()) {
			this.testAdapter.triSAReset();
			//this.testAdapter = new TestAdapter();
				
			//}
		}
		
	}
	
	

}
