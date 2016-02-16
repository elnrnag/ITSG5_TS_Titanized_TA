/**
 * @author	STF 424_ITS_Test_Platform
 * @version    $URL$
 *             $Id$
 */
package org.etsi.codec;

import org.etsi.ttcn.tci.TciCDProvided;

/**
 *  TCI Required interface to be implemented by TCI providers.
 */
public interface ITCIRequired {

	/**
	 * Get a codec instance according the the provided codec name
	 * @param encodingName Name of the codec to get
	 * @return A codec instance
	 */
	public TciCDProvided getCodec(final String encodingName);
}
