/**
 *  Component manager that handles ports and component creation
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ComponentMgr.java $
 *              $Id: ComponentMgr.java 1423 2014-05-22 13:59:50Z filatov $
 *
 */
package org.etsi.its.adapter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observer;

import org.etsi.common.ITuple;
import org.etsi.common.Tuple;
import org.etsi.its.adapter.ports.IObservable;
import org.etsi.its.adapter.ports.IPort;
import org.etsi.ttcn.tri.TriComponentId;
import org.etsi.ttcn.tri.TriPortId;

/**
 * Component manager that handles ports and component creation
 */
public class ComponentMgr{

	/** 
	 * Association Component ID <-> Component reference
	 */
	private LinkedHashMap<String, TriComponentId> mapCompNameToTriComp;

	/** 
	 * Association Component ID <-> { (TTCN-3 Port reference / SUT Port reference) }
	 */
	private LinkedHashMap<String, Map<String, ITuple<TriPortId, IPort>>> mapTriPortToTuple;

	/** 
	 * Used to add Observer object
	 */
	private TestAdapter adapter;

	/** 
	 * Constructor
	 * @param  adapter TestAdapter reference, required for Observer/Observable pattern
	 */
	public ComponentMgr(final TestAdapter adapter) {

		this.adapter = adapter;
		mapCompNameToTriComp = new LinkedHashMap<String, TriComponentId>();
		mapTriPortToTuple = new LinkedHashMap<String, Map<String, ITuple<TriPortId, IPort>>>();
	}

	/** 
	 * Adds a new component
	 * If the component was already added, nothing is done
	 * @param  component   The component to add
	 */
	public void addComponent(TriComponentId component) {
		// Sanity check
		if(component == null) {
			System.err.println("Error: Trying to add null component");
			return;
		}		

		if(!mapCompNameToTriComp.containsKey(component.getComponentId())) {
			// Create an entry in the list of Component
			mapCompNameToTriComp.put(component.getComponentId(), component); 
			if(!mapTriPortToTuple.containsKey(component.getComponentId())) {
			    // Create an entry in the list of Component/Ports
				mapTriPortToTuple.put(component.getComponentId(), new LinkedHashMap<String, ITuple<TriPortId, IPort>>()); 
			}
		}
	} 

	/**
	 * Adds a new port to the specified component
	 * @param  component   The component reference
	 * @param  portname    The port name
	 * @param  port        The port to add
	 */
	public void addPort(final String componentName, final TriPortId ttcnPort, final IPort port) {
		// Sanity checks
		if(componentName.isEmpty() || (ttcnPort == null) || (port == null)) {
		    System.err.println("Wrong parameters");
			return;
		}				
		if(!mapCompNameToTriComp.containsKey(componentName)) {
		    System.err.println("Error: Trying to add port to unknown component");
			return;
		}
		if(!mapTriPortToTuple.containsKey(componentName)) {
		    // Create an entry in the list of Component/Ports
            mapTriPortToTuple.put(componentName, new LinkedHashMap<String, ITuple<TriPortId, IPort>>()); 
		}		

		Map<String, ITuple<TriPortId, IPort>> portItem = mapTriPortToTuple.get(componentName);
		if(!portItem.containsKey(ttcnPort.getPortName())) {
			portItem.put(ttcnPort.getPortName(), new Tuple<TriPortId, IPort>(ttcnPort, port));
			((IObservable)port).addObserver((Observer) adapter);
			mapTriPortToTuple.put(componentName, portItem);
		}
	}

	/** 
	 * Gets the component reference from its name
	 * @param  componentName   The component name
	 * @return The component reference if the component exists, null otherwise
	 */
	public TriComponentId getComponent(String componentName) {

		// Sanity checks
		if(componentName == null || componentName.isEmpty()) {
			System.err.println("Invalid component");
			return null;
		}

		return mapCompNameToTriComp.get(componentName);
	}

