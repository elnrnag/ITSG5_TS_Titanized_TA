/**
 * @authorSTF 424_ITS_Test_Platform
 * @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/extfunc/org/etsi/its/extfunc/ItsExternalFunctionsProvider.java $
 *             $Id: ItsExternalFunctionsProvider.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.its.extfunc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.etsi.certificates.CertificatesIOFactory;
import org.etsi.certificates.io.ICertificatesIO;
import org.etsi.codec.ITciCDWrapper;
import org.etsi.codec.TciCDWrapperFactory;
import org.etsi.common.ByteHelper;
import org.etsi.geodesic.DMS;
import org.etsi.geodesic.Positioning;
import org.etsi.geodesic.WGS84;
import org.etsi.ttcn.tci.BooleanValue;
import org.etsi.ttcn.tci.CharstringValue;
import org.etsi.ttcn.tci.EnumeratedValue;
import org.etsi.ttcn.tci.FloatValue;
import org.etsi.ttcn.tci.IntegerValue;
import org.etsi.ttcn.tci.OctetstringValue;
import org.etsi.ttcn.tci.RecordOfValue;
import org.etsi.ttcn.tci.RecordValue;

import de.fraunhofer.sit.c2x.CryptoLib;
import de.fraunhofer.sit.c2x.EcdsaP256KeyPair;

/**
 * This class implements external ITS function
 * 
 * See TTCN-3 modules LibItsGeoNetworking_Functions and LibItsCommon_Functions
 * 
 */
public class ItsExternalFunctionsProvider implements IItsExternalFunctionsProvider {

    /**
     * Module version
     */
    public static final String Version = "1.0.0.0";

    /**
     * Logger instance
     */
    private final static Logger _logger = Logger.getLogger("org.etsi.its");

    /**
     * Unique instance of TciCDWrapper class
     */
    private ITciCDWrapper _tcicdWrapper;

    /**
     * Constant factor used for distance computation
     */
    private static final double earthRadius = 6378137;
    private static final double rbis = earthRadius * Math.PI / 180;

    private static final long ITS_REF_TIME = 1072915200000L;

    /**
     * Reference to the ePassport files manager
     */
    private ICertificatesIO _certCache = CertificatesIOFactory.getInstance();
    
    /**
     * Default ctor
     */
    public ItsExternalFunctionsProvider() {
        _logger.entering("ItsExternalFunctionsProvider", "Constructor",
                String.format("version:%s", Version));

        _tcicdWrapper = TciCDWrapperFactory.getTciCDInstance();
    }

    /**
     * This external function gets the current time
     * 
     * @return The current time in ITS format
     * 
     *         TTCN-3 signature: external function fx_getCurrentTime() return
     *         TimestampIts;
     */
    @Override
    public synchronized IntegerValue fx_getCurrentTime() {
        _logger.entering("ItsExternalFunctionsProvider", "fx_getCurrentTime");
        
        String datestr="01/01/2004 00:00:00 +0000";
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss Z");
        IntegerValue now = null;
        try {
//            System.out.println("ItsExternalFunctionsProvider.fx_getCurrentTime: " + ((java.util.Date)formatter.parse(datestr)).getTime());
            now = _tcicdWrapper.setInteger(
                new BigInteger(
                    1, 
                    ByteHelper.longToByteArray(System.currentTimeMillis() - ((java.util.Date)formatter.parse(datestr)).getTime(), Long.SIZE / Byte.SIZE)
                )
            );
        } catch (ParseException e) {
            now = _tcicdWrapper.setInteger(0);
        }
        
        _logger.exiting("ItsExternalFunctionsProvider", "fx_getCurrentTime", String.format("%10d", _tcicdWrapper.getBigInteger(now))); 
        return now;
    } // End of method fx_getCurrentTime

