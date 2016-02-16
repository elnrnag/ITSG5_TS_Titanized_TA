/**
 * @author      ETSI / STF462 / Alexandre Berge
 * @version     $$URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/v2g/ExiHelper.java $$
 *              $$Id: ExiHelper.java 1139 2013-08-09 14:20:11Z berge $$
 */
package org.etsi.ttcn.codec.its.v2g;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.etsi.common.ByteHelper;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.SchemaIdResolver;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class ExiHelper {

	private static String schemaId = "v2g";
	private static boolean locked = false;
	protected static String handshakeSchemaName = "ttcn_PowerUp\\xsd\\V2G_CI_AppProtocol.xsd";
	protected static String v2gSchemaName = "ttcn_PowerUp\\xsd\\V2G_CI_MsgBody.xsd";

	public static void setSchemaId(String schemaName) {
	    while(locked) {
		try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	    }
	    System.err.println("Changing Schema ID:" +schemaName);
	    schemaId = schemaName;
	}

	public static void lockSchemaId() {
	    locked = true;
	}

	private static void unlockSchemaId() {
	    locked = false;
	}
	
	public static byte[] encode(byte[] xmlInput) {
		InputStream xmlIn = new ByteArrayInputStream(xmlInput);
		ByteArrayOutputStream exiOut = new ByteArrayOutputStream();
		
		//ByteHelper.dump("encode EXI", xmlInput);
		System.out.println("encode XML:" + new String(xmlInput));
        
		EXIFactory exiFactory = createEXIFactory(); 
		try {
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(exiOut); 
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(exiResult.getHandler());
			xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler",
			exiResult.getLexicalHandler());
			xmlReader.setProperty("http://xml.org/sax/properties/declaration-handler",
			exiResult.getLexicalHandler());
			xmlReader.parse(new InputSource(xmlIn));
		} catch (Exception e) {
			System.err.println("ERROR: Unable to EXI-encode message: " + e.getMessage());
			e.printStackTrace();
			return xmlInput;
		} 
		
		ByteHelper.dump("encode EXI", exiOut.toByteArray());
		
		return exiOut.toByteArray();
	}

	public static byte[] decode(byte[] exiInput) {
		InputStream exiIn = new ByteArrayInputStream(exiInput);
		ByteArrayOutputStream xmlOut = new ByteArrayOutputStream();

		ByteHelper.dump("decode EXI", exiInput);
		
		EXIFactory exiFactory = createEXIFactory();
		try {
			EXISource exiSource = new EXISource(exiFactory); 
			XMLReader xmlReader = exiSource.getXMLReader(); 
			TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
			Transformer transformer = transformerFactory.newTransformer();
			SAXSource saxSource = new SAXSource(new InputSource(exiIn)); 
			saxSource.setXMLReader(xmlReader);
			transformer.transform(saxSource, new StreamResult(xmlOut));
		} catch (Exception e) {
			System.err.println("ERROR: Unable to EXI-decode message: " + e.getMessage());
			e.printStackTrace();
			unlockSchemaId();
			return exiInput;
		} 
		
		//ByteHelper.dump("decode XML:", xmlOut.toByteArray());
		try {
            System.out.println("decode XML:" + xmlOut.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		unlockSchemaId();
		return xmlOut.toByteArray();
	}
	
	private static EXIFactory createEXIFactory() {

		EXIFactory exiFactory = DefaultEXIFactory.newInstance(); 
		
		try {
			// Encoding options
			EncodingOptions encodingOptions = EncodingOptions.createDefault();
			exiFactory.setEncodingOptions(encodingOptions);
			
			// Fidelity options
			FidelityOptions fidelityOptions = FidelityOptions.createDefault();
			fidelityOptions.setFidelity(FidelityOptions.FEATURE_STRICT, false);
			fidelityOptions.setFidelity(FidelityOptions.FEATURE_PREFIX, false);
			fidelityOptions.setFidelity(FidelityOptions.FEATURE_DTD, false);
			fidelityOptions.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, false);
			fidelityOptions.setFidelity(FidelityOptions.FEATURE_COMMENT, false);
			fidelityOptions.setFidelity(FidelityOptions.FEATURE_PI, false);
			fidelityOptions.setFidelity(FidelityOptions.FEATURE_SC, false);			
			exiFactory.setFidelityOptions(fidelityOptions); 
		}
		catch(Exception e) {
			System.err.println("ERROR: Unable to setup EXIFactory: " + e.getMessage());
			e.printStackTrace();			
		}
		
		// Coding mode
		exiFactory.setCodingMode(CodingMode.BIT_PACKED);
		
		// Value partition capacity
		exiFactory.setValuePartitionCapacity(0);
		
		// SCHEMA OPTIONS
		loadGrammar(exiFactory);

		return exiFactory;
	}
	
	private static void loadGrammar(EXIFactory exiFactory) {
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars grammars = null;
		MySchemaIdResolver schemaIdResolver = new MySchemaIdResolver(grammarFactory);
		exiFactory.setSchemaIdResolver(schemaIdResolver);
		try {
			grammars = schemaIdResolver.resolveSchemaId(schemaId); 
			if (grammars != null)
				exiFactory.setGrammars(grammars);
		} catch (EXIException e) {
			e.printStackTrace();
		}
	}	
	
	
	private static class MySchemaIdResolver implements SchemaIdResolver {

		private GrammarFactory grammarFactory;
		
		public MySchemaIdResolver(GrammarFactory grammarFactory) {
			this.grammarFactory = grammarFactory;
		}
		
		@Override
		public Grammars resolveSchemaId(String schemaId) throws EXIException {
			Grammars grammars = null;
			String explicitSchemaName = null;
			if(schemaId.equals("handshake")) {
				explicitSchemaName = handshakeSchemaName;
			}
			if(schemaId.equals("v2g")) {
				explicitSchemaName = v2gSchemaName;
			}	
			File schemaFile = new File(explicitSchemaName);
			grammars = grammarFactory.createGrammars(schemaFile.getAbsolutePath());
			return grammars;
		}
	}
}
