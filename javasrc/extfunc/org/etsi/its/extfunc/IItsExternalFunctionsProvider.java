/**
 * @author	STF 424_ITS_Test_Platform
 * @version	$id$
 */
package org.etsi.its.extfunc;

import org.etsi.ttcn.tci.BooleanValue;
import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.tci.FloatValue;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.tci.OctetstringValue;
import org.etsi.ttcn.tci.RecordOfValue;
import org.etsi.ttcn.tci.RecordValue;

/** 
 * This interface provides the list of the TTCN-3 external function to be implemented
 * 
 * Refer to TTCN-3 modules LibItsGeoNetworking_Functions and LibItsCommon_Functions
 */
public interface IItsExternalFunctionsProvider {

	/**
	 * This external function gets the current time
	 * @return The current time in Epoch format
	 * 
	 * TTCN-3 signature:
	 * external function fx_getCurrentTime() return TimestampIts;
	 */
	public IntegerValue fx_getCurrentTime();

	/**
	 * This external function gets the current time
	 * @param p_latitudeA Latitude of node A
	 * @param p_longitudeA Longitude of node A
	 * @param p_latitudeB Latitude of node B
	 * @param p_longitudeB Longitude of node B
	 * @return The current time in Epoch format
	 * 
	 * TTCN-3 signature:
	 * external function fx_computeDistance(in UInt32 p_latitudeA, in UInt32 p_longitudeA, in UInt32 p_latitudeB, in UInt32 p_longitudeB) return float;
	 */
	public FloatValue fx_computeDistance(final IntegerValue p_latitudeA, final IntegerValue p_longitudeA, final IntegerValue p_latitudeB, final IntegerValue p_longitudeB);

	/**
	 * External function to compute a position using a reference position, a distance and an orientation 
	 * @param p_iutLongPosVector Reference position
	 * @param p_distance Distance to the reference position (in meter)
	 * @param p_orientation Direction of the computed position (0 to 359; 0 means North)
	 * @param p_latitude Computed position's latitude
	 * @param p_longitude Computed position's longitude
	 * 
	 * TTCN-3 signature:
	 * external function fx_computePositionUsingDistance(in UInt32 p_refLatitude, in Uint32 p_refLongitude, in integer p_distance, in integer p_orientation, out UInt32 p_latitude, out UInt32 p_longitude);
	 */
	public void fx_computePositionUsingDistance(final IntegerValue p_refLatitude, final IntegerValue p_refLongitude, final IntegerValue p_distance, final IntegerValue p_orientation, IntegerValue p_latitude, IntegerValue p_longitude);
	
	/**
     * External function to compute radius of a given circular area
     * @param   p_squareMeters  Square meters of an circular area
     * @return  Computed radius in meters
     */
    public FloatValue fx_computeRadiusFromCircularArea(final FloatValue p_squareMeters);
	
	/**
     * External function to compute timestamp based on current time
     * @return  Unix-Epoch-Time mod 2^32
     * 
     * TTCN-3 signature:
     * external function fx_computeGnTimestamp() return UInt32;
     */
     public IntegerValue fx_computeGnTimestamp();
     
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
     public OctetstringValue fx_computeIPv6CheckSum(
             final OctetstringValue p_sourceAddress, 
             final OctetstringValue p_destinationAddress, 
             final IntegerValue p_payloadLength, 
             final OctetstringValue p_payload, 
             final IntegerValue p_nextHdr 
     );
     
     public OctetstringValue xf_parseIpv6Address(final CharstringValue p_textIpv6Address);
     
     /**
      * @desc    Produces a 256-bit (32-byte) hash value
      * @param   p_toBeHashedData Data to be used to calculate the hash value
      * @return  The hash value
      */
     public OctetstringValue fx_hashWithSha256(final OctetstringValue p_toBeHashedData);
     
     /**
      * @desc    Produces a Elliptic Curve Digital Signature Algorithm (ECDSA) signaturee
      * @param   p_toBeSignedData    The data to be signed
      * @param   p_privateKey        The private key
      * @return  The signature value
      */
     public OctetstringValue fx_signWithEcdsaNistp256WithSha256(final OctetstringValue p_toBeSignedData, final OctetstringValue/*IntegerValue*/ p_privateKey);
     
     /**
      * @desc    Verify the signature of the specified data
      * @param   p_toBeVerifiedData          The data to be verified
      * @param   p_signature                 The signature
      * @param   p_ecdsaNistp256PublicKeyX   The public key (x coordinate)
      * @param   p_ecdsaNistp256PublicKeyY   The public key (y coordinate)
      * @return  true on success, false otherwise
      */
     public BooleanValue fx_verifyWithEcdsaNistp256WithSha256(final OctetstringValue p_toBeVerifiedData, final OctetstringValue p_signature, final OctetstringValue p_ecdsaNistp256PublicKeyX, final OctetstringValue p_ecdsaNistp256PublicKeyY);
     