    /**
     * This external function gets the current time
     * 
     * @param p_latitudeA
     *            Latitude of node A
     * @param p_longitudeA
     *            Longitude of node A
     * @param p_latitudeB
     *            Latitude of node B
     * @param p_longitudeB
     *            Longitude of node B
     * @return The current time in Epoch format
     * 
     *         TTCN-3 signature: external function fx_computeDistance(in UInt32
     *         p_latitudeA, in UInt32 p_longitudeA, in UInt32 p_latitudeB, in
     *         UInt32 p_longitudeB) return float;
     */
    @Override
    public synchronized FloatValue fx_computeDistance(
            final IntegerValue p_latitudeA, final IntegerValue p_longitudeA,
            final IntegerValue p_latitudeB, final IntegerValue p_longitudeB) {
//        _logger.entering("ItsExternalFunctionsProvider", "fx_computeDistance",
//                String.format("%d, %d, %d, %d", 
//                        p_latitudeA.getInteger(),
//                        p_longitudeA.getInteger(), 
//                        p_latitudeB.getInteger(),
//                        p_longitudeB.getInteger()));

        // Initialise the returned value
        FloatValue dist = _tcicdWrapper.getFloat();

        double dlat = (
            new Double(_tcicdWrapper.getInteger(p_latitudeB)) - 
            new Double(_tcicdWrapper.getInteger(p_latitudeA))
        ) / 10000000;
        double dlong = (
            new Double(_tcicdWrapper.getInteger(p_longitudeB)) - 
            new Double(_tcicdWrapper.getInteger(p_longitudeA))
        ) / 10000000;

        long d = Math.round(Math.sqrt(Math.pow(dlat * rbis, 2) + Math.pow(dlong * rbis * Math.cos(dlat), 2)));

        dist.setFloat(d);
//        System.out.println("Distance: " + d);

        return dist;
    } // End of method fx_computeDistance

    /**
     * External function to compute a position using a reference position, a
     * distance and an orientation
     * 
     * @param p_iutLongPosVector
     *            Reference position
     * @param p_distance
     *            Distance to the reference position (in meter)
     * @param p_orientation
     *            Direction of the computed position (0 to 359; 0 means North)
     * @param p_latitude
     *            Computed position's latitude
     * @param p_longitude
     *            Computed position's longitude
     * 
     *            TTCN-3 signature: external function
     *            fx_computePositionUsingDistance(in LongPosVector
     *            p_iutLongPosVector, in integer p_distance, in integer
     *            p_orientation, out UInt32 p_latitude, out UInt32 p_longitude);
     */
    @Override
    public synchronized void fx_computePositionUsingDistance(
            final IntegerValue p_refLatitude,
            final IntegerValue p_refLongitude, final IntegerValue p_distance,
            final IntegerValue p_orientation, IntegerValue p_latitude,
            IntegerValue p_longitude) {
//        _logger.entering(
//                "ItsExternalFunctionsProvider",
//                "fx_computePositionUsingDistance",
//                String.format("%d, %d", p_distance.getInteger(),
//                        p_orientation.getInteger()));

        double angularD = new Double(_tcicdWrapper.getInteger(p_distance)) / earthRadius;
        double radHeading = new Double(_tcicdWrapper.getInteger(p_orientation)) * Math.PI / 180;

        // Convert to rad
        double lat1 = (new Double(_tcicdWrapper.getInteger(p_refLatitude)) / 10000000) * Math.PI / 180;
        double long1 = (new Double(_tcicdWrapper.getInteger(p_refLongitude)) / 10000000) * Math.PI / 180;

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(angularD)
                + Math.cos(lat1) * Math.sin(angularD) * Math.cos(radHeading));
        double long2 = long1
                + Math.atan2(
                        Math.sin(radHeading) * Math.sin(angularD)
                                * Math.cos(lat1),
                        Math.cos(angularD) - Math.sin(lat1) * Math.sin(lat2));

        // normalise to -180...+180
        long2 = (long2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

        // convert to 1/10 of microdegrees
        Long rlat2 = Math.round(lat2 * 10000000 / Math.PI * 180);
        Long rlong2 = Math.round(long2 * 10000000 / Math.PI * 180);

        // The out parameter needs to be set on the object level
        _tcicdWrapper.setInteger(p_latitude, rlat2.intValue());
        _tcicdWrapper.setInteger(p_longitude, rlong2.intValue());
//        p_latitude = _tcicdWrapper.setInteger((int) rlat2);
//        p_longitude = _tcicdWrapper.setInteger((int) rlong2);

    } // End of method fx_computePositionUsingDistance

    @Override
    public FloatValue fx_computeRadiusFromCircularArea(FloatValue p_squareMeters) {

        // Initialise the returned value
        FloatValue radius = _tcicdWrapper.getFloat();
        
        radius.setFloat((float) Math.sqrt(p_squareMeters.getFloat() / Math.PI));
        
        return radius;
    }
    
    /**
     * External function to compute timestamp based on current time
     * 
     * @return Unix-Epoch-Time mod 2^32
     * 
     *         TTCN-3 signature: external function fx_computeGnTimestamp()
     *         return UInt32;
     */
    @Override
    public IntegerValue fx_computeGnTimestamp() {
        
        IntegerValue timestamp = _tcicdWrapper.setInteger(
                new BigInteger(
                    1,
                    ByteHelper.longToByteArray((System.currentTimeMillis() - ITS_REF_TIME) % (long)Math.pow(2,32), Long.SIZE / Byte.SIZE) 
                )
            );
        
        return timestamp;
    }

