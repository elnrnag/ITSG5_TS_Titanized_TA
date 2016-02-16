/**
 *  Layer factory (Singleton)
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/LayerFactory.java $
 *              $Id: LayerFactory.java 1822 2014-11-18 09:18:17Z berge $
 *
 */
package org.etsi.its.adapter.layers;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.etsi.its.adapter.IManagementLayers;
import org.etsi.its.adapter.layers.test.BtpSourceLayer;
import org.etsi.its.adapter.layers.test.CamSourceLayer;
import org.etsi.its.adapter.layers.test.DebugLayer;
import org.etsi.its.adapter.layers.test.DenmSourceLayer;
import org.etsi.its.adapter.layers.test.Gn6SourceLayer;
import org.etsi.its.adapter.layers.test.GnSourceLayer;
import org.etsi.its.adapter.layers.test.LoopbackLayer;

/**
 *  Layer factory (Singleton)
 */
public class LayerFactory {

    /**
     * Unique instance of the factory
     */
    private static LayerFactory instance = new LayerFactory();
    
    /**
     * List of the registered layer types 
     */
    protected Map<String, Class<? extends Layer>> layers = new TreeMap<String, Class<? extends Layer>>();
    
    /**
     * Private constructor (Singleton pattern)
     */
    private LayerFactory() {
        // Register the layers
        layers.put("BTP", BtpLayer.class);
        layers.put("GN", GnLayer.class);
        layers.put("G5", G5Layer.class);
        layers.put("ETH", EthernetLayer.class);
        
//        layers.put("Loopback", LoopbackLayer.class);
//        layers.put("Debug", DebugLayer.class);
//        layers.put("CamSource", CamSourceLayer.class);
//        layers.put("DenmSource", DenmSourceLayer.class);
//        layers.put("GnSource", GnSourceLayer.class);
//        layers.put("BtpSource", BtpSourceLayer.class);
//        layers.put("Gn6Source", Gn6SourceLayer.class);
    }

    /**
     * Gets the unique factory instance
     * @return LayerFactory instance
     */
    public static LayerFactory getInstance(){
        return instance;
    }

    /**
     * Creates a port of the desired type
     * @param  management   Layer management instance
     * @param  layerName    Name of the layer
     * @param  lowerStack   Lower protocol stack    
     * @return Protocol port instance
     */
    public Layer createLayer(IManagementLayers management, String layerName, Stack<String> lowerStack) {
//        System.out.println(">>> LayerFactory.createLayer: " + layerName);
        Layer layer = null;
        Class<?>[] ctorParams = {IManagementLayers.class, lowerStack.getClass()};
        
           try {        
            Class<? extends Layer> cls = layers.get(layerName);
            if (cls == null) {
                throw new RuntimeException("No class registered under " + layerName);
            }
            Constructor<? extends Layer> ctor = cls.getConstructor(ctorParams);
            layer = ctor.newInstance(management, lowerStack);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return layer;
    }

}
