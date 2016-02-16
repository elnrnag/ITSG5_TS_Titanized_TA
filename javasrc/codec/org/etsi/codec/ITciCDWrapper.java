/**
 * @author    STF 424_ITS_Test_Platform
 * @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/codec/ITciCDWrapper.java $
 *             $Id: ITciCDWrapper.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.codec;

import java.math.BigInteger;

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
/** 
 * This interface is used to provide vendor specific implementations of TCI-CD interface
 * 
 * Note that the TCI Codec Interface (TCI-CD) describes the operations a TTCN-3 Executable is required to implement and the operations a codec implementation for a certain encoding scheme shall provide to the TE
 * 
 * See ETSI ES 201 873-6 V4.2.1 - Clause 7.3.2.1 TCI-CD required
 */
public interface ITciCDWrapper {

    /** 
     * Constructs and returns a basic TTCN-3 integer type
     * 
     * See ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.2 getInteger
     * @return An instance of Type representing a TTCN-3 integer type
     */
    public IntegerValue setInteger(final Integer value);

    /** 
     * Constructs and sets a basic TTCN-3 integer type
     * 
     * See ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.2 getInteger
     */
    public void setInteger(IntegerValue intValue, final Integer value);

    /** 
     * Constructs and returns a basic TTCN-3 big integer type
     * 
     * @see ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.2 getInteger
     * @return An instance of Type representing a TTCN-3 integer type
     */
    public IntegerValue setInteger(final BigInteger value);
    
    /** 
     * Constructs and returns a basic TTCN-3 big integer type
     * 
     * @see ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.2 getInteger
     * @return An instance of Type representing a TTCN-3 integer type
     */
    public void setInteger(final BigInteger value, IntegerValue setInt);

    /** 
     * Constructs and returns a basic integer type
     * 
     * See ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.2 getInteger
     * @return An instance of Type representing a TTCN-3 integer type
     */
    public int getInteger(final IntegerValue iv);

    /** 
     * Constructs and returns a basic big integer type
     * 
     * @see ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.2 getInteger
     * @return An instance of Type representing a TTCN-3 integer type
     */
    public long/*TODO BigInteger*/ getBigInteger(final IntegerValue iv);

    /** 
     * Constructs and returns a basic TTCN-3 octet string type
     * 
     * See ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.2 getOctetstring
     * @return An instance of Type representing a TTCN-3 octet string type
     */
    public OctetstringValue getOctetstring();

    /** 
     * Constructs and returns a basic TTCN-3 string type
     * @return An instance of Type representing a TTCN-3 string type
     */
    public CharstringValue getCharstringValue();

    /** 
     * Constructs and returns a basic TTCN-3 float type
     * 
     * See ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.3 getFloat
     * @return An instance of Type representing a TTCN-3 float type
     */
    public FloatValue getFloat();
    
    /** 
     * Constructs and returns a basic TTCN-3 float type
     * 
     * @return An instance of Type representing a TTCN-3 float type
     */
    public Value getUnionValue(final Type decodingHypothesis, final String variantName);
    
    /** 
     * Constructs and returns a basic TTCN-3 float type
     * 
     * @return An instance of Type representing a TTCN-3 float type
     */
    public RecordValue getRecordValue(final String typeName);
    
    /** 
     * Constructs and returns a basic TTCN-3 float type
     * 
     * @return An instance of Type representing a TTCN-3 float type
     */
    public RecordOfValue getRecordOfValue(final String typeName);
    
    /** 
     * Constructs and returns a basic TTCN-3 boolean type
     * 
     * See ETSI ES 201 873-6 V4.2.1 - 7.3.2.1.4 getBoolean
     * @return An instance of Type representing a TTCN-3 boolean type
     */
    public BooleanValue getBoolean();

    /** 
     * Constructs and returns a basic TTCN-3 enumerated type
     * 
     * @return An instance of Type representing a TTCN-3 enumerated type
     */
    public EnumeratedValue getEnumValue(String string);
    
    /**
     * Convert the specified type string into a Type object
     * @param type The type in string format
     * @return A Type object
     */
    public Type getTypeForName(final String type);

    // FIXME To be continued
} // End of interface ITciCDWrapper
