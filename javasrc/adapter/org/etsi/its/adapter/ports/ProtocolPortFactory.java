/**
 *  Protocol port factory (Singleton)
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/ProtocolPortFactory.java $
 *              $Id: ProtocolPortFactory.java 2230 2015-06-03 09:11:02Z mullers $
 *
 */
package org.etsi.its.adapter.ports;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.TreeMap;

/**
 *  Protocol port factory (Singleton)
 */
public class ProtocolPortFactory {

    /**
     * Unique instance of the factory
     */
    private static ProtocolPortFactory instance = new ProtocolPortFactory();
        
    /**
     * List of the registered protocol port types 
     */
    protected Map<String, Class<? extends ProtocolPort>> ports = new TreeMap<String, Class<? extends ProtocolPort>>();
    
    /**
     * Private constructor (Singleton pattern)
     */
    private ProtocolPortFactory() {
        // Register the ports - Refer to TTCN-3 component LibIts_TestSystem.ItsSystem for ports name
        ports.put("camPort", CamPort.class);
        ports.put("denmPort", DenmPort.class);
        ports.put("mapSpatPort", MapSpatPort.class);
        ports.put("btpPort", BtpPort.class);
        ports.put("geoNetworkingPort", GnPort.class);
        ports.put("ipv6OverGeoNetworkingPort", Gn6Port.class);
        try {
            ports.put("v2gPort", Class.forName( "org.etsi.its.adapter.ports.V2GPort" ).asSubclass(ProtocolPort.class)); 
        } catch( ClassNotFoundException e ) {}
    }

    /**
     * Gets the unique factory instance
     * @return ProtocolPortFactory instance
     */
    public static ProtocolPortFactory getInstance(){
        return instance;
    }

    /**
     * Creates a port of the desired type
     * @param  portName            Name of the port
     * @param  componentName       Name of the component owning the port instance
     * @param  lowerStackDesc      Description of the lower protocol stack of the port in the form "Layer/Layer/Layer/..."   
     * @param  linkLayerAddress    Link-layer address to be used by this port as source address (null if not applicable)
     * @return Protocol port instance
     */
    public ProtocolPort createPort(String portName, String componentName, String lowerStackDesc, String linkLayerAddress) {
//        System.out.println(">>> ProtocolPortFactory.createPort: " + portName + ", " + componentName + ", " + lowerStackDesc + ", " + linkLayerAddress);
        
        ProtocolPort port = null;
        Class<?>[] ctorParams = {portName.getClass(), componentName.getClass(), lowerStackDesc.getClass(), linkLayerAddress.getClass()};
        
        try {
            Class<? extends ProtocolPort> cls = ports.get(portName);
            
            if (cls == null) {
                throw new RuntimeException("No class registered under " + portName);
            }
            
            Constructor<? extends ProtocolPort> ctor = cls.getConstructor(ctorParams);
            port = ctor.newInstance(portName, componentName, lowerStackDesc, linkLayerAddress);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return port;
    }
}
 