/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author elnrnag
 *
 */
public class TitanTriLogger {
	private final static int LOGLEVEL_ERROR = 10;
	private final static int LOGLEVEL_INFO = 5;
	private final static int LOGLEVEL_ALL = 0;
	
	private final static String LOGLEVEL_STR_ERROR = "error";
	private final static String LOGLEVEL_STR_INFO = "info";
	private final static String LOGLEVEL_STR_ALL = "all";
	
	private static int loglevel = LOGLEVEL_ERROR;
	
	public static void info(String info){
		if (loglevel <= LOGLEVEL_INFO) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
			System.out.println("[INFO] ("+ sdf.format(new Date()) +") "+info);
		}
	}
	
	public static void error(String error){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		System.out.println("[ERROR] ("+ sdf.format(new Date()) +") "+error);
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		System.out.println("[STACKTRACE] ");
		for (int i = 0; i < stackTrace.length; i++) {
			System.out.println(stackTrace[i].toString());
		}
		System.out.println("[END STACKTRACE]");
	}
	
	public static void setLoglevel(String level) {
		if (level.equals(LOGLEVEL_STR_ALL)) {
			setLoglevel(LOGLEVEL_ALL);
		} else if (level.equals(LOGLEVEL_STR_INFO)) {
			setLoglevel(LOGLEVEL_INFO);
		} else if (level.equals(LOGLEVEL_STR_ERROR)) {
			setLoglevel(LOGLEVEL_ERROR);
		}
	}
	
	public static void setLoglevel(int level) {
		loglevel = level;
	}
	
	
	
	
}
