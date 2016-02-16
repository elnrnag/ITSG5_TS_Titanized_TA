/**
 *  Abstract class for Adapter-related port implementations
 * 
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/AdapterPort.java $
 *              $Id: AdapterPort.java 417 2011-04-20 15:12:06Z berge $
 *
 */
package org.etsi.its.adapter.ports;

import java.util.Observable;
import java.util.Observer;

/**
 *  Abstract class for Adapter-related port implementations
 */ 
public abstract class AdapterPort implements IPort, IObservable {

    /**
     * Constructor
     * @param   portName        Name of the port
     * @param   componentName   Name of the component owning this port instance
     */
    public AdapterPort(final String portName, final String componentName) {
        this.portName = portName;
        this.componentName = componentName;
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