     /**
      * @desc    Produce a new public/private key pair based on Elliptic Curve Digital Signature Algorithm (ECDSA) algorithm
      * @param   p_privateKey    The new private key value
      * @param   p_publicKeyX    The new public key value (x coordinate)
      * @param   p_publicKeyX    The new public key value (y coordinate)
      * @return  true on success, false otherwise
      */
     public BooleanValue fx_generateKeyPair(OctetstringValue/*IntegerValue*/ p_privateKey, OctetstringValue p_publicKeyX, OctetstringValue p_publicKeyY);
     
     /**
      * @desc    Check that given polygon doesn't have neither self-intersections nor holes.
      * @param   p_region   Polygonal Region
      * @return  true on success, false otherwise
      */
     public BooleanValue fx_isValidPolygonalRegion(final RecordOfValue p_region);
     
     /**
      * @desc Check if a polygonal regin is inside another one
      * @param p_parent  The main polygonal region
      * @param p_region  The polygonal region to be included
      * @return true on success, false otherwise
      * @verdict Unchanged
      */
     public BooleanValue fx_isPolygonalRegionInside(final RecordOfValue p_parent, final RecordOfValue p_region);
     
     /**
      * @desc Check that the location is inside a circular region
      * @param p_region      The circular region to consider
      * @param p_location    The device location
      * @return true on success, false otherwise
      * @verdict Unchanged
      */
     public BooleanValue fx_isLocationInsideCircularRegion(final RecordValue p_region, final RecordValue p_location);
     
     /**
      * @desc Check that the location is inside a rectangular region
      * @param p_region      The rectangular region to consider
      * @param p_location    The device location
      * @return true on success, false otherwise
      * @verdict Unchanged
      */
     public BooleanValue fx_isLocationInsideRectangularRegion(final RecordOfValue p_region, final RecordValue p_location);
     
     /**
      * @desc Check that the location is inside a polygonal region
      * @param p_region      The polygonal region to consider
      * @param p_location    The device location
      * @return true on success, false otherwise
      * @verdict Unchanged
      */
     public BooleanValue fx_isLocationInsidePolygonalRegion(final RecordOfValue p_region, final RecordValue p_location);
     
     /**
      * @desc Check if the location is inside an identified region
      * @param p_region      The identified region to consider
      * @param p_location    The device location
      * @return true on success, false otherwise
      * @verdict Unchanged
      */
     public BooleanValue fx_isLocationInsideIdentifiedRegion(final RecordValue p_region, final RecordValue p_location);
     
     /**
      * @desc Convert a spacial coordinate from DMS to Dms
      * @param p_degrees The degrees (D)
      * @param p_minutes The minutes (M)
      * @param p_seconds The seconds (S)
      * @param p_latlon  The latitude/longitude: (N|S|E|W)
      * @return The decimal coordinate on success, 0.0, otherwise
      * @verdict Unchanged
      */
     public FloatValue fx_dms2dd(final IntegerValue p_degrees, final IntegerValue p_minutes, final FloatValue p_seconds, final OctetstringValue p_latlon);
     
     /**
      * @desc    Load in memory cache the certificates available in the specified directory
      * @param   p_rootDirectory Root directory to access to the certificates identified by the certificate ID
      * @param   p_configId      A configuration identifier
      * @return  true on success, false otherwise
      */
     public BooleanValue fx_loadCertificates(final CharstringValue p_rootDirectory, final CharstringValue p_configId);
     
     /**
      * @desc    Unload from memory cache the certificates
      * @return  true on success, false otherwise
      */
     public BooleanValue fx_unloadCertificates();
     
     /**
      * @desc    Read the specified certificate
      * @param   p_certificateId the certificate identifier
      * @param   p_certificate   the expected certificate
      * @return  true on success, false otherwise
      */
     public BooleanValue fx_readCertificate(final CharstringValue p_certificateId, OctetstringValue p_certificate);
     
     /**
      * @desc    Read the specified certificate digest
      * @param   p_certificateId the certificate identifier
      * @param   p_certificate   the expected certificate
      * @return  true on success, false otherwise
      */
     public BooleanValue fx_readCertificateDigest(final CharstringValue p_certificateId, OctetstringValue p_certificate);
     
     /**
      * @desc    Read the signing private key for the specified certificate
      * @param   p_keysId the keys identifier
      * @param   p_key    the signing private key
      * @return  true on success, false otherwise
      */
     public BooleanValue fx_readSigningKey(final CharstringValue p_keysId, OctetstringValue p_key);
     
     /**
      * @desc    Read the encrypting private key for the specified certificate
      * @param   p_keysId  the keys identifier
      * @param   p_key     the encrypt private key
      * @return  true on success, false otherwise
      */
     public BooleanValue fx_readEncryptingKey(final CharstringValue p_keysId, OctetstringValue p_key);

} // End of interface IItsExternalFunctionsProvider
