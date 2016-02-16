/**
 * @author    STF 424_ITS_Test_Platform
 * @version    $id$
 */
package org.etsi.tool.testingtech;

import java.math.BigInteger;

import org.etsi.codec.ITciCDWrapper;
import org.etsi.ttcn.tci.BooleanValue;
import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.tci.EnumeratedValue;
import org.etsi.ttcn.tci.FloatValue;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.tci.OctetstringValue;
import org.etsi.ttcn.tci.RecordOfValue;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

import de.tu_berlin.cs.uebb.muttcn.runtime.RB;

/** This class implements the ITciCDWrapper interface
 * 
 * Note that the TCI Codec Interface (TCI-CD) describes the operations a TTCN-3 Executable is required to implement and the operations a codec implementation for a certain encoding scheme shall provide to the TE
 * 
 * See ETSI ES 201 873-6 V4.2.1 - Clause 7.3.2.1 TCI-CD required
 */
public class TciCDWrapper implements ITciCDWrapper {

    /**
     * RuntimeBehavior instance reference
     */
    private RB _rb;

    /**
     * Specialized ctor
     * @param rb TTCN-3 runtime reference
     */
    public TciCDWrapper(final RB rb) {
        _rb = rb;
    }

    @Override
    public IntegerValue setInteger(Integer value) {
        IntegerValue iv = (IntegerValue)_rb.getTciCDRequired().getInteger().newInstance();
        iv.setInt(value);
        return iv;
    }

    @Override
    public IntegerValue setInteger(BigInteger value) {
        IntegerValue iv = (IntegerValue)_rb.getTciCDRequired().getInteger().newInstance();
        iv.setBigInt(value);
        return iv;
    }

    @Override
    public void setInteger(IntegerValue intValue, final Integer value) {
        intValue.setInt(value);
    }

    @Override
    public void setInteger(BigInteger value, IntegerValue setInt) {
        setInt.setBigInt(value);
    }
    
    /** Constructs and returns a basic TTCN-3 integer type
     * 
     * See ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.2 getInteger
     * @return An instance of Type representing a TTCN-3 integer type
     */
//    @Override
//    public IntegerValue getInteger() {
//        return (IntegerValue)_rb.getTciCDRequired().getInteger().newInstance();
//    }
//
    @Override
    public int getInteger(IntegerValue iv) {
        return iv.getInt();
    }

    @Override
    public long getBigInteger(IntegerValue iv) {
        return iv.getBigInt().longValue();
    }

    /** Constructs and returns a basic TTCN-3 float type
     * 
     * See ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.3 getFloat
     * @return An instance of Type representing a TTCN-3 float type
     */
    @Override
    public FloatValue getFloat() {
        return (FloatValue)_rb.getTciCDRequired().getFloat().newInstance();
    }

    /** Constructs and returns a basic TTCN-3 octet string type
     * 
     * See ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.2 getOctetstring
     * @return An instance of Type representing a TTCN-3 octet string type
     */
    @Override
    public OctetstringValue getOctetstring() {
        return (OctetstringValue)_rb.getTciCDRequired().getOctetstring().newInstance();
    }

    /** Constructs and returns a basic TTCN-3 string type
     * 
     * @return An instance of Type representing a TTCN-3 string type
     */
    @Override
    public CharstringValue getCharstringValue() {
        return (CharstringValue)_rb.getTciCDRequired().getCharstring().newInstance();
    }

    @Override
    public Value getUnionValue(Type decodingHypothesis, String variantName) {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented for TTWB");
    }

    @Override
    public Type getTypeForName(String type) {
        return _rb.getTciCDRequired().getTypeForName(type);
    }
    
    @Override
    public BooleanValue getBoolean() {
        return (BooleanValue)_rb.getTciCDRequired().getBoolean().newInstance();
    }

    @Override
    public RecordValue getRecordValue(String typeName) {
        return (RecordValue) _rb.getTciCDRequired().getTypeForName(typeName);
    }

    @Override
    public RecordOfValue getRecordOfValue(String typeName) {
        return (RecordOfValue) _rb.getTciCDRequired().getTypeForName(typeName);
    }

    @Override
    public EnumeratedValue getEnumValue(String typeName) {
        return (EnumeratedValue) _rb.getTciCDRequired().getTypeForName(typeName);
    }
    
} // End of class TciCDWrapper
