/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/CodecFactory.java $
 *              $Id: CodecFactory.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.TreeMap;

import org.etsi.ttcn.tci.TciCDProvided;
import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.codec.generic.Bitstring;
import org.etsi.ttcn.codec.generic.Boolean;
import org.etsi.ttcn.codec.generic.Charstring;
import org.etsi.ttcn.codec.generic.Octetstring;
import org.etsi.ttcn.codec.generic.Integer;
import org.etsi.ttcn.codec.generic.Set;
import org.etsi.ttcn.codec.generic.Record;
import org.etsi.ttcn.codec.generic.RecordOf;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.codec.generic.Enumerated;
import org.etsi.ttcn.codec.generic.Dummy;

public class CodecFactory {

    private static CodecFactory instance = new CodecFactory();
    private static boolean initialized = false;
    protected Map<String, Class<? extends ICodec>> codecs = new TreeMap<String, Class<? extends ICodec>>();
    protected Map<String, TciCDProvided> externalCodecs = new TreeMap<String, TciCDProvided>();

    private CodecFactory() {
        codecs.put(TciTypeClass.BITSTRING + "//", Bitstring.class);
        codecs.put(TciTypeClass.OCTETSTRING + "//", Octetstring.class);
        codecs.put(TciTypeClass.CHARSTRING + "//", Charstring.class);
        codecs.put(TciTypeClass.RECORD + "//", Record.class);
        codecs.put(TciTypeClass.SET + "//", Set.class);
        codecs.put(TciTypeClass.INTEGER + "//", Integer.class);
        codecs.put(TciTypeClass.FLOAT + "//", org.etsi.ttcn.codec.generic.Float.class);
        codecs.put(TciTypeClass.UNION + "//", Union.class);
        codecs.put(TciTypeClass.ENUMERATED + "//", Enumerated.class);
        codecs.put(TciTypeClass.RECORD_OF + "//", RecordOf.class);
        codecs.put(TciTypeClass.SET_OF + "//", RecordOf.class);
        codecs.put(TciTypeClass.BOOLEAN + "//", Boolean.class);
 
    }

    public static CodecFactory getInstance() {
        
        instance.initialize();
        return instance;
    }

    public void initialize() {
        
        if(!initialized) {
            initialized = true; 
        
            // FIXME: dynamic
            // initialize additional codec plugins
            org.etsi.ttcn.codec.its.adapter.Plugin.init();
            org.etsi.ttcn.codec.its.uppertester.Plugin.init();
            org.etsi.ttcn.codec.its.geonetworking.Plugin.init();
            org.etsi.ttcn.codec.its.btp.Plugin.init();
            org.etsi.ttcn.codec.its.security.Plugin.init();
            org.etsi.ttcn.codec.its.facilities.Plugin.init();
            org.etsi.ttcn.codec.its.mapspat.Plugin.init();
        }
        
    }

    public TciCDProvided getExternalCodec(String encoding) {
        TciCDProvided codec = null;
        
        if(encoding == null) {
            return null;
        }
        
//        System.out.println("getExternalCodec: Search external codec for " + encoding);
        codec = externalCodecs.get(encoding);
        if(codec != null) {
            System.out.print(String.format("%50s", encoding));
            System.out.println(" ==> " + codec.getClass().getName());      
            return codec;
        }
        return null;
    }
    
    public ICodec getCodec(MainCodec mainCodec, int classType, String encoding, String typeName) {

        System.out.print(String.format("%50s", typeName + "(" + encoding + ")"));
        Class<? extends ICodec> cls = null;
        Class<?>[] ctorParams = {MainCodec.class};

        System.out.println("getCodec: Search internal codec for " + classType + '/' + encoding + '/' + typeName);
        cls = codecs.get(classType + '/' + encoding + '/' + typeName);
        if(cls == null) {
            cls = codecs.get(classType + '/' + encoding + '/');
        }
        if(cls == null) {
            cls = codecs.get(classType + "//");
        }

        if(cls != null) {
            System.out.println(" ==> " + cls.getName());
            try {
                Constructor<? extends ICodec> ctor = cls.getConstructor(ctorParams);
                return ctor.newInstance(mainCodec);
            } 
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        System.out.println(" ==> No codec found !");	  
        return new Dummy(mainCodec);
    }

    public void setCodec(int classType, String encoding, String typeName, Class<? extends ICodec> codec) {
//        System.out.println("setCodec: add " + classType + "/" + encoding + "/" + typeName + "/" + codec);
        codecs.put(classType + '/' + encoding + '/' + typeName, codec);
    }
    
    public void setExternalCodec(String encoding, TciCDProvided codec) {
        externalCodecs.put(encoding, codec);
    }
}