    /**
     * @desc Calculate ICMPv6 checksum on pseudo header according RFC 4443 -
     *       Clause 2.3
     * @param p_sourceAddress
     *            Source address (128 bits),
     * @param p_destinationAddress
     *            Destination address (128 bits)
     * @param p_payloadLength
     *            Upper-Layer Packet Length (32 bits)
     * @param p_payload
     *            Upper-Layer payload
     * @param p_nextHdr
     *            Next header value (e.g. 0x3a for ICMPv6) (8bits)
     * @return The checksum value (16bits)
     * @see RFC 2460 IPv6 Specification
     * 
     *      TTCN-3 signature: external function fx_computeIPv6CheckSum( in
     *      template (value) Ipv6Address p_sourceAddress, in template (value)
     *      Ipv6Address p_destinationAddress, in template (value) integer
     *      p_payloadLength, in template (value) octetstring p_payload, in
     *      template (value) integer p_nextHdr ) return Oct2;
     * 
     *      <pre>
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
            final OctetstringValue p_sourceAddress,
            final OctetstringValue p_destinationAddress,
            final IntegerValue p_payloadLength,
            final OctetstringValue p_payload, final IntegerValue p_nextHdr) {
        _logger.entering("ItsExternalFunctionsProvider",
                "fx_computeIPv6CheckSum");

        // Build the pseudo header according RFC 2460 - Clause 8.1
        ByteArrayOutputStream pseudoheader = new ByteArrayOutputStream();
        // Source address (128bits)
        int i = 0;
        for (; i < p_sourceAddress.getLength(); i++) {
            pseudoheader.write(p_sourceAddress.getOctet(i));
        } // End of 'for' loop
            // Destination address (128bits)
        for (i = 0; i < p_destinationAddress.getLength(); i++) {
            pseudoheader.write(p_destinationAddress.getOctet(i));
        } // End of 'for' loop
        try {
            // Upper-Layer Packet Length (32bits)
            pseudoheader.write(ByteHelper.intToByteArray(_tcicdWrapper.getInteger(p_payloadLength), 4));
            // Checksum set to 0 (24bits)
            pseudoheader.write(ByteHelper.intToByteArray(0, 3));
            // Next header (8bits)
            pseudoheader.write((byte) _tcicdWrapper.getInteger(p_nextHdr));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Add the payload
        for (i = 0; i < p_payload.getLength(); i++) {
            pseudoheader.write(p_payload.getOctet(i));
        } // End of 'for' loop

        i = 0;
        int length = pseudoheader.size();
        byte[] buffer = pseudoheader.toByteArray();
        ByteHelper
                .dump("ItsExternalFunctionsProvider.fx_computeIPv6CheckSum: pseaudo header",
                        buffer);
        long sum = 0;
        while (length > 0) {
            sum += (buffer[i++] & 0xff) << 8;
            if ((--length) == 0) {
                break;
            }
            sum += (buffer[i++] & 0xff);
            --length;
        }
        sum = (~((sum & 0xFFFF) + (sum >> 16))) & 0xFFFF;
        _logger.info(String
                .format("ItsExternalFunctionsProvider.fx_computeIPv6CheckSum: finalSum=%d",
                        sum));
        // Set the return value
        OctetstringValue checksum = _tcicdWrapper.getOctetstring();
        checksum.setLength(2);
        checksum.setOctet(0, (byte) ((byte) (sum >> 8) & 0xff));
        checksum.setOctet(1, (byte) (sum & 0x00ff));

        _logger.exiting("ItsExternalFunctionsProvider",
                "fx_computeIPv6CheckSum", checksum); // FIXME Check which method
                                                        // to call for logging
        return checksum;
    }

    @Override
    public OctetstringValue xf_parseIpv6Address(
            CharstringValue p_textIpv6Address) {

        byte[] hexIpv6Address = null;

        try {
            InetAddress ipv6Address = InetAddress.getByName(p_textIpv6Address
                    .getString());
            hexIpv6Address = ipv6Address.getAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        OctetstringValue result = _tcicdWrapper.getOctetstring();
        result.setLength(hexIpv6Address.length);
        for (int i = 0; i < hexIpv6Address.length; i++) {
            result.setOctet(i, hexIpv6Address[i]);
        }
        return result;
    }

    /**
     * @desc    Produces a 256-bit (32-byte) hash value
     * @param   p_toBeHashedData Data to be used to calculate the hash value
     * @return  The hash value
     */
    public OctetstringValue fx_hashWithSha256(final OctetstringValue p_toBeHashedData) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_hashWithSha256");
        
