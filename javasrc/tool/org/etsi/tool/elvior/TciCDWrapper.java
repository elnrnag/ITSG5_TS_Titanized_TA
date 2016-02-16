/**
 * @author    STF 424_ITS_Test_Platform
 * @version    $id$
 */
package org.etsi.tool.elvior;

import java.math.BigInteger;

import org.elvior.ttcn.tritci.IntegerValueEx;
import org.elvior.ttcn.tritci.TciProvider;
import org.etsi.codec.ITciCDWrapper;
import org.etsi.ttcn.tci.BooleanValue;
import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.tci.EnumeratedValue;
import org.etsi.ttcn.tci.FloatValue;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.tci.OctetstringValue;
import org.etsi.ttcn.tci.RecordOfValue;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.TciCDRequired;
import org.etsi.ttcn.tci.Type;
import org.etsi.ttcn.tci.Value;

/** This class implements the ITciCDWrapper interface
 * 
 * Note that the TCI Codec Interface (TCI-CD) describes the operations a TTCN-3 Executable is required to implement and the operations a codec implementation for a certain encoding scheme shall provide to the TE
 * 
 * See ETSI ES 201 873-6 V4.2.1 - Clause 7.3.2.1 TCI-CD required
 */
public class TciCDWrapper implements ITciCDWrapper {

    private TciCDRequired _tciFactory = TciProvider.getInstance().getTciCDRequired();
    
    public TciCDWrapper() {
        // Nothing to do
    }
    
    @Override
    public IntegerValue setInteger(final Integer value) {
        IntegerValue iv = (IntegerValue)_tciFactory.getInteger().newInstance();
        iv.setInteger(value);
        return iv;
    }

    @Override
    public int getInteger(final IntegerValue iv) {
        return iv.getInteger();
    }

    @Override
    public IntegerValue setInteger(final BigInteger value) { 
//        System.out.println(">>> getBigInteger: " + value);
        IntegerValueEx bi = (IntegerValueEx) _tciFactory.getInteger().newInstance(); //_tciFactory.getTypeForName("org.elvior.ttcn.tritci.IntegerValueEx");
        if (bi != null) { 
//            System.out.println("getBigInteger: then");
            bi.setInt64(value.longValue()); 
//            System.out.println("getBigInteger: bi=" + bi.getInt64());
            return (IntegerValue)bi;
        } else { 
//            System.out.println("getBigInteger: else");
            IntegerValue i = (IntegerValue) _tciFactory.getInteger().newInstance();
//            System.out.println("getBigInteger: i=" + i);
            i.setInteger(value.intValue());
            return i;
        }
    }

    @Override
    public void setInteger(BigInteger value, IntegerValue setInt) {
        ((IntegerValueEx)setInt).setInt64(value.longValue());
    }
    
    @Override
    public long getBigInteger(final IntegerValue iv) {
        return ((IntegerValueEx)iv).getInt64();
    }

    @Override
    public OctetstringValue getOctetstring() {
        return (OctetstringValue)_tciFactory.getOctetstring().newInstance();
    }

    /** Constructs and returns a basic TTCN-3 string type
     * 
     * @return An instance of Type representing a TTCN-3 string type
     */
    @Override
    public CharstringValue getCharstringValue() {
        return (CharstringValue)_tciFactory.getCharstring().newInstance();
    }

    @Override
    public FloatValue getFloat() {
        return (FloatValue)_tciFactory.getFloat().newInstance();
    }

    @Override
    public Value getUnionValue(final Type decodingHypothesis, final String variantName) {
//        System.out.println(">>> getUnionValue: " + decodingHypothesis.getName() + ", " + variantName);
        
        String variantTypeName = decodingHypothesis.getDefiningModule().getModuleName() + "." + decodingHypothesis.getName() + "." + variantName;
//        System.out.println("variantTypeName is " + variantTypeName);
        Type variantType = _tciFactory.getTypeForName(variantTypeName);
        if(variantType != null) {
            Value testVal = variantType.newInstance();
            if(testVal != null) {
//                System.out.println("Variant value has been created.");
                return testVal;
            }
        }
        System.err.println("variantType is null");
        
        System.out.println("Variant value hasn't been created.");
        return null;
    }
    
    @Override
    public Type getTypeForName(final String type) { 
//        return TciProvider.getInstance().getTciCDRequired().getTypeForName(type);
//        System.out.println("TciCDWrapper.getTypeForName" + type);
        Type originalType = TciProvider.getInstance().getTciCDRequired().getTypeForName(type);
//        System.out.println("TciCDWrapper.getTypeForName" + originalType.getName());
        return originalType;
    } // End of method getTypeForName
    
    @Override
    public BooleanValue getBoolean() {
        return (BooleanValue)_tciFactory.getBoolean().newInstance();
    }

    @Override
    public EnumeratedValue getEnumValue(String typeName) {
        return (EnumeratedValue)_tciFactory.getTypeForName(typeName).newInstance();
    }
    
    @Override
    public RecordValue getRecordValue(final String typeName) {
        return (RecordValue)_tciFactory.getTypeForName(typeName).newInstance();
    }

    @Override
    public RecordOfValue getRecordOfValue(final String typeName) {
        return (RecordOfValue)_tciFactory.getTypeForName(typeName).newInstance();
    }

    @Override
    public void setInteger(IntegerValue intValue, final Integer value) {
        intValue.setInteger(value);
    }
    
} // End of class TciCDWrapper
