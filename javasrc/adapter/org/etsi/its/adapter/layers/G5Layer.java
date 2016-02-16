/**
 *  Implementation of ITS G5 layer
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/G5Layer.java $
 *                $Id: G5Layer.java 421 2011-04-21 09:39:17Z berge $
 *
 */
package org.etsi.its.adapter.layers;

import java.util.Stack;

import org.etsi.its.adapter.IManagementLayers;

/**
 *  Implementation of ITS G5 layer
 */
public class G5Layer extends Layer {

    /**
     * Constructor
     * @param  management   Layer management instance
     * @param  lowerStack   Lower protocol stack   
     */
	public G5Layer(IManagementLayers management, Stack<String> lowerStack) {
		super(management, lowerStack);
	}

}
