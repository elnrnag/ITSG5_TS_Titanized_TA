/**
 *  Abstract class for packet source pseudo layers. It forwards a message to upper layer every second
 *  SHALL NOT BE INCLUDED IN RELEASE
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/test/SourceLayer.java $
 *              $Id: SourceLayer.java 633 2011-08-17 10:02:33Z berge $
 *
 */
package org.etsi.its.adapter.layers.test;

import java.util.HashMap;
import java.util.Stack;

import org.etsi.its.adapter.IManagementLayers;
import org.etsi.its.adapter.layers.Layer;

/**
 *  Abstract class for packet source pseudo layers. It forwards a message to upper layer every second
 */
public abstract class SourceLayer extends Layer  implements Runnable {

	/**
	 * Default interval for sending messages
	 */
	protected static final long SOURCE_INTERVAL = 1000;
	
	/**
     * Constructor
     * @param  management   Layer management instance
     * @param  lowerStack   Lower protocol stack   
     */
	public SourceLayer(IManagementLayers management, Stack<String> lowerStack) {
		super(management, lowerStack);
	}

	/**
     * Thread function for sending periodic messages
     * @see java.lang.Runnable#run()
     */
	@Override
	public void run() {
		while(running) {
			super.receive(message, new HashMap<String, Object>());
			try {
				Thread.sleep(SOURCE_INTERVAL); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#register(org.etsi.its.adapter.layers.Layer)
	 */
	@Override
	public void register(Layer upperLayer) {
		running = true;
		sourceThread = new Thread(this);
		sourceThread.start();
		super.register(upperLayer);
	}
	
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#unregister(org.etsi.its.adapter.layers.Layer)
	 */
	@Override
	public void unregister(Layer upperLayer) {
		if(running) {
			running = false;
			try {
				sourceThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.unregister(upperLayer);
	}

	/**
     * Indicates whether the source is still active. Setting this field to false will cause
     * the sending thread to stop its execution.
     */
	protected boolean running;

	/**
     * Sending thread instance.
     */
	private Thread sourceThread;
		
	/**
	 * Message to be sent periodically
	 */
	protected byte[] message = "DUMMY".getBytes();
}
