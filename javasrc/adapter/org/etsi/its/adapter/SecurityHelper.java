/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/SecurityHelper.java $
 *              $Id: SecurityHelper.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.its.adapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.etsi.common.ByteHelper;

import de.fraunhofer.sit.c2x.CryptoLib;

public class SecurityHelper {
    
    private static SecurityHelper Instance = new SecurityHelper();
    
    public static SecurityHelper getInstance() { return Instance; }
    
    /**
     * Storage for received certificates
     */
    private Map<Long, ByteArrayOutputStream> _neighborsCertificates = null;
    
    private SecurityHelper() {
        _neighborsCertificates = new HashMap<Long, ByteArrayOutputStream>();
    }
    
    public byte[] size2tls(final int length) {
        byte[] result = null;
        if (length < 128) { // One byte length
            result = new byte[] { (byte)length }; 
        } else {
            long lv = length;
            long bitLen = bitLength(lv);
            long byteLen = byteLength(bitLen);
            long flags = (long) ((byteLen | 1) << (byteLen * Byte.SIZE - bitLength(byteLen) - 1));
            long len = (long) (byteLen << (byteLen * Byte.SIZE - bitLength(byteLen) - 1));
            if ((flags & lv) != 0) { // We can encode the length on the MSB part
                byteLen += 1;
                len = (long) (byteLen << (byteLen * Byte.SIZE - bitLength(byteLen)) - 1);
            }
            result = ByteHelper.longToByteArray((long)(lv | len), (int) byteLen);
        }
        
        return result;
    }
    
    public long tls2size(final ByteArrayInputStream buf) {
        // Sanity check
        if (buf.available() == 0) {
            return 0;
        }
        
        // Read the first byte
        byte msb =  (byte) buf.read();
        if ((msb & 0x80) == 0x00) { // Integer < 128
            return msb;
        } else {
            // Decode the length. The encoding of the length shall use at most 7 bits set to 1 (see Draft ETSI TS 103 097 V1.1.14 Clause 4.1    Presentation Language Table 1/8)
            byte bit;
            byte byteLen = 1;
            do {
                bit = (byte) ((byte) (msb << byteLen++) & 0x80);
            } while (bit != 0x00);
            // Set the IntX length
            byte[] data = new byte[byteLen - 1];
            buf.read(data, 0, byteLen - 1);
            byte[] length = ByteHelper.concat(new byte[] { msb }, data);
            length[0] &= (byte)(Math.pow(2.0, 8 - byteLen + 1) - 1);
            long lv = ByteHelper.byteArrayToLong(length);
            return lv;
        }
    }
    
    public long bitLength(final long value) {
        return (long) Math.ceil(Math.log(value) / Math.log(2));
    }
    
    public long byteLength(final long value) {
        double d = value; // Convert int to double
        return (long) Math.ceil(d / Byte.SIZE);
    }
    