	/** 
	 * Retrieves the TTCN-3 port identifier (TriPortId) from component/port names
	 * @param  componentName   The component reference
	 * @param  portName        The port name
	 * @return The TTCN-3 port identifier if the component and the port exists, null otherwise
	 */
	public TriPortId getPortId(String componentName, String portName) {

		// Sanity checks
		if(componentName.isEmpty() || portName.isEmpty()) {
		    System.err.println("Wrong parameters");
			return null;
		}
		if(!mapCompNameToTriComp.containsKey(componentName)) {
		    System.err.println("Unknown component");
			return null;
		}
		if(!mapTriPortToTuple.containsKey(componentName)) {
		    System.err.println("No port list entry");
			return null;
		}

		Map<String, ITuple<TriPortId, IPort>> portItem = mapTriPortToTuple.get(componentName);
		if(!portItem.containsKey(portName)) {
			return null;
		}
		
		ITuple<TriPortId, IPort> item = portItem.get(portName);
		return item.getA();
	}

	/** 
	 * Retrieves the test adapter port identifier (Port or IAdapterPort) from component/port names
	 * @param  componentName   The component owner
	 * @return The port reference if the component and the port exists, null otherwise
	 * 
	 * @see Port
	 * @see IAdapterPort
	 */
	public IPort getPort(String componentName, String portName) {

		// Sanity checks
		if(componentName.isEmpty() || portName.isEmpty()) {
		    System.err.println("Wrong parameters");
			return null;
		}
		if(!mapCompNameToTriComp.containsKey(componentName)) {
		    System.err.println("Unknown component");
			return null;
		}
		if(!mapTriPortToTuple.containsKey(componentName)) {
		    System.err.println("No port list entry");
			return null;
		}

		Map<String, ITuple<TriPortId, IPort>> portItem = mapTriPortToTuple.get(componentName);
		if(!portItem.containsKey(portName)) {
			return null;
		}
		
		ITuple<TriPortId, IPort> item = portItem.get(portName);
		return item.getB();
	}

	/**
	 * Removes the specified component
	 * 
	 * Note that after the port removal, if the component has no more port, it will be removed also
	 * 
	 * @param  component   The component reference
	 */
	public void removeComponent(String componentName) {

		removeAllPorts();
	}

	/** 
	 * Removes the specified port
	 * 
	 * Note that all ports attached to this component will be removed also
	 * 
	 * @param  componentName   The component name to remove
	 */
	public void removePort(String componentName, String portName) {
		// Sanity checks		
		if(componentName.isEmpty() || portName.isEmpty()) {
		    System.err.println("Wrong parameters");
			return;
		}
		if(!mapCompNameToTriComp.containsKey(componentName)) {
		    System.err.println("Unknown component");
			return;
		}
		if(!mapTriPortToTuple.containsKey(componentName)) {
		    System.err.println("No port list entry");
			return;
		}
		
		Map<String, ITuple<TriPortId, IPort>> portItem = mapTriPortToTuple.get(componentName);
		if(!portItem.containsKey(portName)) {
			return;
		}
		// Remove Observers
		((IObservable)portItem.get(portName).getB()).deleteObservers();
		// Call dispose 
		((IPort)portItem.get(portName).getB()).dispose();
		// Remove item
		portItem.remove(portName);
		if(portItem.size() != 0) {
			mapTriPortToTuple.put(componentName, portItem);
		} else {
			mapTriPortToTuple.remove(componentName);
            mapCompNameToTriComp.remove(componentName);
            if(mapCompNameToTriComp.isEmpty()) {
                mapCompNameToTriComp.clear();
            }
        }
		if(mapTriPortToTuple.isEmpty()) {
			mapTriPortToTuple.clear();
		}
	}
	
	/** 
	 * Removes all ports.
	 */
	public void removeAllPorts() {
		
	    // Remove all ports
		for(Object componentName : mapTriPortToTuple.keySet().toArray()) {
			Map<String, ITuple<TriPortId, IPort>> portItem = mapTriPortToTuple.get(componentName);
			for(Object portName : portItem.keySet().toArray()) {
				removePort((String)componentName, (String)portName);
			}
		}
		
		// Remove component mapping
		mapCompNameToTriComp.clear();
	}
} 
