/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import org.etsi.ttcn.tri.TriAddress;
import org.etsi.ttcn.tri.TriMessage;
import org.etsi.ttcn.tri.TriStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class TitanPortHandler extends Thread {
	
	private boolean endOfLife = false;
	private Socket titanSocket;
	private TitanTESocketHandler teSocketHandler;
	private TitanItsTriMapper mapper;
	
	private LinkedBlockingQueue<TitanMessage> q;
	
	private TitanTriPortId portId;
	private TitanTriPortId tsiPortId;
	private String[] portTypeList;
	
	private static String JSONKEY_MSGID      = "msg_id";
	private static String JSONKEY_MSG        = "msg";
	
	private static String JSONKEY_SENDMSG    = "sendmsg";
	private static String JSONKEY_MAP        = "mapped";
	private static String JSONKEY_REGISTER   = "reg";
	private static String JSONKEY_UNMAP      = "unmapped";
	private static String JSONKEY_UNREGISTER = "unregister";
	
	private static String JSONKEY_PORTID     = "portid";
	private static String JSONKEY_TYPE_LIST  = "type_list";
	
	public static String JSONKEY_ADDR        = "addr";
	
	private static String ERROR_UNABLE_TO_DECODE = "Unable to decode message";
	private static String ERROR_KEY_NOT_FOUND = "Could not get msgId from message";
	
	public TitanPortHandler(Socket s, TitanItsTriMapper mapper) {
		titanSocket = s;
		this.mapper = mapper; 
		try {
			s.setKeepAlive(true);
			this.q = new LinkedBlockingQueue<TitanMessage>();
			TitanTriLogger.info(String.format("Incoming connection: %s:%d",s.getInetAddress().toString(),s.getPort()));
		} catch (SocketException se) {
			TitanTriLogger.error("TitanPortHandler: "+se.getMessage());
		}
	}
	
	@Override
	public void run() {
		try (DataInputStream in = 
				new DataInputStream(new BufferedInputStream(titanSocket.getInputStream()));
			 DataOutputStream out = 
				new DataOutputStream(new BufferedOutputStream(titanSocket.getOutputStream())))
			    
		{
			//Start TE socket handler
			teSocketHandler = new TitanTESocketHandler(this, in);
			teSocketHandler.start();
			
			while (!endOfLife) {
				TitanMessage msg = q.take();
				if (msg != null) {
					try {
						TitanTriLogger.info("Incoming message...");
						handleMessage(msg, out);
						TitanTriLogger.info("Message handled");
					} catch (JSONException je) {
						TitanTriLogger.error(je.getMessage());
						je.printStackTrace();
					}
				}
			}	
			TitanTriLogger.info("End of port handler's life");
		} catch (IOException ioe) {
			TitanTriLogger.error(ioe.getMessage());
			ioe.printStackTrace();
		} catch (Exception e) {
			TitanTriLogger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void sendToTE(JSONObject Message, DataOutputStream out) 
			throws IOException, UnsupportedEncodingException, JSONException {
		TitanTriLogger.info("Sending "+this.portId.getPortName()+" message to TE: ");
		TitanTriLogger.info(Message.toString());
		byte[] messageBytes = Message.toString().getBytes("UTF-8");
		short payloadLength = (short)messageBytes.length;
		if (payloadLength < messageBytes.length){
			TitanTriLogger.error("!!!!Message length truncated!!!!");
		}
		out.write(TPKTHeader.getHeaderAsByteArray(payloadLength));
		out.write(messageBytes);
		out.flush();
	}

	public LinkedBlockingQueue<TitanMessage> getQueue() {
		return this.q;
	}
	
	private void handleMessage(TitanMessage tmsg, DataOutputStream out) 
			throws IOException, JSONException {
		switch (tmsg.getType()) {
		//Handle TE -> TA (southbound) message
		case SOUTHBOUND_MESSAGE:
			try {
				TitanTriLogger.info("Incoming Southbound message...");
				TitanTriReply status = handleSouthboundMessage(tmsg.getFromTE());
				TitanTriLogger.info("Message handled, sending reply...");
				sendToTE(status, out);
			} catch (JSONException je) {
				TitanTriLogger.error(je.getMessage());
			}
			break;
		//Handle TA -> TE (northbound) message
		case ENQUEUE_MSG:
			TitanTriLogger.info("EnqueueMessage incoming");
			enqueueMessage(tmsg.getReceivedMessage(), tmsg.getTriAddress(), out);
			break;
		case ENQUEUE_CALL:
			
			break;
		case ENQUEUE_REPLY:
			break;
		case ENQUEUE_EXCEPTION:
			break;
		case QUIT:
			handleUnregister();
			break;
		default:
			TitanTriLogger.error("Bad message type from mapper: " + String.valueOf(tmsg.getType()));
		}
	}
	
	
	private void enqueueMessage(TriMessage receivedMessage, TriAddress address, DataOutputStream out)
			throws JSONException, IOException {
		TitanTriMessage msg = new TitanTriMessage(decodeMessage(receivedMessage.getEncodedMessage()));
		TitanEnqueueMessage msgEnqueue = new TitanEnqueueMessage(address, msg);
		TitanTriReply msgContainer = new TitanTriReply(1, msgEnqueue);
		TitanTriLogger.info("Sending message to TE: " + msgContainer.toString());
		sendToTE(msgContainer, out);
		
	}

	/**
	   * Skeleton for invoking the codecs for decoding the received data
	   * 
	   * @param The message received from the TA
	   * 
	   * @return The same byte[] as the input - 
	   * TODO: invoke the codec
	   */
	private byte[] decodeMessage(byte[] message) {
		return message;
	}
	
	
	
	private TitanTriReply handleSouthboundMessage(byte[] message) {
		String stringMsg; 
		try {
			stringMsg = new String(message, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			TitanTriLogger.error(ERROR_UNABLE_TO_DECODE);
			TitanTriLogger.error(new String(message));
			endOfLife = true;
			return new TitanTriReply(0, new TitanTriStatus(TriStatus.TRI_ERROR, ERROR_UNABLE_TO_DECODE));
		}
		TitanTriLogger.info("Message received: " + stringMsg);
		JSONObject fromTe;
		try {
			fromTe = new JSONObject(stringMsg);
		} catch (JSONException keyNotFound) {
			TitanTriLogger.error(ERROR_UNABLE_TO_DECODE);
			TitanTriLogger.error(stringMsg);
			endOfLife = true;
			return new TitanTriReply(0, new TitanTriStatus(TriStatus.TRI_ERROR, ERROR_UNABLE_TO_DECODE));
		}
		
		int msgId;
		try {
			msgId = fromTe.getInt(JSONKEY_MSGID);
		} catch (JSONException keyNotFound) {
			TitanTriLogger.error(ERROR_KEY_NOT_FOUND);
			return new TitanTriReply(0, new TitanTriStatus(TriStatus.TRI_ERROR, ERROR_KEY_NOT_FOUND));
		}
		
		TriStatus result;
		try {
			JSONObject jo = fromTe.getJSONObject(JSONKEY_MSG);
			if (jo.has(JSONKEY_SENDMSG)) {
				TitanTriLogger.info("Handling message sending...");
				result = handleSendMessage(jo.getJSONObject(JSONKEY_SENDMSG));
				TitanTriLogger.info("Message sent...");
			} else if (jo.has(JSONKEY_MAP)) {
				TitanTriLogger.info("Handling Map message...");
				result = handleMap(jo.getJSONObject(JSONKEY_MAP));
				TitanTriLogger.info("Mapping handled");
			} else if (jo.has(JSONKEY_UNMAP)) {
				result = handleUnmap();
			} else if (jo.has(JSONKEY_REGISTER)) {
				TitanTriLogger.info("Registering port");
				result = handleRegister(jo.getJSONObject(JSONKEY_REGISTER));
				TitanTriLogger.info("Port registered");
			} else if (jo.has(JSONKEY_UNREGISTER)) {
				result = handleUnregister();
			} else {
				TitanTriLogger.error("Unidentified object received from TE: ");
				TitanTriLogger.error(jo.toString());
				result = new TitanTriStatus(TriStatus.TRI_ERROR);
			}			
		} catch (JSONException je) {
			TitanTriLogger.error(je.getMessage());
			je.printStackTrace();
			result = new TitanTriStatus(TriStatus.TRI_ERROR);
		} catch (UnsupportedEncodingException uee) {
			TitanTriLogger.error(ERROR_UNABLE_TO_DECODE);
			TitanTriLogger.error(stringMsg);
			endOfLife = true;
			result = new TitanTriStatus(TriStatus.TRI_ERROR, ERROR_UNABLE_TO_DECODE);
		}
		
		TitanTriStatus titanTriStatus;
		if (result instanceof TitanTriStatus) {
			titanTriStatus = (TitanTriStatus)result;
		} else {
			titanTriStatus = new TitanTriStatus(result);
		}
		
		TitanTriReply reply = new TitanTriReply(msgId, titanTriStatus);
		return reply;
		
	}

	private TriStatus handleSendMessage(JSONObject jo) 
			throws JSONException, UnsupportedEncodingException {
		TitanTriAddress address = new TitanTriAddress(jo.optJSONObject(JSONKEY_ADDR));
		TitanTriMessage sendMessage = new TitanTriMessage(jo);
		if (sendMessage.getEncodedMessage() == null) {
			return new TitanTriStatus(TriStatus.TRI_ERROR, TitanItsTriMapper.ERROR_STR_NOT_IMPLEMENTED);
		} else {
			return mapper.testAdapter.triSend(this.portId.getComponent(), tsiPortId, address, sendMessage);
		}
	}

	private TriStatus handleRegister(JSONObject jo) throws JSONException {
		TitanTriLogger.info(jo.toString());
		//Get the TE PortId
		this.portId = new TitanTriPortId(jo.getJSONObject(JSONKEY_PORTID));
		
		//Get the type list, if any
		JSONArray jsonTypeList = jo.optJSONArray(JSONKEY_TYPE_LIST);
		if (jsonTypeList != null) {
			String[] types = new String[jsonTypeList.length()];
			for (int i = 0; i < jsonTypeList.length(); i++) {
				types[i] = jsonTypeList.getString(i);
			}
			this.portTypeList = types;
		}
		TitanTriLogger.info("Registration complete, sending reply...");
		return new TitanTriStatus(TriStatus.TRI_OK);
	}
	
	private TriStatus handleUnregister() {
		//Stop the incoming socket handler
		TitanTESocketHandler kill = this.teSocketHandler;
		this.teSocketHandler = null;
		kill.interrupt();
		
		this.endOfLife = true;
		mapper.unregisterPortHandler(this);
		return new TitanTriStatus(TriStatus.TRI_OK);
	}

	private TriStatus handleMap(JSONObject jo) throws JSONException {
		TitanTriLogger.info(jo.toString());
		tsiPortId = new TitanTriPortId(jo.getJSONObject(JSONKEY_PORTID));
		TitanTriLogger.info("Mapping port id "+tsiPortId.getPortName()+" to TSI "+portId.getPortName());
		mapper.registerPortHandler(this, tsiPortId);
		TitanTriLogger.info("Mapped port id: "+tsiPortId.getPortName()+" to TSI "+portId.getPortName());
		if (mapper.testAdapter == null) {
			TitanTriLogger.info("TestAdapter null");
		}
		return mapper.testAdapter.triMap(portId, tsiPortId);
	}

	private TriStatus handleUnmap() throws JSONException {
		TitanTriLogger.info("Unmapping "+tsiPortId.getPortName());
		TriStatus status = mapper.testAdapter.triUnmap(portId, tsiPortId);
		TitanTriLogger.info("Unmapping locally "+tsiPortId.getPortName());
		mapper.unregisterTsiPortId(tsiPortId, false);
		TitanTriLogger.info("Unmapped "+tsiPortId.getPortName());
		return status;
	}
	
	
}