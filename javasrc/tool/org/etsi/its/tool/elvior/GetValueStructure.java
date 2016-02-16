package org.etsi.its.tool.elvior;

import org.elvior.ttcn.tritci.IntegerValueEx;
import org.etsi.ttcn.tci.BitstringValue;
import org.etsi.ttcn.tci.BooleanValue;
import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.tci.EnumeratedValue;
import org.etsi.ttcn.tci.FloatValue;
import org.etsi.ttcn.tci.HexstringValue;
import org.etsi.ttcn.tci.OctetstringValue;
import org.etsi.ttcn.tci.RecordOfValue;
import org.etsi.ttcn.tci.RecordValue;
import org.etsi.ttcn.tci.TciTypeClass;
import org.etsi.ttcn.tci.UnionValue;
import org.etsi.ttcn.tci.Value;

public class GetValueStructure {
    
    static String indent = null;
    static StringBuilder buffer = null;
    
    static public void displayValueStructure(final Value value) {
        buffer = new StringBuilder();
        indent = "";
        LogValueDataStructure(value);
//        System.out.println(buffer.toString());
    }
    
    static public String getValueStructure(final Value value) {
        buffer = new StringBuilder();
        indent = "";
        LogValueDataStructure(value);
        return buffer.toString();
    }
    
    static private void LogValueDataStructure(final Value value) {
        if (value.notPresent()) {
            buffer.append(indent + value.getClass().getName() + " : omitted" + "\n");
            return;
        }
        switch (value.getType().getTypeClass()) {
            case TciTypeClass.BOOLEAN: {
                    BooleanValue bv = (BooleanValue)value;
                    buffer.append(indent + bv.getClass().getName() + " : " + new java.lang.Boolean(bv.getBoolean()) + " - encode :  " + bv.getValueEncoding() + " - variant :  " + bv.getValueEncodingVariant() + "\n");
                }
                break;
            case TciTypeClass.INTEGER: {
                    IntegerValueEx iv = (IntegerValueEx)value;
                    buffer.append(indent + iv.getClass().getName() + " : " + iv.getInt64() + " - encode :  " + iv.getValueEncoding() + " - variant :  " + iv.getValueEncodingVariant() + "\n");
                }
                break;
            case TciTypeClass.FLOAT: {
                    FloatValue fv = (FloatValue)value;
                    buffer.append(indent + fv.getClass().getName() + " : " + fv.getFloat() + " - encode :  " + fv.getValueEncoding() + " - variant :  " + fv.getValueEncodingVariant() + "\n");
                }
                break;
            case TciTypeClass.CHARSTRING: {
                    CharstringValue csv = (CharstringValue)value;
                    buffer.append(indent + csv.getClass().getName() + " : " + csv.getString() + " - encode :  " + csv.getValueEncoding() + " - variant :  " + csv.getValueEncodingVariant() + "\n");
                }
                break;
            case TciTypeClass.BITSTRING: {
                    BitstringValue bv = (BitstringValue)value;
                    buffer.append(indent + bv.getClass().getName() + " : " + bv.getString() + " - encode :  " + bv.getValueEncoding() + " - variant :  " + bv.getValueEncodingVariant() + "\n");
                }
                break;
            case TciTypeClass.OCTETSTRING: {
                    OctetstringValue ov = (OctetstringValue)value;
                    buffer.append(indent + ov.getClass().getName() + " : " + ov.getString() + " - encode :  " + ov.getValueEncoding() + " - variant :  " + ov.getValueEncodingVariant() + "\n");
                }
                break;
            case TciTypeClass.HEXSTRING: {
                    HexstringValue hv = (HexstringValue)value;
                    buffer.append(indent + hv.getClass().getName() + " : " + hv.getString() + " - encode :  " + hv.getValueEncoding() + " - variant :  " + hv.getValueEncodingVariant() + "\n");
                }
                break;
            case TciTypeClass.ENUMERATED: {
                    EnumeratedValue ev = (EnumeratedValue)value;
                    buffer.append(indent + ev.getClass().getName() + " : " + ev.getEnum() + " - encode :  " + ev.getValueEncoding() + " - variant :  " + ev.getValueEncodingVariant() + "\n");
                }
                break;
            case TciTypeClass.SET: 
                // No break;
            case TciTypeClass.RECORD: {
                    RecordValue rv = (RecordValue)value;
                    buffer.append(indent + rv.getClass().getName() + " - encode :  " + rv.getValueEncoding() + " - variant :  " + rv.getValueEncodingVariant() + "\n");
                    indent += "\t";
                    for (String key : rv.getFieldNames()) {
                        LogValueDataStructure(rv.getField(key));
                    } // End of 'for'statement
                    indent = indent.substring(0, indent.length() -1);
                }
                break;
            case TciTypeClass.ANYTYPE:
                // No break;
            case TciTypeClass.UNION: {
                    UnionValue uv = (UnionValue)value;
                    buffer.append(indent + uv.getClass().getName() + " - encode :  " + uv.getValueEncoding() + " - variant :  " + uv.getValueEncodingVariant() + "\n");
                    indent += "\t";
                    LogValueDataStructure(uv.getVariant(uv.getPresentVariantName()));
                    indent = indent.substring(0, indent.length() -1);
                }
                break;
            case TciTypeClass.RECORD_OF: 
                // No break;
            case TciTypeClass.SET_OF: {
                    RecordOfValue rofv = (RecordOfValue)value;
                    buffer.append(indent + rofv.getClass().getName() + " - encode :  " + rofv.getValueEncoding() + " : " + rofv.getLength() + " - variant :  " + rofv.getValueEncodingVariant() + "\n");
                    indent += "\t";
                    for (int key = 0; key < rofv.getLength(); key++) {
                        LogValueDataStructure(rofv.getField(key));
                    } // End of 'for'statement
                    indent = indent.substring(0, indent.length() -1);
                }
                break;
             default:
                throw new RuntimeException("Unimplemented class type: " + value.getType().getTypeClass());
        } // End of 'switch' value
    }
    
}
