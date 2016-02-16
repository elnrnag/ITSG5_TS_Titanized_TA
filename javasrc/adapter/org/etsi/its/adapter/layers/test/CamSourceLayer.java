/**
 *  CAM source pseudo layer. It forwards a CAM message to upper layer every second
 *  SHALL NOT BE INCLUDED IN RELEASE
 *                
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/test/CamSourceLayer.java $
 *              $Id: CamSourceLayer.java 422 2011-04-21 11:33:57Z berge $
 *
 */
package org.etsi.its.adapter.layers.test;

import java.util.Stack;

import org.etsi.its.adapter.IManagementLayers;

/**
 *  CAM source pseudo layer. It forwards a CAM message to upper layer every second
 */
public class CamSourceLayer extends SourceLayer {

    /**
     * Constructor
     * @param  management   Layer management instance
     * @param  lowerStack   Lower protocol stack   
     */
	public CamSourceLayer(IManagementLayers management, Stack<String> lowerStack) {
		super(management, lowerStack);
		//message = new byte[]{};
	}

}
