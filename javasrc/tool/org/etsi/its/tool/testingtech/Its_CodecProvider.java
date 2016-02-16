/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $URL$
 *              $Id$
 */
package org.etsi.its.tool.testingtech;

import org.etsi.codec.ITciCDWrapper;
import org.etsi.codec.TciCDWrapperFactory;
import org.etsi.tool.testingtech.TTWBCodecSupport;
import org.etsi.ttcn.codec.CodecFactory;
import org.etsi.ttcn.codec.MainCodec;
import org.etsi.ttcn.tci.TciCDProvided;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriMessage;

import com.testingtech.ttcn.extension.CodecProvider;
import com.testingtech.util.plugin.PluginInitException;

import de.tu_berlin.cs.uebb.muttcn.runtime.RB;

/** This is the main entry point for Its codec
 */
public class Its_CodecProvider implements CodecProvider {

    /** 
     * This method provides the codec associated to the specified encoding identifier
     * 
     * @param rb TTwb Runtime reference
     * @param encodingName The name of the encoding, specified by the TTCN-3 key words 'with/encode'
     * @return The codec associated to the specified encoding identifier
     */
    @Override
    public TciCDProvided getCodec(RB rb, String encodingName) throws PluginInitException {
        
        // Register External codecs
        CodecFactory cf = CodecFactory.getInstance();
        TTWBCodecSupport cs = new TTWBCodecSupport(rb);
        cf.setExternalCodec("LibItsCam_asn1", cs.getCodec("LibItsCam_asn1"));
        cf.setExternalCodec("LibItsDenm_asn1", cs.getCodec("LibItsDenm_asn1"));
        cf.setExternalCodec("LibItsMapSpat_asn1", cs.getCodec("LibItsMapSpat_asn1"));
        //TODO: add V2G external codecs (xsd)
        //TODO: Yann add CALM external codecs (ASN.1)
        return new Codec();
    }

    private class Codec implements TciCDProvided {

        @Override
        public Value decode(TriMessage message, Type decodingHypothesis) {
            MainCodec codec = new MainCodec((ITciCDWrapper) TciCDWrapperFactory.getTciCDInstance());
            Value v = null;
            try {
                v = codec.triDecode(message, decodingHypothesis);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return v;
        }

        @Override
        public TriMessage encode(Value value) {
            MainCodec codec = new MainCodec((ITciCDWrapper) TciCDWrapperFactory.getTciCDInstance());
            TriMessage m = null;
            try {
                m = codec.triEncode(value);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return m;
        }                
    }
}