        byte[] toBeHashedData = new byte[p_toBeHashedData.getLength()];
        for (int i = 0; i < toBeHashedData.length; i++) {
            toBeHashedData[i] = (byte) p_toBeHashedData.getOctet(i);
        } // End 'for' statement
        byte[] hash = CryptoLib.hashWithSha256(toBeHashedData);
        
        OctetstringValue result = _tcicdWrapper.getOctetstring();
        result.setLength(hash.length);
        for (int i = 0; i < hash.length; i++) {
            result.setOctet(i, hash[i]);
        } // End 'for' statement
        return result;
    }
    
    /**
     * @desc    Produces a Elliptic Curve Digital Signature Algorithm (ECDSA) signaturee
     * @param   p_toBeSignedData    The data to be signed
     * @param   p_privateKey        The private key
     * @return  The signature value
     */
    public OctetstringValue fx_signWithEcdsaNistp256WithSha256(final OctetstringValue p_toBeSignedData, final OctetstringValue/*IntegerValue*/ p_privateKey) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_signWithEcdsaNistp256WithSha256");
        
        byte[] toBeSignedData = new byte[p_toBeSignedData.getLength()];
        for (int i = 0; i < toBeSignedData.length; i++) {
            toBeSignedData[i] = (byte) p_toBeSignedData.getOctet(i);
        } // End 'for' statement
        byte[] privateKey = new byte[p_privateKey.getLength()];
        for (int i = 0; i < privateKey.length; i++) {
            privateKey[i] = (byte) p_privateKey.getOctet(i);
        } // End 'for' statement
        byte[] signed;
        try {
//            System.out.println("fx_signWithEcdsaNistp256WithSha256: toBeSignedData=" + ByteHelper.byteArrayToString(toBeSignedData) + " - " + toBeSignedData.length);
//            System.out.println("fx_signWithEcdsaNistp256WithSha256: ts103097SignatureEncodedAsByteArray=" + new BigInteger(privateKey));
            signed = CryptoLib.signWithEcdsaNistp256WithSha256(toBeSignedData, new BigInteger(privateKey));
        } catch (Exception e) {
            e.printStackTrace();
            signed = new byte[] {};
        }
        
        OctetstringValue result = _tcicdWrapper.getOctetstring();
        result.setLength(signed.length);
        for (int i = 0; i < signed.length; i++) {
            result.setOctet(i, signed[i]);
        } // End 'for' statement
        return result;
    }
    
    /**
     * @desc    Verify the signature of the specified data
     * @param   p_toBeVerifiedData          The data to be verified
     * @param   p_signature                 The signature
     * @param   p_ecdsaNistp256PublicKeyX   The public key (x coordinate)
     * @param   p_ecdsaNistp256PublicKeyY   The public key (y coordinate)
     * @return  true on success, false otherwise
     */
    public BooleanValue fx_verifyWithEcdsaNistp256WithSha256(final OctetstringValue p_toBeVerifiedData, final OctetstringValue p_signature, final OctetstringValue p_ecdsaNistp256PublicKeyX, final OctetstringValue p_ecdsaNistp256PublicKeyY) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_verifyWithEcdsaNistp256WithSha256");
        
        BooleanValue result = _tcicdWrapper.getBoolean();
        result.setBoolean(false);
        
        byte[] toBeVerifiedData = new byte[p_toBeVerifiedData.getLength()];
        for (int i = 0; i < toBeVerifiedData.length; i++) {
            toBeVerifiedData[i] = (byte) p_toBeVerifiedData.getOctet(i);
        } // End 'for' statement
        byte[] ts103097SignatureEncodedAsByteArray = new byte[p_signature.getLength()];
        for (int i = 0; i < ts103097SignatureEncodedAsByteArray.length; i++) {
            ts103097SignatureEncodedAsByteArray[i] = (byte) p_signature.getOctet(i);
        } // End 'for' statement
        byte[] ecdsaNistp256PublicKeyX = new byte[p_ecdsaNistp256PublicKeyX.getLength()];
        for (int i = 0; i < ecdsaNistp256PublicKeyX.length; i++) {
            ecdsaNistp256PublicKeyX[i] = (byte) p_ecdsaNistp256PublicKeyX.getOctet(i);
        } // End 'for' statement
        byte[] ecdsaNistp256PublicKeyY = new byte[p_ecdsaNistp256PublicKeyY.getLength()];
        for (int i = 0; i < ecdsaNistp256PublicKeyY.length; i++) {
            ecdsaNistp256PublicKeyY[i] = (byte) p_ecdsaNistp256PublicKeyY.getOctet(i);
        } // End 'for' statement
        try {
//            System.out.println("fx_verifyWithEcdsaNistp256WithSha256: toBeVerifiedData=" + ByteHelper.byteArrayToString(toBeVerifiedData) + " - " + toBeVerifiedData.length);
//            System.out.println("fx_verifyWithEcdsaNistp256WithSha256: ts103097SignatureEncodedAsByteArray=" + ByteHelper.byteArrayToString(ts103097SignatureEncodedAsByteArray));
//            System.out.println("fx_verifyWithEcdsaNistp256WithSha256: ecdsaNistp256PublicKeyX=" + ByteHelper.byteArrayToString(ecdsaNistp256PublicKeyX));
//            System.out.println("fx_verifyWithEcdsaNistp256WithSha256: ecdsaNistp256PublicKeyY=" + ByteHelper.byteArrayToString(ecdsaNistp256PublicKeyY));
            boolean ret = CryptoLib.verifyWithEcdsaNistp256WithSha256(toBeVerifiedData, ts103097SignatureEncodedAsByteArray, ecdsaNistp256PublicKeyX, ecdsaNistp256PublicKeyY);
            result.setBoolean(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        _logger.exiting("ItsExternalFunctionsProvider", "fx_verifyWithEcdsaNistp256WithSha256");
        return result;
    }
    
    /**
     * @desc    Produce a new public/private key pair based on Elliptic Curve Digital Signature Algorithm (ECDSA) algorithm
     * @param   p_privateKey    The new private key value
     * @param   p_publicKeyX    The new public key value (x coordinate)
     * @param   p_publicKeyX    The new public key value (y coordinate)
     * @return  true on success, false otherwise
     */
    public BooleanValue fx_generateKeyPair(OctetstringValue/*IntegerValue*/ p_privateKey, OctetstringValue p_publicKeyX, OctetstringValue p_publicKeyY) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_generateKeyPair");
        
        BooleanValue result = _tcicdWrapper.getBoolean();
        result.setBoolean(true);
        
        EcdsaP256KeyPair ecdsaP256KeyPair = CryptoLib.generateKeyPair();
        byte[] ref = ecdsaP256KeyPair.getPrivateKey().toByteArray();
        p_privateKey.setLength(ref.length);
        for (int i = 0; i < p_privateKey.getLength(); i++) {
            p_privateKey.setOctet(i, ref[i]);
        } // End 'for' statement
        ref = ecdsaP256KeyPair.getPublicKeyX();
        p_publicKeyX.setLength(ref.length);
        for (int i = 0; i < ref.length; i++) {
            p_publicKeyX.setOctet(i, ref[i]);
        } // End 'for' statement
        ref = ecdsaP256KeyPair.getPublicKeyY();
        p_publicKeyY.setLength(ref.length);
        for (int i = 0; i < ref.length; i++) {
            p_publicKeyY.setOctet(i, ref[i]);
        } // End 'for' statement
        
        _logger.exiting("ItsExternalFunctionsProvider", "fx_generateKeyPair");
        return result;
    }
    
    /**
     * @desc    Check that given polygon doesn't have neither self-intersections nor holes.
     * @param   p_region   Polygonal Region
     * @return  true on success, false otherwise
     */
    public BooleanValue fx_isValidPolygonalRegion(final RecordOfValue p_region) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_isValidPolygonalRegion");
        
        // Setup arguments
        ArrayList<WGS84> polygonalArea = new ArrayList<WGS84>();
        for (int position = 0; position < p_region.getLength(); position++) {
            RecordValue rv = (RecordValue) p_region.getField(position);
            polygonalArea.add(
                new WGS84(
                    _tcicdWrapper.getInteger((IntegerValue)(rv.getField("latitude"))),
                    _tcicdWrapper.getInteger((IntegerValue)(rv.getField("longitude"))) 
            ));
        } // End of 'for' statement
        
        // Call Geodesic function
        BooleanValue result = _tcicdWrapper.getBoolean();
        result.setBoolean(Positioning.getInstance().isValidPolygonArea(polygonalArea));
        
        _logger.exiting("ItsExternalFunctionsProvider", "fx_isValidPolygonalRegion");
        return result;
    }
    
    @Override
    public BooleanValue fx_isPolygonalRegionInside(final RecordOfValue p_parent, final RecordOfValue p_region) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_isPolygonalRegionInside");
        
        // Setup arguments
        ArrayList<WGS84> parentArea = new ArrayList<WGS84>();
        for (int position = 0; position < p_parent.getLength(); position++) {
            RecordValue rv = (RecordValue) p_parent.getField(position);
            parentArea.add(
                new WGS84(
                    _tcicdWrapper.getInteger((IntegerValue)(rv.getField("latitude"))),
                    _tcicdWrapper.getInteger((IntegerValue)(rv.getField("longitude"))) 
            ));
        } // End of 'for' statement
        ArrayList<WGS84> regionArea = new ArrayList<WGS84>();
        for (int position = 0; position < p_region.getLength(); position++) {
            RecordValue rv = (RecordValue) p_region.getField(position);
            regionArea.add(
                new WGS84(
                    _tcicdWrapper.getInteger((IntegerValue)(rv.getField("latitude"))),
                    _tcicdWrapper.getInteger((IntegerValue)(rv.getField("longitude"))) 
            ));
        } // End of 'for' statement
        
        // Call Geodesic function
        BooleanValue result = _tcicdWrapper.getBoolean();
        result.setBoolean(Positioning.getInstance().isPolygonalRegionInside(parentArea, regionArea));
        
        _logger.exiting("ItsExternalFunctionsProvider", "fx_isPolygonalRegionInside");
        return result;
    }

    @Override
    public BooleanValue fx_isLocationInsideCircularRegion(final RecordValue p_region, final RecordValue p_location) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_isLocationInsideCircularRegion");
        
        // Setup arguments
        RecordValue rv = (RecordValue)p_region.getField("center"); // Center
        WGS84 center = new WGS84(
            _tcicdWrapper.getInteger((IntegerValue)(rv.getField("latitude"))),
            _tcicdWrapper.getInteger((IntegerValue)(rv.getField("longitude"))) 
        );
        int radius = _tcicdWrapper.getInteger((IntegerValue)p_region.getField("radius"));
        WGS84 location = new WGS84( // Location
            _tcicdWrapper.getInteger((IntegerValue)(p_location.getField("latitude"))),
            _tcicdWrapper.getInteger((IntegerValue)(p_location.getField("longitude"))) 
        );
        
        // Call Geodesic function
        BooleanValue result = _tcicdWrapper.getBoolean();
        result.setBoolean(Positioning.getInstance().isLocationInsideCircularArea(location, center, radius));
        
        _logger.exiting("ItsExternalFunctionsProvider", "fx_isLocationInsideCircularRegion");
        return result;
    }
    
    @Override
    public BooleanValue fx_isLocationInsideRectangularRegion(final RecordOfValue p_region, final RecordValue p_location) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_isLocationInsideRectangularRegion");
        
        // Setup arguments
        ArrayList< ArrayList<WGS84> > polygonalAreas = new ArrayList< ArrayList<WGS84> >();
        for (int position = 0; position < p_region.getLength(); position++) {
            RecordValue rectangleArea = (RecordValue)p_region.getField(position);
            // Retrieve corners
            RecordValue rv = (RecordValue)rectangleArea.getField("northwest"); // Upper left corner
            WGS84 upperLeftCorner = new WGS84(
                (long)_tcicdWrapper.getInteger((IntegerValue)(rv.getField("latitude"))),
                (long)_tcicdWrapper.getInteger((IntegerValue)(rv.getField("longitude"))) 
            );
            rv = (RecordValue)rectangleArea.getField("southeast"); // Lower right corner
            WGS84 lowerRightCorner = new WGS84(
                (long)_tcicdWrapper.getInteger((IntegerValue)(rv.getField("latitude"))),
                (long) _tcicdWrapper.getInteger((IntegerValue)(rv.getField("longitude"))) 
            );
            // Build the polygonal area
            ArrayList<WGS84> rectangularItem = new ArrayList<WGS84>();
            rectangularItem.add(upperLeftCorner);
            rectangularItem.add(
                new WGS84(
                    upperLeftCorner.getLatitude(),
                    lowerRightCorner.getLongitude(),
                    lowerRightCorner.getAltitude()
                )
            );
            rectangularItem.add(lowerRightCorner);
            rectangularItem.add(
                new WGS84(
                    lowerRightCorner.getLatitude(),
                    upperLeftCorner.getLongitude(),
                    upperLeftCorner.getAltitude()
                )
            );
            
            polygonalAreas.add(rectangularItem);
        } // End of 'for' statement
        
        WGS84 location = new WGS84( // Location
            _tcicdWrapper.getInteger((IntegerValue)(p_location.getField("latitude"))),
            _tcicdWrapper.getInteger((IntegerValue)(p_location.getField("longitude"))) 
        );
        
        // Call Geodesic function
        BooleanValue result = _tcicdWrapper.getBoolean();
        result.setBoolean(Positioning.getInstance().isLocationInsidePolygonalAreas(location, polygonalAreas));
        
        _logger.exiting("ItsExternalFunctionsProvider", "fx_isLocationInsideRectangularRegion");
        return result;
    }
    
    @Override
    public BooleanValue fx_isLocationInsidePolygonalRegion(final RecordOfValue p_region, final RecordValue p_location) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_isLocationInsidePolygonalRegion");
        
        // Setup arguments
        ArrayList<WGS84> polygonalArea = new ArrayList<WGS84>();
        for (int position = 0; position < p_region.getLength(); position++) {
            RecordValue rv = (RecordValue) p_region.getField(position);
            polygonalArea.add(
                new WGS84(
                    (long)_tcicdWrapper.getInteger((IntegerValue)(rv.getField("latitude"))),
                    (long)_tcicdWrapper.getInteger((IntegerValue)(rv.getField("longitude"))) 
            ));
//            System.out.println("fx_isLocationInsidePolygonalRegion: add " + polygonalArea.get(polygonalArea.size() - 1));
        } // End of 'for' statement
        WGS84 location = new WGS84( // Location
            (long)_tcicdWrapper.getInteger((IntegerValue)(p_location.getField("latitude"))),
            (long)_tcicdWrapper.getInteger((IntegerValue)(p_location.getField("longitude"))) 
        );
