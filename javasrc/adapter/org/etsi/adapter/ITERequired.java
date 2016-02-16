/**
 *  Test Execution Required interface to be implemented by TE providers.
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/adapter/ITERequired.java $
 *              $Id: ITERequired.java 459 2011-04-28 13:14:45Z berge $
 *
 */
package org.etsi.adapter;

import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriAddress;
import org.etsi.ttcn.tri.TriCommunicationTE;
import org.etsi.ttcn.tri.TriMessage;
import org.etsi.ttcn.tri.TriStatus;

/**
 *  Test Execution Required interface to be implemented by TE providers.
 */
public interface ITERequired {

	/**
	 * Gets TriCommunicationTE implementation
	 * @return TriCommunicationTE implementation
	 */
	TriCommunicationTE getCommunicationTE();
	
	/**
	 * Creates TriStatus object corresponding to statusCode
	 * @param  statusCode  Status value
	 * @return TriStatus corresponding to statusCode
	 */
	TriStatus getTriStatus(int statusCode);
	
	/**
	 * Creates TriStatus object corresponding to statusCode (non TRI-standard)
	 * @param  statusCode  Status value
	 * @param  message     Informational message (Error cause, ...)
	 * @return TriStatus corresponding to statusCode
	 */
	TriStatus getTriStatus(int statusCode, String message); 
	
	/**
	 * Gets TriAddress
	 * @param  message
	 * @return TriAddress
	 */
	TriAddress getTriAddress(byte[] message);
	
	/**
	 * Gets TriMessage
	 * @param  message
	 * @return TriMessage
	 */
	TriMessage getTriMessage(byte[] message);
	
	/**
	 * Gets Test Adapter configuration parameter (non TRI-standard)
	 * @param  param   Name of the configuration parameter
	 * @return Value associated to the TA parameter
	 */
	Value getTaParameter(String param);
}
