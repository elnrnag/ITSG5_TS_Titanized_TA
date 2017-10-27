/******************************************************************************
 * Copyright (c) 2000-2015 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.titan.trimapper;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author elnrnag
 *
 */
public class TitanTriMapperMain {

	private static final String HELP_FORMAT_STRING = "%-25s";
	private static final String[][] HELP_STRINGS =
		{{"----------------------------------------------------------",""},
		 {"Usage:", ""},
		 {"java -jar ./TitanTriMapper.jar [[-p <portnum>] [-ta <TestAdapter>] | [-h]]",""},
		 {"",""},
		 
		 {"-p <portnum>", "Port number to use (default = 2222)"},
		 
		 {"-l <loglevel>", "[error,info,all]"},
		 
		 {"-c <filename>", "TA config file (if it does not exist it will be"},
		 {"","created with default settings)"},
		 {"","(default: ./taproperties.cfg)"},
		 
		 {"-h | --help", "Display this help message"},
		 {"----------------------------------------------------------",""},
		};
	
	private static final String ERROR_INSTANTIATION_FAILED = 
			"Error: Unable to instantiate class";
	private static final String ERROR_ILLEGAL_ACCESS = 
			"Error: Illegal access of class";
	private static final String ERROR_CLASS_NOT_FOUND = 
			"Error: Class not found";
	
	/**
	 * Instantiates and runs the TRI test adapter mapper for Titan
	 * @param -p | --port <port#>
	 * @param -h | --help Display help string 
	 */
	public static void main(String[] args) {
		ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();
        System.out.println("Classpath:");
        for(URL url: urls){
        	System.out.println(url.getFile());
        }
		
		
		
		
		int port       = 2222;
		//String TAClass = "org.etsi.its.adapter.TestAdapter";
		//boolean fakeAdapter = false;
		String TAConfigFile = "./taproperties.cfg";
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-p")) {
				port = Integer.valueOf(args[++i]);
			} else if (args[i].equals("-h") || args[i].equals("--help")) {
				displayHelp();
				return;
			} else if (args[i].equals("-l")) {
				String loglev = args[++i];
				TitanTriLogger.setLoglevel(loglev);
				
			} else if (args[i].equals("-c")) {
				TAConfigFile = args[++i];
			//} else if (args[i].equals("--fake")) {
			//	fakeAdapter = true;
				//TAClass = "org.eclipse.titan.trimapper.FakeTestAdapter";
			} else {
				displayHelp();
				return;
			}
		}
		
		try {
			System.out.println("Starting TitanTriMapper...");
			TitanItsTriMapper mapper = new TitanItsTriMapper(port, TAConfigFile);
			if (mapper != null) {
				int rc = mapper.run();
				if (rc == 0) {
					System.out.println("TitanTriMapper finished OK");
				} else {
					System.out.println("TitanTriMapper finished with error(s)!");
				}
			}
			
		} catch (InstantiationException ie) {
			System.out.println(String.format(ERROR_INSTANTIATION_FAILED));
		} catch (IllegalAccessException iae) {
			System.out.println(String.format(ERROR_ILLEGAL_ACCESS));
		} catch (ClassNotFoundException cnfe) {
			System.out.println(String.format(ERROR_CLASS_NOT_FOUND));
		}
		
	}

	private static void displayHelp() {
		for (int i = 0; i<HELP_STRINGS.length;i++) {
			String a = HELP_STRINGS[i][0];
			String b = HELP_STRINGS[i][1];
			String helpline = String.format(HELP_FORMAT_STRING, a) + b;
			System.out.println(helpline);
		}
		
	}

}