//        System.out.println("fx_isLocationInsidePolygonalRegion: location= " + location);
        
        // Call Geodesic function
        BooleanValue result = _tcicdWrapper.getBoolean();
        result.setBoolean(Positioning.getInstance().isLocationInsidePolygonalArea(location, polygonalArea));
        
        _logger.exiting("ItsExternalFunctionsProvider", "fx_isLocationInsidePolygonalRegion");
        return result;
    }
    
    @Override
    public BooleanValue fx_isLocationInsideIdentifiedRegion(final RecordValue p_region, final RecordValue p_location) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_isLocationInsideIdentifiedRegion");
        
        // Setup arguments
        int regionDictionary = ((EnumeratedValue) p_region.getField("region_dictionary")).getInt();
        int regionId = _tcicdWrapper.getInteger((IntegerValue)(p_region.getField("region_identifier")));
        long localRegion = _tcicdWrapper.getBigInteger((IntegerValue)(p_region.getField("local_region")));
        WGS84 location = new WGS84( // Location
            _tcicdWrapper.getInteger((IntegerValue)(p_location.getField("latitude"))),
            _tcicdWrapper.getInteger((IntegerValue)(p_location.getField("longitude"))) 
        );
        
        // Call Geodesic function
        BooleanValue result = _tcicdWrapper.getBoolean();
        result.setBoolean(Positioning.getInstance().isLocationInsideIdentifiedRegion(regionDictionary, regionId, localRegion, location));
        
        _logger.exiting("ItsExternalFunctionsProvider", "fx_isLocationInsideIdentifiedRegion");
        return result;
    }
    
    @Override
    public FloatValue fx_dms2dd(final IntegerValue p_degrees, final IntegerValue p_minutes, final FloatValue p_seconds, final OctetstringValue p_latlon) {
        _logger.entering("ItsExternalFunctionsProvider", "fx_dms2dd");
        
        // Call Geodesic function
        FloatValue result = _tcicdWrapper.getFloat();
        DMS location = new DMS( // Location
            _tcicdWrapper.getInteger(p_degrees), 
            _tcicdWrapper.getInteger(p_minutes),
            p_seconds.getFloat(),
            (char)p_latlon.getOctet(0)
        );
        result.setFloat((float) location.toDD());
        
        _logger.exiting("ItsExternalFunctionsProvider", "fx_dms2dd");
        return result;
    }
    
    /**
     * @desc    Load in memory cache the certificates available in the specified directory
     * @param   p_rootDirectory Root directory to access to the certificates identified by the certificate ID
     * @param   p_configId      A configuration identifier
     * @return  true on success, false otherwise
     */
    @Override
    public BooleanValue fx_loadCertificates(final CharstringValue p_rootDirectory, final CharstringValue p_configId) {
        
        BooleanValue result = _tcicdWrapper.getBoolean();
        result.setBoolean(_certCache.loadCertificates(p_rootDirectory.getString(), p_configId.getString()));
        
        return result;
    }
    
    /**
     * @desc    Unload from memory cache the certificates
     * @return  true on success, false otherwise
     */
    @Override
    public BooleanValue fx_unloadCertificates() {
        
        BooleanValue result = _tcicdWrapper.getBoolean();
        result.setBoolean(_certCache.unloadCertificates());
        
        return result;
    }
    
    /**
     * @desc    Read the specified certificate
     * @param   p_certificateId the certificate identifier
     * @param   p_certificate   the expected certificate
     * @return  true on success, false otherwise
     */
    @Override
    public BooleanValue fx_readCertificate(final CharstringValue p_certificateId, OctetstringValue p_certificate) {
        
        BooleanValue result = _tcicdWrapper.getBoolean();
        ByteArrayOutputStream certificate = new ByteArrayOutputStream();
        boolean b = _certCache.readCertificate(p_certificateId.getString(), certificate); 
        result.setBoolean(b);
        
        if(b){
        	byte[] value = certificate.toByteArray();
        	p_certificate.setLength(value.length);
        	if (value.length != 0) {
        		for (int i = 0; i < value.length; i++) {
        			p_certificate.setOctet(i, value[i]);
        		} // End 'for' statement
        	}
        }else{
        	p_certificate.setLength(0);
        }
        
        return result;
    }

    private static final byte[] ZERO32 = new byte[] {
    	0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  
    	0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  
    	0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  
    	0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00  
    };
    
    
    /**
     * @desc    Read the specified certificate digest
     * @param   p_certificateId the certificate identifier
     * @param   p_digest   the expected certificate digest
     * @return  true on success, false otherwise
     */
    @Override
    public BooleanValue fx_readCertificateDigest(final CharstringValue p_certificateId, OctetstringValue p_digest) {
        
        BooleanValue result = _tcicdWrapper.getBoolean();
        ByteArrayOutputStream digest = new ByteArrayOutputStream();
        boolean b = _certCache.readCertificateDigest(p_certificateId.getString(), digest); 
        result.setBoolean(b);
        
    	p_digest.setLength(8);
    	byte[] value;
        if(b){
        	value = digest.toByteArray();
        	if (value.length != 8) {
        		value = ZERO32;
        	}
        }else{
    		value = ZERO32;
        }        	
   		for (int i = 0; i < 8; i++) {
   			p_digest.setOctet(i, value[i]);
   		} // End 'for' statement
        return result;
    }
    
    /**
     * @desc    Read the signing private key for the specified certificate
     * @param   p_keysId the keys identifier
     * @param   p_key    the signing private key
     * @return  true on success, false otherwise
     */
    @Override
    public BooleanValue fx_readSigningKey(CharstringValue p_keysId, OctetstringValue p_key) {
        
        BooleanValue result = _tcicdWrapper.getBoolean();
        ByteArrayOutputStream key = new ByteArrayOutputStream();
        
        boolean b = _certCache.readSigningKey(p_keysId.getString(), key); 
        result.setBoolean(b);
        
    	p_key.setLength(32);

    	byte[] value;
    	if(b){
            value = key.toByteArray();
        	if(value.length != 32) {
            	value = ZERO32;
        	}
        }else{
        	value = ZERO32;
        }
        for (int i = 0; i < 32; i++) {
        	p_key.setOctet(i, value[i]);
	    }
        return result;
    }
    
    /**
     * @desc    Read the encrypting private key for the specified certificate
     * @param   p_keysId the keys identifier
     * @param   p_key    the encrypting private key
     * @return  true on success, false otherwise
     */
    @Override
    public BooleanValue fx_readEncryptingKey(CharstringValue p_keysId, OctetstringValue p_key) {
        
        BooleanValue result = _tcicdWrapper.getBoolean();
        ByteArrayOutputStream key = new ByteArrayOutputStream();
        
        boolean b = _certCache.readEncryptingKey(p_keysId.getString(), key); 
        result.setBoolean(b);
        
    	p_key.setLength(32);

    	byte[] value;
    	if(b){
            value = key.toByteArray();
        	if(value.length != 32) {
            	value = ZERO32;
        	}
        }else{
        	value = ZERO32;
        }
        for (int i = 0; i < 32; i++) {
        	p_key.setOctet(i, value[i]);
	    }
        return result;
    }
    
} // End of class ExternalFunctions

