/**
 * @author    STF 424_ITS_Test_Platform
 * @version    $id$
 */
package org.etsi.tool.elvior;

import java.io.IOException;
import java.util.Properties;

import org.elvior.ttcn.tritci.TriFactory;
import org.elvior.ttcn.tritci.TriProvider;
import org.etsi.adapter.ITERequired;
import org.etsi.its.tool.elvior.MainTA;
import org.etsi.its.tool.elvior.PluginAdapter;
import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;
import org.etsi.ttcn.tri.TriAddress;
import org.etsi.ttcn.tri.TriCommunicationTE;
import org.etsi.ttcn.tri.TriMessage;
import org.etsi.ttcn.tri.TriStatus;

public class TeRequiredImpl implements ITERequired {

    TriFactory _factory = TriProvider.getInstance().getTriFactory();
    
//    private TriTciChannel _tciFactory = TriTciChannel.getInstance();
    
    private final PluginAdapter _ta;
    
    private static Properties _properties = new Properties();
    
    public TeRequiredImpl(PluginAdapter ta) {
        _ta = ta;
        // Load TA settings
        try {
            _properties.load(MainTA.class.getResourceAsStream("/org/etsi/its/tool/elvior/res/ta.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TriCommunicationTE getCommunicationTE() {
        return (TriCommunicationTE)_ta;
    }

    /**
     * @desc Retrieve TA settings from external file
     * It does not work with TestcatT3 because of TCI is only available during test case execution (from triExecuteTestCase method call till triEndTestCase method call)
     */
    @Override
    public Value getTaParameter(String param) { 
        CharstringValue string = new CharstringValue() {
            private String _value = "";
            @Override
            public boolean notPresent() {
                return _value.isEmpty();
            }
            
            @Override
            public String getValueEncodingVariant() {
                return null;
            }
            
            @Override
            public String getValueEncoding() {
                return null;
            }
            
            @Override
            public Type getType() {
                return null;
            }
            
            @Override
            public void setString(String value) {
                _value = value;
            }
            
            @Override
            public void setLength(int length) {
            }
            
            @Override
            public void setChar(int index, char arg1) {
            }
            
            @Override
            public String getString() {
                return _value;
            }
            
            @Override
            public int getLength() {
                return _value.length();
            }
            
            @Override
            public char getChar(int index) {
                return _value.charAt(index);
            }
        };
        string.setString(_properties.getProperty(param, ""));
        
        return string;
    }

    @Override
    public TriAddress getTriAddress(byte[] message) {
        TriAddress address = _factory.createAddress();
        address.setEncodedAddress(message);
        return address;
    }

    @Override
    public TriMessage getTriMessage(byte[] message) {
        TriMessage msg = _factory.createMessage();
        msg.setEncodedMessage(message);
        msg.setNumberOfBits(message.length * Byte.SIZE);
        return msg;
    }

    @Override
    public TriStatus getTriStatus(int statusCode) {
        TriStatus status = _factory.createErrorStatus();
        status.setStatus(statusCode);
        return status;
    }

    @Override
    public TriStatus getTriStatus(int statusCode, String message) {
        return getTriStatus(statusCode);
    }

} // End of class TeRequiredImpl
