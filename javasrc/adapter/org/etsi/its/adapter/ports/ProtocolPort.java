/**
 *  Abstract class for Protocol-related port implementations
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/ProtocolPort.java $
 *              $Id: ProtocolPort.java 1917 2015-01-12 13:08:04Z berge $
 *
 */
package org.etsi.its.adapter.ports;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.Management;
import org.etsi.its.adapter.layers.Layer;

/**
 *  Abstract class for Protocol-related port implementations
 */
public abstract class ProtocolPort extends Layer implements IPort, IObservable {

    /**
     * Constructor
     * @param   portName        Name of the port
     * @param   componentName   Name of the component owning this port instance
     * @param   lowerStackDesc  Description of the port's lower stack in the form "Layer/Layer/Layer/..."
     * @param  linkLayerAddress    Link-layer address to be used by this port as source address (null if not applicable)
     */
    public ProtocolPort(String portName, String componentName, String lowerStackDesc, String linkLayerAddress) {
        super(Management.getInstance(componentName), parseStackDesc(lowerStackDesc));
        this.portName = portName;
        this.componentName = componentName;
        Management.getInstance(componentName).setLinkLayerAddress(ByteHelper.hexStringToByteArray(linkLayerAddress));
        initialize();
    }
    
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.ports.IPort#getPortName()
	 */
	@Override
	public String getPortName() {
		return portName;
	}

	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.ports.IPort#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return componentName;
	}

	/**
	 * Parses a stack description in the form "Layer/Layer/Layer/..." 
	 * @param  lowerStackDesc  String representing a stack description
	 * @return Parsed stack description
	 */
	private static Stack<String> parseStackDesc(String lowerStackDesc) {
		Stack<String> lowerStack = new Stack<String>();
		String [] layers = lowerStackDesc.split("/");
		for(int i=layers.length-1; i >=0; i--) {
			if(!layers[i].equalsIgnoreCase("")) {
				lowerStack.push(layers[i]);
			}
		}
		return lowerStack;
	}

	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.ports.IPort#send(byte[])
	 */
	@Override
	public boolean send(byte[] message) {
		HashMap<String, Object> params = new HashMap<String, Object>();
        
//		ByteHelper.dump("ProtocolPortLayer.send", message);
		return send(message, params);
	}
	
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#receive(byte[])
	 */
	@Override
	public void receive(byte[] message, Map<String, Object> lowerInfo) {
		setChanged();
		notifyObservers(new PortEvent(message, portName, componentName));
	}
	
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.ports.IPort#dispose()
	 */
	@Override
	public void dispose() {
		unregister(null);
	}
	
    /**
     * Marks this Observable object as having been changed  
     */
	protected void setChanged() {
		observable.setChanged();
	}
	
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.ports.IObservable#notifyObservers(java.lang.Object)
	 */
	@Override 
	public void notifyObservers(Object arg) {
		observable.notifyObservers(arg);
	}
	
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.ports.IObservable#addObserver(java.util.Observer)
	 */
	@Override
	public void addObserver(Observer observer) {
		observable.addObserver(observer);
	}
	
	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.ports.IObservable#deleteObservers()
	 */
	@Override
	public void deleteObservers() {
		observable.deleteObservers();
	}

	/**
	 * Embedded object for implementing Observable behaviour
	 */
	private ObservablePort observable = new ObservablePort();

	/** 
	 * Name of the port
	 */
	private String portName;
	
	/** 
	 * Name of the component owner; needed for enqueueing message
	 */
	private String componentName;
	
	/**
	 * Nested class for implementing Observable behaviour
	 */
	private class ObservablePort extends Observable implements IObservable  {

		/* (non-Javadoc)
		 * @see java.util.Observable#setChanged()
		 */
		@Override
		public void setChanged() {
			super.setChanged();
		}		
	}
}
