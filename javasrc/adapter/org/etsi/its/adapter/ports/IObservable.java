/**
 *  Interface for observable objects
 * 
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/IObservable.java $
 *              $Id: IObservable.java 417 2011-04-20 15:12:06Z berge $
 *
 */
package org.etsi.its.adapter.ports;

import java.util.Observer;

/**
 *  Interface for observable objects
 */
public interface IObservable {

	/**
	 * If this object has changed then notify all of its observers
	 * @param arg
	 */
	void notifyObservers(Object arg);

	/**
	 * Adds an observer to the set of observers for this object, provided that it is not the same as some observer already in the set
	 * @param  observer    Observer to be registered 
	 */
	void addObserver(Observer observer);

	/**
	 * Clears the observer list so that this object no longer has any observers
	 */
	void deleteObservers();

}
