/**
 * @author    STF 424_ITS_Test_Platform
 * @version    $id$
 */
package org.etsi.its.tool.testingtech;

import org.etsi.codec.TciCDWrapperFactory;
import org.etsi.its.extfunc.IItsExternalFunctionsProvider;
import org.etsi.its.extfunc.ItsExternalFunctionsProvider;
import org.etsi.tool.testingtech.TciCDWrapper;
import org.etsi.ttcn.tci.BooleanValue;
import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.tci.FloatValue;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.tci.OctetstringValue;
import org.etsi.ttcn.tci.RecordOfValue;
import org.etsi.ttcn.tci.RecordValue;

import com.testingtech.ttcn.extension.ExternalFunctionsProvider;
import com.testingtech.ttcn.tri.ExternalFunctionsDefinition;

import de.tu_berlin.cs.uebb.muttcn.runtime.RB;

/**
 * This class provides access to the implementation of TTCN-3 external function
 * @see IItsExternalFunctionsProvider
 */
public class ExternalFunctionsPluginProvider implements ExternalFunctionsProvider, ExternalFunctionsDefinition, IItsExternalFunctionsProvider {

    /**
     * Reference to the 
     */
    private IItsExternalFunctionsProvider _externalFunctionsPluginProvider;

    
    /** 
     * Constructor
     */
    public ExternalFunctionsPluginProvider() {
        _externalFunctionsPluginProvider = null;

    }

    /** 
     * Constructor
     * 
     * @param rb TTwb runtime reference
     */
    public ExternalFunctionsPluginProvider(RB rb) {
        TciCDWrapperFactory.getInstance().setImpl(new TciCDWrapper(rb));
        _externalFunctionsPluginProvider = (IItsExternalFunctionsProvider)new ItsExternalFunctionsProvider();

    }

    /** 
     * Refer to TTwb developer's guide
     */
    @Override
    public ExternalFunctionsDefinition getExternalFunctions(RB rb) {
        return new ExternalFunctionsPluginProvider(rb);
    }

    /**
     * This external function gets the current time
     * @param pLatitudeA Latitude of node A
     * @param pLongitudeA Longitude of node A
     * @param pLatitudeB Latitude of node B
     * @param pLongitudeB Longitude of node B
     * @return The current time in Epoch format
     * 
     * TTCN-3 signature:
     * external function fx_computeDistance(in UInt32 p_latitudeA, in UInt32 p_longitudeA, in UInt32 p_latitudeB, in UInt32 p_longitudeB) return float;
     */
    @Override
    public FloatValue fx_computeDistance(IntegerValue pLatitudeA,
            IntegerValue pLongitudeA, IntegerValue pLatitudeB,
            IntegerValue pLongitudeB) {
        return _externalFunctionsPluginProvider.fx_computeDistance(pLatitudeA, pLongitudeA, pLatitudeB, pLongitudeB);
    }

    /**
     * External function to compute a position using a reference position, a distance and an orientation 
     * @param pIutLongPosVector Reference position
     * @param pDistance Distance to the reference position (in meter)
     * @param pOrientation Direction of the computed position (0 to 359; 0 means North)
     * @param pLatitude Computed position's latitude
     * @param pLongitude Computed position's longitude
     * 
     * TTCN-3 signature:
     * external function fx_computePositionUsingDistance(in LongPosVector p_iutLongPosVector, in integer p_distance, in integer p_orientation, out UInt32 p_latitude, out UInt32 p_longitude);
     */
    @Override
    public void fx_computePositionUsingDistance(
            IntegerValue p_refLatitude, IntegerValue p_refLongitude,
            IntegerValue pDistance, IntegerValue pOrientation,
            IntegerValue pLatitude, IntegerValue pLongitude) {
        _externalFunctionsPluginProvider.fx_computePositionUsingDistance(p_refLatitude, p_refLongitude, pDistance, pOrientation, pLatitude, pLongitude);
    }

    @Override
    public FloatValue fx_computeRadiusFromCircularArea(FloatValue p_squareMeters) {        
        return _externalFunctionsPluginProvider.fx_computeRadiusFromCircularArea(p_squareMeters);
    }
    
    /**
     * This external function gets the current time
     * @return The current time in Epoch format
     * 
     * TTCN-3 signature:
     * external function fx_getCurrentTime() return TimeStamp;
     */
    @Override
    public IntegerValue fx_getCurrentTime() {
        return _externalFunctionsPluginProvider.fx_getCurrentTime();
    }
    