    public byte[] checkSecuredProfileAndExtractPayload(final byte[] p_message, final int p_offset, final boolean p_enforceSecurityCheck, final int p_itsAidOther) {
//        System.out.println(">>> SecurityHelper.checkSecuredProfileAndExtractPayload: " + ByteHelper.byteArrayToString(p_message));
        
        ByteArrayInputStream decvalue = new ByteArrayInputStream(p_message, p_offset, p_message.length - p_offset);
        
        // Check version
        if (decvalue.read() != 2) {
            System.err.println("SecurityHelper.checkSecuredProfileAndExtractPayload: Drop packet - Wrong version number");
            if (p_enforceSecurityCheck) {
                // Drop it
                return null;
            }
        }
        // Extract header fields length and header fields
        long headerFieldsLength = tls2size(decvalue);
//        System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: headerFieldsLength:" + headerFieldsLength);
        byte[] headerFields = new byte[(int) headerFieldsLength];
        decvalue.read(headerFields, 0, (int) headerFieldsLength);
        ByteArrayOutputStream certificateKeys = new ByteArrayOutputStream();
        if (!checkHeaderfields(headerFields, certificateKeys, p_enforceSecurityCheck, p_itsAidOther)) {
            System.err.println("SecurityHelper.checkSecuredProfileAndExtractPayload: Drop packet - Wrong Headerfields");
            if (p_enforceSecurityCheck) {
                // Drop it
                return null;
            }
        }
        byte[] aaSigningPublicKeyX, aaSigningPublicKeyY;
        aaSigningPublicKeyX = ByteHelper.extract(certificateKeys.toByteArray(), 0, 32);
//        System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: aaSigningPublicKeyX:" + ByteHelper.byteArrayToString(aaSigningPublicKeyX));
        aaSigningPublicKeyY = ByteHelper.extract(certificateKeys.toByteArray(), 32, 32);
//        System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: aaSigningPublicKeyX:" + ByteHelper.byteArrayToString(aaSigningPublicKeyX));
//        System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: headerFields:" + ByteHelper.byteArrayToString(headerFields));
        // Extract payload, decvalue is updated with the payload
        if (decvalue.read() != 1) {
            System.err.println("SecurityHelper.checkSecuredProfileAndExtractPayload: Drop packet - Wrong Payload type");
            if (p_enforceSecurityCheck) {
                // Drop it
                return null;
            }
        }
        long payloadLength = tls2size(decvalue);
//        System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: payloadLength:" + payloadLength);
        byte[] payload = new byte[(int) payloadLength];
        decvalue.read(payload, 0, (int) payloadLength);
//        System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: payload:" + ByteHelper.byteArrayToString(payload));
        if (p_enforceSecurityCheck) { // Extract Secure Trailer
            long secureTrailerLength = tls2size(decvalue);
            byte[] secureTrailer = new byte[(int) secureTrailerLength];
            decvalue.read(secureTrailer, 0, secureTrailer.length);
            ByteArrayOutputStream signature = new ByteArrayOutputStream();
            if (!extractMessageSignature(secureTrailer, signature)) {
                // Drop it
                System.err.println("SecurityHelper.checkSecuredProfileAndExtractPayload: Drop packet - Wrong Signatures");
                return null;
            }
//    //        System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: signature:" + ByteHelper.byteArrayToString(signature.toByteArray()));
            // Build signed data
            byte[] toBeVerifiedData = ByteHelper.extract(
                p_message, 
                p_offset, 
                p_message.length - (int)(p_offset + secureTrailerLength - 1 /* Exclude signature structure but keep signature type and signature length */)
            );
//    //        System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload:" + ByteHelper.byteArrayToString(toBeVerifiedData));
            // Calculate Digest digest from the buffer toBeVerifiedData
            byte[] hash = CryptoLib.hashWithSha256(toBeVerifiedData);
            boolean result;
            try {
                result = CryptoLib.verifyWithEcdsaNistp256WithSha256(
                    hash, 
                    signature.toByteArray(),
                    aaSigningPublicKeyX, 
                    aaSigningPublicKeyY
                );
//        //        System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: Verify signature: " + new Boolean(result));
                if (!result) {
                    // Drop packet
//                    System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: toBeVerifiedData   :" + ByteHelper.byteArrayToString(toBeVerifiedData));
//                    System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: Hash               :" + ByteHelper.byteArrayToString(hash));
//                    System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: signature          :" + ByteHelper.byteArrayToString(signature.toByteArray()));
//                    System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: aaSigningPublicKeyX:" + ByteHelper.byteArrayToString(aaSigningPublicKeyX));
//                    System.out.println("SecurityHelper.checkSecuredProfileAndExtractPayload: aaSigningPublicKeyY:" + ByteHelper.byteArrayToString(aaSigningPublicKeyY));
                    System.err.println("SecurityHelper.checkSecuredProfileAndExtractPayload: Drop packet - Invalid signature");
                    return null;
                }
                
                return payload;
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Drop packet
            System.err.println("<<< SecurityHelper.checkSecuredProfileAndExtractPayload: dropped");
            return null;
        }
        
        return payload;
    }
    
    public boolean checkHeaderfields(final byte[] p_headerfields, final ByteArrayOutputStream p_keys, final boolean p_enforceSecurityCheck, final int p_itsAidOther) { 
//        System.out.println(">>> SecurityHelper.checkHeaderfields: " + ByteHelper.byteArrayToString(p_headerfields));
        
        // Sanity check
        if (p_headerfields.length == 0) {
            System.err.println("SecurityHelper.checkHeaderfields: Drop packet - Invalid header fields");
            return false;
        }
        // Extract digest or certificate
        int signerInfoTypeIndex = 0;
        if (
            ((p_headerfields[signerInfoTypeIndex] & 0x80) != 0x80) ||  // SignerInfo Type: certificate digest with ecdsap256 (1)
            (
                (p_headerfields[signerInfoTypeIndex + 1] != 0x01) &&   // SignerInfo Type: certificate digest with ecdsap256 (1)
                (p_headerfields[signerInfoTypeIndex + 1] != 0x02) &&   // SignerInfo Type: certificate (2)
                (p_headerfields[signerInfoTypeIndex + 1] != 0x03)      // SignerInfo Type: certificate chain (3)
            )
        ) {
            System.err.println("SecurityHelper.checkHeaderfields: Drop packet - Certificate");
            if (p_enforceSecurityCheck) { 
                // Drop it
                return false;
            }
        }
        signerInfoTypeIndex += 1;
        if (p_headerfields[signerInfoTypeIndex] == 0x02) { //  SignerInfo Type: Certificate (2)
            signerInfoTypeIndex += 1;
            // Extract certificate because of it is an Other message profile
            byte[] certificate = decodeCertificate(p_headerfields, signerInfoTypeIndex, p_keys);
            if (certificate == null) {
                System.err.println("SecurityHelper.checkHeaderfields: Drop packet - Certificate not decoded");
                if (p_enforceSecurityCheck) { 
                    // Drop it
                    return false;
                }
            }
    //        System.out.println("SecurityHelper.checkHeaderfields: Certificate=" + ByteHelper.byteArrayToString(certificate));
            // Add it in our map
            Long lKey = ByteHelper.byteArrayToLong(calculateDigestFromCertificate(certificate));
            if (!_neighborsCertificates.containsKey(lKey)) {
        //        System.out.println("SecurityHelper.checkHeaderfields: Add keys for " + ByteHelper.byteArrayToString(calculateDigestFromCertificate(certificate)) + " / " + lKey);
                _neighborsCertificates.put(lKey, p_keys);
            }
            signerInfoTypeIndex += certificate.length;
        } else if (p_headerfields[signerInfoTypeIndex] == 0x01) { // SignerInfo Type: certificate digest with SHA256 (1)
            signerInfoTypeIndex += 1;
            byte[] hashedid8 = ByteHelper.extract(p_headerfields, signerInfoTypeIndex, Long.SIZE / Byte.SIZE);
            signerInfoTypeIndex += (Long.SIZE / Byte.SIZE);
            Long lKey = ByteHelper.byteArrayToLong(hashedid8);
    //        System.out.println("SecurityHelper.checkHeaderfields: Certificate digest with SHA256=" + lKey + "/ " + ByteHelper.byteArrayToString(hashedid8));
            if (!_neighborsCertificates.containsKey(lKey) || (_neighborsCertificates.get(lKey) == null)) {
                System.err.println("SecurityHelper.checkHeaderfields: Drop packet - Unknown HahedId8");
                if (p_enforceSecurityCheck) { 
                    // Drop it
                    return false;
                }
            }
            try {
                p_keys.write(_neighborsCertificates.get(lKey).toByteArray());
            } catch (Exception e) {
                // Drop it
                e.printStackTrace();
                if (p_enforceSecurityCheck) { 
                    // Drop it
                    return false;
                }
            }
        } else { // TODO Add certchain support
            signerInfoTypeIndex += 1;
            ByteArrayInputStream ba = new ByteArrayInputStream(ByteHelper.extract(p_headerfields, signerInfoTypeIndex, p_headerfields.length - signerInfoTypeIndex));
            int certChainLength = (int) this.tls2size(ba);
    //        System.out.println("SecurityHelper.checkHeaderfields: Certchain length = " + certChainLength);
            signerInfoTypeIndex += this.size2tls(certChainLength).length;
            ByteArrayOutputStream keys;
            do {
                // Extract certificate because of it is an Other message profile
                keys = new ByteArrayOutputStream();
                byte[] certificate = decodeCertificate(p_headerfields, signerInfoTypeIndex, keys);
                if (certificate == null) {
                    // Drop it
                    System.err.println("SecurityHelper.checkHeaderfields: Drop packet - Failed to decode chain of certificate");
                    return false;
                }
        //        System.out.println("SecurityHelper.checkHeaderfields: Certificate=" + ByteHelper.byteArrayToString(certificate));
                // Add it in our map
                Long lKey = ByteHelper.byteArrayToLong(calculateDigestFromCertificate(certificate));
                if (!_neighborsCertificates.containsKey(lKey)) {
//                    System.out.println("SecurityHelper.checkHeaderfields: Add keys for " + ByteHelper.byteArrayToString(calculateDigestFromCertificate(certificate)) + " / " + lKey);
                    _neighborsCertificates.put(lKey, p_keys);
                }
                certChainLength -= certificate.length;
                signerInfoTypeIndex += certificate.length;
        //        System.out.println("SecurityHelper.checkHeaderfields: Extracted certificate = " + ByteHelper.byteArrayToString(certificate));
            } while (certChainLength > 0);
        }
        if (p_headerfields[signerInfoTypeIndex++] != 0x00) { // Header Field: Generation Time (0)
            if (p_enforceSecurityCheck) { 
                // Drop it
                System.err.println("SecurityHelper.checkHeaderfields: Drop packet - GenerationTime not found");
                return false;
            }
        }
        // Check generation time
        long generationTime = ByteHelper.byteArrayToLong(ByteHelper.extract(p_headerfields, signerInfoTypeIndex, Long.SIZE / Byte.SIZE));
//        System.out.println("SecurityHelper.checkHeaderfields: generationTime=" + ByteHelper.byteArrayToString(ByteHelper.extract(p_headerfields, signerInfoTypeIndex, Long.SIZE / Byte.SIZE)));
        if (Math.abs(System.currentTimeMillis() - generationTime) < 1000) {
            System.err.println("SecurityHelper.checkHeaderfields: Drop packet - GenerationTime out of range");
            if (p_enforceSecurityCheck) { 
                // Drop it
                return false;
            }
        }
        signerInfoTypeIndex += (Long.SIZE / Byte.SIZE);
        if (signerInfoTypeIndex < p_headerfields.length) { 
    //        System.out.println("SecurityHelper.checkHeaderfields: dump #1=" + ByteHelper.byteArrayToString(ByteHelper.extract(p_headerfields, signerInfoTypeIndex, p_headerfields.length - signerInfoTypeIndex)));
            if (p_headerfields[signerInfoTypeIndex] == 0x03) { // Header Field: Generation Location (3)
                signerInfoTypeIndex += 1;
                byte[] lat = ByteHelper.extract(p_headerfields, signerInfoTypeIndex, 4);
                signerInfoTypeIndex += 4;
        //        System.out.println("SecurityHelper.checkHeaderfields: latitude=" + ByteHelper.byteArrayToString(lat));
                byte[] lon = ByteHelper.extract(p_headerfields, signerInfoTypeIndex, 4);
                signerInfoTypeIndex += 4;
        //        System.out.println("SecurityHelper.checkHeaderfields: longitude=" + ByteHelper.byteArrayToString(lon));
                byte[] ele = ByteHelper.extract(p_headerfields, signerInfoTypeIndex, 2);
                signerInfoTypeIndex += 2;
        //        System.out.println("SecurityHelper.checkHeaderfields: elevation=" + ByteHelper.byteArrayToString(ele));
            }
        }
        if (signerInfoTypeIndex < p_headerfields.length) { 
    //        System.out.println("SecurityHelper.checkHeaderfields: dump #2=" + ByteHelper.byteArrayToString(ByteHelper.extract(p_headerfields, signerInfoTypeIndex, p_headerfields.length - signerInfoTypeIndex)));
            if (p_headerfields[signerInfoTypeIndex] == 0x05) { // Header Field: Its AID (5)
                signerInfoTypeIndex += 1;
                // Check ItsAid
                if ((p_headerfields[signerInfoTypeIndex] & 0x80) == 0x00) { // Short integer
                    if (
                        (p_headerfields[signerInfoTypeIndex] != 0x24) && // CAM
                        (p_headerfields[signerInfoTypeIndex] != 0x25) && // DENM
                        (p_headerfields[signerInfoTypeIndex] != p_itsAidOther)
                    ) {
                        System.err.println("SecurityHelper.checkHeaderfields: Drop packet - Unknown ItsAid value");
                        if (p_enforceSecurityCheck) { 
                            // Drop it
                            return false;
                        }
                    }
//                    System.out.println("SecurityHelper.checkHeaderfields: ItsAid=" + p_headerfields[signerInfoTypeIndex]);
                    signerInfoTypeIndex += 1;
                } else {
                    // FIXME to be continued
                }
            }
        }
        if (signerInfoTypeIndex < p_headerfields.length) { 
            // TODO check other fields
    //        System.out.println("SecurityHelper.checkHeaderfields: dump #3=" + ByteHelper.byteArrayToString(ByteHelper.extract(p_headerfields, signerInfoTypeIndex, p_headerfields.length - signerInfoTypeIndex)));
        }
        
        return true;
    }
    
    public byte[] decodeCertificate(final byte[] p_headerfields, final int p_offset, final ByteArrayOutputStream p_keys) { 
//        System.out.println(">>> SecurityHelper.decodeCertificate: " + ByteHelper.byteArrayToString(ByteHelper.extract(p_headerfields, p_offset, p_headerfields.length - p_offset)));
        
        ByteArrayInputStream headerfields = new ByteArrayInputStream(p_headerfields, p_offset, p_headerfields.length - p_offset);
//        System.out.println("SecurityHelper.decodeCertificate: headerfields length=" + headerfields.available());
        ByteArrayOutputStream cert = new ByteArrayOutputStream(); // FIXME To be removed
        try {
            // Version
            cert.write((byte)headerfields.read());
            if (cert.toByteArray()[0] != 0x02) {
                System.err.println("SecurityHelper.decodeCertificate: Wrong version number");
                return null;
            }
            // SignerInfo type
            byte signerInfoType = (byte)headerfields.read();
            cert.write(signerInfoType);
            switch (signerInfoType) {
                case 0x01:
                    byte[] digest = new byte[8];
                    headerfields.read(digest, 0, digest.length);
//                    System.out.println("SecurityHelper.decodeCertificate: hashedid8=" + ByteHelper.byteArrayToString(digest));
                    cert.write(digest);
                    break;
                // FIXME To be continued
            } // End of 'switch' statement
            // SubjectInfo type
            byte subjectInfoType = (byte)headerfields.read();
            if (
                (subjectInfoType != 0x01) && // Subject Info: authorization ticket (1)
                (subjectInfoType != 0x02)    // Subject Info: authorization authority (2)
            ) { 
                System.err.println("SecurityHelper.decodeCertificate: Subject Info: authorization authority/ticket expected - " + ByteHelper.byteArrayToString(cert.toByteArray()));
                return null;
            }
            cert.write(subjectInfoType);
            long length = tls2size(headerfields);
            if (length != 0) {
                cert.write(size2tls((int) length));
                byte[] subjectInfo = new byte[(int) length];
                headerfields.read(subjectInfo, 0, subjectInfo.length);
                cert.write(subjectInfo);
        //        System.out.println("SecurityHelper.decodeCertificate: subjectInfo: " + ByteHelper.byteArrayToString(subjectInfo));
            } else {
                cert.write(0x00);
            }
            // Subject Attributes length
            length = tls2size(headerfields);
            cert.write(size2tls((int) length));
            // Subject Attributes
            byte[] b = new byte[(int) length];
            headerfields.read(b, 0, b.length);
            cert.write(b);
    //        System.out.println("SecurityHelper.decodeCertificate: Subject Attributes length=" + length + " / " + headerfields.available());
            ByteArrayInputStream subjectAttributes = new ByteArrayInputStream(b);
            if (subjectAttributes.read() == 0x00) { // Subject Attribute: verification key (0) - Mandatory
                if (subjectAttributes.read() == 0x00) { // Public Key Alg: ecdsa nistp256 with sha256 (0)
                    if (subjectAttributes.read() == 0x04) { // ECC Point Type: uncompressed (4)
                        byte[] key = new byte[32];
                        subjectAttributes.read(key, 0, 32);
                        p_keys.write(key);
    //                    System.out.println("SecurityHelper.decodeCertificate: Verification key1=" + ByteHelper.byteArrayToString(key));
                        subjectAttributes.read(key, 0, 32);
    //                    System.out.println("SecurityHelper.decodeCertificate: Verification key2=" + ByteHelper.byteArrayToString(key));
                        p_keys.write(key);
                    } // FIXME To be continued
                } // FIXME To be continued
            } // FIXME To be continued
            
            // Read the next header
            byte v = (byte) subjectAttributes.read();
            if (v == 0x01) { //  // Subject Attribute: encryption key (1)
                if (subjectAttributes.read() == 0x01) { // Public Key Alg: ecdsa nistp256 (1)
                    if (subjectAttributes.read() == 0x00) { // Symmetric Algorithm: aes 128 ccm (0)
                        if (subjectAttributes.read() == 0x04) { // ECC Point Type: uncompressed (4)
                            byte[] key = new byte[32];
                            subjectAttributes.read(key, 0, 32);
        //                    System.out.println("SecurityHelper.decodeCertificate: Encryption key1=" + ByteHelper.byteArrayToString(key));
                            //p_keys.write(key);
                            subjectAttributes.read(key, 0, 32);
        //                    System.out.println("SecurityHelper.decodeCertificate: Encryption key2=" + ByteHelper.byteArrayToString(key));
                            //p_keys.write(key);
                        } // FIXME To be continued
                    } // FIXME To be continued
                } // FIXME To be continued
                
                // Read the next header
                v = (byte) subjectAttributes.read();
            } // FIXME To be continued
            
            // Assurance level
            if (v != 0x02) {
                System.err.println("SecurityHelper.decodeCertificate: Assurance level expected - " + ByteHelper.byteArrayToString(cert.toByteArray()));
                return null;
            }
            v = (byte) subjectAttributes.read(); // Skip assurance level value
    //        System.out.println("SecurityHelper.decodeCertificate: assurance level value=" + v);
            if (subjectInfoType == 0x01) { // Authorization Ticket
                if (subjectAttributes.read() != 0x21) { // Subject Attribute: its aid ssp list (33)
                    System.err.println("SecurityHelper.decodeCertificate: Its aid ssp list expected - " + ByteHelper.byteArrayToString(cert.toByteArray()));
                    return null;
                }
                length = tls2size(subjectAttributes);
        //        System.out.println("SecurityHelper.decodeCertificate:  Its aid ssp length=" + length);
                byte[] its_aid_ssp_list = new byte[(int) length];
                subjectAttributes.read(its_aid_ssp_list, 0, (int) length);
        //        System.out.println("SecurityHelper.decodeCertificate: its_aid_list=" + ByteHelper.byteArrayToString(its_aid_ssp_list));
                // TODO Process ATS AID list
            } else if (subjectInfoType == 0x02) { // Authorization Authority
                if (subjectAttributes.read() != 0x20) { // Subject Attribute: its aid ssp (32)
                    System.err.println("SecurityHelper.decodeCertificate: Its aid list expected - " + ByteHelper.byteArrayToString(cert.toByteArray()));
                    return null;
                }
                length = tls2size(subjectAttributes);
        //        System.out.println("SecurityHelper.decodeCertificate: its_aid_list length=" + length);
                byte[] its_aid_list = new byte[(int) length];
                subjectAttributes.read(its_aid_list, 0, (int) length);
        //        System.out.println("SecurityHelper.decodeCertificate: its_aid_list=" + ByteHelper.byteArrayToString(its_aid_list));
                // TODO Process ATS AID list
            } else {
                System.err.println("SecurityHelper.decodeCertificate: Unknown subjectInfoType - " + subjectInfoType);
                return null;
            }
            
            // Validity restrictions
            length = tls2size(headerfields);
    //        System.out.println("SecurityHelper.decodeCertificate: Length=" + length + " / " + headerfields.available());
            cert.write(size2tls((int)length));
            v = (byte)headerfields.read();
            if (v == 0x00) { // Validity Restriction: time end (0)
                cert.write(v);
                byte[] time = new byte[4];
                headerfields.read(time, 0, 4);
                cert.write(time);
                int endTime = ByteHelper.byteArrayToInt(time);
        //        System.out.println("SecurityHelper.decodeCertificate: Validity Restriction: endTime=" + endTime);
                // Check times
                long currentTime = (System.currentTimeMillis() - 1072915200000L) / 1000L;
                if (currentTime > endTime) {
                    System.err.println("SecurityHelper.decodeCertificate: Validity Restriction: time end not matched");
                    return null;
                }
                v = (byte)headerfields.read();
            }
            if (v == 0x01) { // Validity Restriction: time start and end (1)
                cert.write(v);
                byte[] time = new byte[4];
                headerfields.read(time, 0, 4);
                cert.write(time);
                int startTime = ByteHelper.byteArrayToInt(time);
        //        System.out.println("SecurityHelper.decodeCertificate: Validity Restriction: startTime=" + startTime);
                headerfields.read(time, 0, 4);
                cert.write(time);
                int endTime = ByteHelper.byteArrayToInt(time);
        //        System.out.println("SecurityHelper.decodeCertificate: Validity Restriction: endTime=" + endTime);
                // Check times
                long currentTime = (System.currentTimeMillis() - 1072915200000L) / 1000L;
        //        System.out.println("SecurityHelper.decodeCertificate: Validity Restriction: currentTime=" + currentTime);
                if ((currentTime < startTime) || (currentTime > endTime)) {
                    System.err.println("SecurityHelper.decodeCertificate: Validity Restriction: time start and end not matched");
                    return null;
                }
                v = (byte)headerfields.read();
            }
            if (v == 0x02) { // Validity Restriction: time start and duration (2)
                cert.write(v);
                byte[] time = new byte[4];
                headerfields.read(time, 0, 4);
                cert.write(time);
                int startTime = ByteHelper.byteArrayToInt(time);
        //        System.out.println("SecurityHelper.decodeCertificate: Validity Restriction: startTime=" + startTime);
                byte[] dur = new byte[2];
                headerfields.read(dur, 0, 2);
                cert.write(dur);
                short duration = ByteHelper.byteArrayToShort(dur);
        //        System.out.println("SecurityHelper.decodeCertificate: Validity Restriction: duration=" + duration);
                int unit = (duration & 0xe0000) >>> 13;
        //        System.out.println("SecurityHelper.decodeCertificate: Validity Restriction: unit=" + unit);
                long value = (duration & 0x1fff);
                switch (unit) {
                    case 0:
                        // Nothing to do
                        break;
                    case 1:
                        value *= 60;
                        break;
                    case 2:
                        value *= 3600;
                        break;
                    default:
                        System.err.println("SecurityHelper.decodeCertificate: Validity Restriction: time start and duration not processed");
                        value = Long.MAX_VALUE;
                } // End of 'switch' statement
        //        System.out.println("SecurityHelper.decodeCertificate: Validity Restriction: value=" + value);
                // Check times
                long currentTime = (System.currentTimeMillis() - 1072915200000L) / 1000L;
                if ((currentTime < startTime) || (currentTime > (startTime + value))) {
                    System.err.println("SecurityHelper.decodeCertificate: Validity Restriction: time start and duration not matched");
                    return null;
                }
                v = (byte)headerfields.read();
            }
            if (v == 0x03) { // region (3)
                cert.write(v);
                // Region type
                v = (byte)headerfields.read();
                cert.write(v);
                if (v == 0x00) { // none (0)
                    // Nothing to do
                } else if (v == 0x01) { // circle (1)
                    byte[] lat = new byte[4];
                    headerfields.read(lat, 0, lat.length);
                    cert.write(lat);
//                    System.out.println("SecurityHelper.decodeCertificate: Circle lat=" + ByteHelper.byteArrayToString(lat));
                    byte[] lon = new byte[4];
                    headerfields.read(lon, 0, lon.length);
                    cert.write(lon);
//                    System.out.println("SecurityHelper.decodeCertificate: Circle lon=" + ByteHelper.byteArrayToString(lon));
                    byte[] rad = new byte[2];
                    headerfields.read(rad, 0, rad.length);
                    cert.write(rad);
//                    System.out.println("SecurityHelper.decodeCertificate: Circle rad=" + ByteHelper.byteArrayToInt(rad));
                } else if (v == 0x02) { // rectangle (2)
                    int rlength = (int) tls2size(headerfields);
                    cert.write(size2tls(rlength));
                    while (rlength > 0) {
                        byte[] ulat = new byte[4];
                        headerfields.read(ulat, 0, ulat.length);
                        cert.write(ulat);
                        byte[] ulon = new byte[4];
                        headerfields.read(ulon, 0, ulon.length);
                        cert.write(ulon);
                        byte[] llat = new byte[4];
                        headerfields.read(llat, 0, llat.length);
                        cert.write(llat);
                        byte[] llon = new byte[4];
                        headerfields.read(llon, 0, llon.length);
                        cert.write(llon);
    //                    System.out.println("SecurityHelper.decodeCertificate: Rectangle ulat=" + ByteHelper.byteArrayToString(ulat));
    //                    System.out.println("SecurityHelper.decodeCertificate: Rectangle ulon=" + ByteHelper.byteArrayToString(ulon));
    //                    System.out.println("SecurityHelper.decodeCertificate: Rectangle llat=" + ByteHelper.byteArrayToString(llat));
    //                    System.out.println("SecurityHelper.decodeCertificate: Rectangle llon=" + ByteHelper.byteArrayToString(llon));
                        rlength -= 4 * 4;
                    }
                } else if (v == 0x03) { // polygon (3)
                    int plength = (int) tls2size(headerfields);
                    cert.write(size2tls((int) plength));
                    byte[] polygonalRegion = new byte[plength];
                    while (plength > 0) {
                        byte[] lat = new byte[4];
                        headerfields.read(lat, 0, lat.length);
                        cert.write(lat);
                        byte[] lon = new byte[4];
                        headerfields.read(lon, 0, lon.length);
                        cert.write(lon);
    //                    System.out.println("SecurityHelper.decodeCertificate: poly point lat=" + ByteHelper.byteArrayToString(lat));
    //                    System.out.println("SecurityHelper.decodeCertificate: poly point lon=" + ByteHelper.byteArrayToString(lon));
                        plength -= 2 * 4;
                    }
                    headerfields.read(polygonalRegion, 0, polygonalRegion.length);
                    cert.write(polygonalRegion);
                    // TODO Process Validity Restriction
//                    System.out.println("SecurityHelper.decodeCertificate: polygonal=" + ByteHelper.byteArrayToString(polygonalRegion));
                    
                } else if (v == 0x04) { // id (4)
                    v = (byte)headerfields.read();
                    cert.write(v);
                    byte[] ri = new byte[2];
                    headerfields.read(ri, 0, ri.length);
                    cert.write(ri);
                    int lr = (int) tls2size(headerfields);
                    cert.write(size2tls((int) lr));
                    // TODO Process Validity Restriction
//                    System.out.println("SecurityHelper.decodeCertificate: Region t=" + v);
//                    System.out.println("SecurityHelper.decodeCertificate: Region ri=" + ByteHelper.byteArrayToString(ri));
//                    System.out.println("SecurityHelper.decodeCertificate: Region lr=" + lr);
                } else {
                    System.err.println("SecurityHelper.decodeCertificate: Unexpected geographical region");
                    return null;
                }
            }
//            System.out.println("SecurityHelper.decodeCertificate: Before signature: " + ByteHelper.byteArrayToString(cert.toByteArray()));
            // Signature
            byte publicKeyAlg = (byte)headerfields.read();
            cert.write(publicKeyAlg);
            switch (publicKeyAlg) {
                case 0x00: // ecdsa nistp256 with sha256
                    byte eccPointType = (byte)headerfields.read();
                    cert.write(eccPointType);
                    switch (eccPointType) {
                        case 0x00: //  ECC Point Type: x-coordinate only
                            byte[] key = new byte[64];
                            headerfields.read(key, 0, key.length);
                            cert.write(key);
//                            System.out.println("SecurityHelper.decodeCertificate: Signature=" + ByteHelper.byteArrayToString(key));
                            break;
                    } // End of 'switch' statement
                    break;
            } // End of 'switch' statement
            // TODO Check certificate signature
            
//            System.out.println("SecurityHelper.decodeCertificate: Processed cert=" + ByteHelper.byteArrayToString(cert.toByteArray()));
            return cert.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.err.println("SecurityHelper.decodeCertificate: Unsupported certificate - " + ByteHelper.byteArrayToString(cert.toByteArray()));
        return null;
    }
    
    public boolean extractMessageSignature(final byte[] p_secureTrailer, final ByteArrayOutputStream p_signature) { 
//        System.out.println(">>> SecurityHelper.extractMessageSignature: " + ByteHelper.byteArrayToString(p_secureTrailer));
        
        // Sanity check
        if (p_secureTrailer.length == 0) {
            return false;
        }
        
        // Extract digest or certificate
        int secureTrailerIndex = 0;
        if (p_secureTrailer[secureTrailerIndex++] == 0x01) { // Trailer Type: signature (1)
            if (p_secureTrailer[secureTrailerIndex++] == 0x00) { // Public Key Alg: ecdsa nistp256 with sha256 (0)
                if (p_secureTrailer[secureTrailerIndex++] == 0x02) { // ECC Point Type: compressed lsb y-0 (2)
                    if (p_secureTrailer.length == (3 + 2 * 32)) {
                        // Build the signature vector
                        try {
                            p_signature.write(new byte[] { (byte)0x00, (byte)0x00 });
                            p_signature.write(ByteHelper.extract(p_secureTrailer, 3, 64));
                            
//                            System.out.println("<<< SecurityHelper.extractMessageSignature: true");
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } // FIXME To be continued
                } // FIXME To be continued
            } // FIXME To be continued
        } // FIXME To be continued
        
        // Else, drop it
        System.err.println("SecurityHelper.extractMessageSignature: Drop packet - Wrong signature");
        return false;
    }
    
    public byte[] calculateDigestFromCertificate(final byte[] p_toBeHashedData) { 
//        System.out.println("SecurityHelper.calculateDigestFromCertificate: " + ByteHelper.byteArrayToString(p_toBeHashedData));
        byte[] hash = CryptoLib.hashWithSha256(p_toBeHashedData);
//        System.out.println("SecurityHelper.calculateDigestFromCertificate: " + ByteHelper.byteArrayToString(hash));
        return ByteHelper.extract(hash, hash.length - 8, 8);
    }
    
} // End of class SecurityHelper 
