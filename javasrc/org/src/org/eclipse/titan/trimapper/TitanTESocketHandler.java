/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;

public class TitanTESocketHandler extends Thread {
	private TitanPortHandler portHandler;
	private DataInputStream in;

	public TitanTESocketHandler(TitanPortHandler portHandler,DataInputStream in) {
		this.portHandler = portHandler;
		this.in = in;
	}

	@Override
	public void run() {
		byte[] tpktHdr = new byte[TPKTHeader.TPKT_HDR_LENGTH];
		boolean stop = false;
		while (!stop) {
			try {
				in.read(tpktHdr, 0, TPKTHeader.TPKT_HDR_LENGTH);
				byte[] southboundMessage = new byte[TPKTHeader.getPayloadLength(tpktHdr)]; 
				in.read(southboundMessage, 0, southboundMessage.length);
				TitanMessage msg = new TitanMessage(TitanMessage.MsgType.SOUTHBOUND_MESSAGE);
				msg.setFromTE(southboundMessage);
				portHandler.getQueue().put(msg);
			} catch (InterruptedException ie) {
				TitanTriLogger.error("PortHandler queue error: "+ ie.getMessage());
				ie.printStackTrace();
			} catch (ClosedByInterruptException cbie) {
				stop = true;
				TitanTriLogger.info("TE Socket handler exititng...");
				TitanMessage msg = new TitanMessage(TitanMessage.MsgType.QUIT);
				try {
					portHandler.getQueue().put(msg);
				} catch (InterruptedException ie) {
					
				}
			} catch (IOException ioe) {
				stop = true;
				TitanTriLogger.info("TE incoming socket closed");
				TitanMessage msg = new TitanMessage(TitanMessage.MsgType.QUIT);
				try {
					portHandler.getQueue().put(msg);
				} catch (InterruptedException ie) {
					
				}
			}
		}
	}
}