    /**
     * External function to compute timestamp based on current time
     * @return  Unix-Epoch-Time mod 2^32
     * 
     * TTCN-3 signature:
     * external function fx_computeGnTimestamp() return UInt32;
     */
     @Override
    public IntegerValue fx_computeGnTimestamp() {
         return _externalFunctionsPluginProvider.fx_computeGnTimestamp();
         
     }

     /**
      * @desc    Calculate ICMPv6 checksum on pseudo header according RFC 4443 - Clause 2.3
      * @param   p_sourceAddress         Source address, 
      * @param   p_destinationAddress    Destination address
      * @param   p_payloadLength         Upper-Layer Packet Length
      * @param   p_payload               Upper-Layer payload
      * @param   p_nextHdr               Next header value (e.g. 0x3a for ICMPv6)
      * @return  The checksum value
      * @see     RFC 2460 IPv6 Specification
      * 
      * TTCN-3 signature:
      * external function fx_computeIPv6CheckSum( 
      *     in template (value) Ipv6Address p_sourceAddress, 
      *     in template (value) Ipv6Address p_destinationAddress, 
      *     in template (value) integer p_payloadLength, 
      *     in template (value) octetstring p_payload, 
      *     in template (value) integer p_nextHdr 
      * ) return Oct2; 
      * <pre>
      * Pseudo header is defined by RFC 2460 - Clause 8.1
      *   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 
      *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      *  |                                                               |
      *  +                                                               +
      *  |                                                               |
      *  +                         Source Address                        +
      *  |                                                               |
      *  +                                                               +
      *  |                                                               |
      *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      *  |                                                               |
      *  +                                                               +
      *  |                                                               |
      *  +                      Destination Address                      +
      *  |                                                               |
      *  +                                                               +
      *  |                                                               |
      *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      *  |                   Upper-Layer Packet Length                   |
      *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      *  |                      zero                     |  Next Header  |
      *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      * </pre>
      */
    @Override
    public OctetstringValue fx_computeIPv6CheckSum(
            OctetstringValue p_sourceAddress, 
            OctetstringValue p_destinationAddress, 
            IntegerValue p_payloadLength, 
            OctetstringValue p_payload, 
            IntegerValue p_nextHdr 
        ) {
        return _externalFunctionsPluginProvider.fx_computeIPv6CheckSum(p_sourceAddress, p_destinationAddress, p_payloadLength, p_payload, p_nextHdr);
    }

    @Override
    public OctetstringValue xf_parseIpv6Address(final CharstringValue p_textIpv6Address) {
        return _externalFunctionsPluginProvider.xf_parseIpv6Address(p_textIpv6Address);
    }
    
    /**
     * @desc    Produces a 256-bit (32-byte) hash value
     * @param   p_toBeHashedData Data to be used to calculate the hash value
     * @return  The hash value
     */
    @Override
    public OctetstringValue fx_hashWithSha256(final OctetstringValue p_toBeHashedData) {
        return _externalFunctionsPluginProvider.fx_hashWithSha256(p_toBeHashedData);
    }
    
    /**
     * @desc    Produces a Elliptic Curve Digital Signature Algorithm (ECDSA) signaturee
     * @param   p_toBeSignedData    The data to be signed
     * @param   p_privateKey        The private key
     * @return  The signature value
     */
    @Override
    public OctetstringValue fx_signWithEcdsaNistp256WithSha256(final OctetstringValue p_toBeSignedData, final OctetstringValue p_privateKey) {
        return _externalFunctionsPluginProvider.fx_signWithEcdsaNistp256WithSha256(p_toBeSignedData, p_privateKey);
    }
    
    /**
     * @desc    Verify the signature of the specified data
     * @param   p_toBeVerifiedData          The data to be verified
     * @param   p_signature                 The signature
     * @param   p_ecdsaNistp256PublicKeyX   The public key (x coordinate)
     * @param   p_ecdsaNistp256PublicKeyY   The public key (y coordinate)
     * @return  true on success, false otherwise
     */
    @Override
    public BooleanValue fx_verifyWithEcdsaNistp256WithSha256(final OctetstringValue p_toBeVerifiedData, final OctetstringValue p_signature, final OctetstringValue p_ecdsaNistp256PublicKeyX, final OctetstringValue p_ecdsaNistp256PublicKeyY) {
        return _externalFunctionsPluginProvider.fx_verifyWithEcdsaNistp256WithSha256(p_toBeVerifiedData, p_signature, p_ecdsaNistp256PublicKeyX, p_ecdsaNistp256PublicKeyY);
    }
    
    /**
     * @desc    Produce a new public/private key pair based on Elliptic Curve Digital Signature Algorithm (ECDSA) algorithm
     * @param   p_privateKey    The new private key value
     * @param   p_publicKeyX    The new public key value (x coordinate)
     * @param   p_publicKeyX    The new public key value (y coordinate)
     * @return  true on success, false otherwise
     */
    @Override
    public BooleanValue fx_generateKeyPair(OctetstringValue p_privateKey, OctetstringValue p_publicKeyX, OctetstringValue p_publicKeyY) {
        return _externalFunctionsPluginProvider.fx_generateKeyPair(p_privateKey, p_publicKeyX, p_publicKeyY);
    }
    
    /**
     * @desc    Check that given polygon doesn't have neither self-intersections nor holes.
     * @param   p_region   Polygonal Region
     * @return  true on success, false otherwise
     */
    @Override
    public BooleanValue fx_isValidPolygonalRegion(RecordOfValue p_region) {
        return _externalFunctionsPluginProvider.fx_isValidPolygonalRegion(p_region);
    }
    
    @Override
    public BooleanValue fx_isPolygonalRegionInside(RecordOfValue p_parent, RecordOfValue p_region) {
        return _externalFunctionsPluginProvider.fx_isPolygonalRegionInside(p_parent, p_region);
    }
    
    @Override
    public BooleanValue fx_isLocationInsideCircularRegion(RecordValue p_region, RecordValue p_location) {
        return _externalFunctionsPluginProvider.fx_isLocationInsideCircularRegion(p_region, p_location);
    }
    
    @Override
    public BooleanValue fx_isLocationInsideRectangularRegion(RecordOfValue p_region, RecordValue p_location) {
        return _externalFunctionsPluginProvider.fx_isLocationInsideRectangularRegion(p_region, p_location);
    }
    
    @Override
    public BooleanValue fx_isLocationInsidePolygonalRegion(final RecordOfValue p_region, final RecordValue p_location) {
        return _externalFunctionsPluginProvider.fx_isLocationInsidePolygonalRegion(p_region, p_location);
    }
    
    @Override
    public BooleanValue fx_isLocationInsideIdentifiedRegion(RecordValue p_region, RecordValue p_location) {
        return _externalFunctionsPluginProvider.fx_isLocationInsideIdentifiedRegion(p_region, p_location);
    }

    @Override
    public FloatValue fx_dms2dd(IntegerValue p_degrees, IntegerValue p_minutes, FloatValue p_seconds, OctetstringValue p_latlon) {
        return _externalFunctionsPluginProvider.fx_dms2dd(p_degrees, p_minutes, p_seconds, p_latlon);
    }

    @Override
    public BooleanValue fx_loadCertificates(CharstringValue p_rootDirectory, CharstringValue p_configId) {
        return _externalFunctionsPluginProvider.fx_loadCertificates(p_rootDirectory, p_configId);
    }

    @Override
    public BooleanValue fx_unloadCertificates() {
        return _externalFunctionsPluginProvider.fx_unloadCertificates();
    }

    @Override
    public BooleanValue fx_readCertificate(CharstringValue p_certificateName, OctetstringValue p_certificate) {
        return _externalFunctionsPluginProvider.fx_readCertificate(p_certificateName, p_certificate);
    }

    @Override
    public BooleanValue fx_readCertificateDigest(CharstringValue p_certificateName, OctetstringValue p_digest) {
        return _externalFunctionsPluginProvider.fx_readCertificateDigest(p_certificateName, p_digest);
    }

    @Override
    public BooleanValue fx_readSigningKey(CharstringValue p_certificateName, OctetstringValue p_privateKey) {
        return _externalFunctionsPluginProvider.fx_readSigningKey(p_certificateName, p_privateKey);
    }

    @Override
    public BooleanValue fx_readEncryptingKey(CharstringValue p_certificateName, OctetstringValue p_privateKey) {
        return _externalFunctionsPluginProvider.fx_readEncryptingKey(p_certificateName, p_privateKey);
    }
    
}
