/**
 *  Upper Tester port implementation. This port is used to trigger IUT's upper interface
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/UpperTesterPort.java $
 *              $Id: UpperTesterPort.java 2230 2015-06-03 09:11:02Z mullers $
 *
 */
package org.etsi.its.adapter.ports;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.etsi.adapter.TERFactory;
import org.etsi.certificates.CertificatesIOFactory;
import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.SecurityHelper;
import org.etsi.ttcn.tci.CharstringValue;

import de.fraunhofer.sit.c2x.CryptoLib;

/** This class implements behaviour for Upper Tester port
 * The Upper tester entity in the SUT enables triggering Protocol functionalities by simulating primitives from 
 * application or LDM entities
 * It is required to trigger the Protocol layer in the SUT to send Protocol specific messages, which are 
 * resulting from upper layer primitives
 */
public class UpperTesterPort extends AdapterPort implements IPort, IObservable {
    
    private static final String SETTINGS_PATTERN = "(\\S+)\\:(\\d+)";
    
    private static final String CertificateId = "CERT_UT";
    
    /**
     * Secured mode status 
     */
    private String _utSecuredMode = null;
    
    /**
     * Secured root path to access certificates & private keys 
     */
    private String _utSecuredRootPath = null;
    
    /**
     * Secured configuration identifier 
     */
    private String _utSecuredConfiId = null;
    
    /**
     * Secured mode status 
     */
    private boolean _isSecuredMode = false;
    
    private ByteArrayOutputStream _certificate;
    
    private ByteArrayOutputStream _hashedId8;
    
    private ByteArrayOutputStream _signingPrivateKey;
    
    /**
     * Constructor
     * @param   portName            Name of the port
     * @param   componentName       Name of the component owning this port instance
     * @param   localPortNumber     Local port number for the UDP listener
     * @param   remotePortNumber    UDP port listener of remote UT application
     */
    public UpperTesterPort(final String portName, final String componentName) {
        super(portName, componentName);

        // UDP connection parameters
        _utSecuredMode = ((CharstringValue)TERFactory.getInstance().getTaParameter("UtSecuredMode")).getString();
        _utSecuredRootPath = ((CharstringValue)TERFactory.getInstance().getTaParameter("TsSecuredRootPath")).getString();
        _utSecuredConfiId = ((CharstringValue)TERFactory.getInstance().getTaParameter("TsSecuredConfiId")).getString();
        String settings = ((CharstringValue)TERFactory.getInstance().getTaParameter("UpperTesterSettings")).getString();
        Matcher matcher = settingsPattern.matcher(settings);
        if (matcher.find()) {
            try {
                utPeerAddress = InetAddress.getByName(matcher.group(1));
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
            utPeerPort = Integer.parseInt(matcher.group(2));
        } else {
            // FIXME
        }   
        
        if ((_utSecuredMode != null) && _utSecuredMode.equals("true")) {
            // Load certificate
            _certificate = new ByteArrayOutputStream();
            CertificatesIOFactory.getInstance().loadCertificates(_utSecuredRootPath, _utSecuredConfiId);
            if (CertificatesIOFactory.getInstance().readCertificate(CertificateId, _certificate)) {
//                System.out.println("UpperTesterPort.UpperTesterPort: _certificate=" + ByteHelper.byteArrayToString(_certificate.toByteArray()));
                _hashedId8 = new ByteArrayOutputStream();
                _signingPrivateKey = new ByteArrayOutputStream();
                CertificatesIOFactory.getInstance().readCertificateDigest(CertificateId, _hashedId8);
//                System.out.println("UpperTesterPort.UpperTesterPort: _hashedId8=" + ByteHelper.byteArrayToString(_hashedId8.toByteArray()));
                CertificatesIOFactory.getInstance().readSigningKey(CertificateId, _signingPrivateKey);
//                System.out.println("UpperTesterPort.UpperTesterPort: _signingPrivateKey=" + ByteHelper.byteArrayToString(_signingPrivateKey.toByteArray()));
                
                _isSecuredMode = true;
            }
        }
        
        // UDP socket for communication with UT
        running = true;
        try {
            utSocket = new DatagramSocket();
            utThread = new UdpThread(utSocket);
            utThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean send(final byte[] message) {
/* FIXME: For debug only. Uncomment if no UT
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Do nothing, we do not care
        }
        
        setChanged();
        byte[] rsp;
        switch (message[0]) {
            case 0x00:
                rsp = new byte[]{(byte)0x01, (byte)0x01};
                break;
            case 0x02: // UtChangePosition
                // No break;
            case 0x03:
                // No break;
            case 0x04:
                // No break;
            case 0x05:
                rsp = new byte[]{(byte)0x24, (byte)0x01};
                break;
            case 0x10: // UtDenmTrigger
                rsp = new byte[]{(byte)0x11, (byte)0x01};
                break;
            default:
                if ((message[0] >= 0x30) && (message[0] <= 0x3F)) { // UtCamTrigger_xxx
                    rsp = new byte[]{(byte)0x21, (byte)0x01};
                } else {
                    rsp = new byte[]{(byte)0x41, (byte)0x01};
                }
                break;
        }
        notifyObservers(new PortEvent(rsp, getPortName(), getComponentName()));
        if(true)
            return true;
*/
        try {
            ByteArrayOutputStream dataToSent = new ByteArrayOutputStream();
            dataToSent.write(message);
            if (_isSecuredMode) { // Send a secured message
                // Build the secured message
                ByteArrayOutputStream toBeSignedData = new ByteArrayOutputStream();
                buildToBeSignedData(dataToSent, toBeSignedData);
                // Sign data
                dataToSent = new ByteArrayOutputStream();
                signSecuredMessage(toBeSignedData, dataToSent);
            }
            byte[] output = dataToSent.toByteArray();
            DatagramPacket packet = new DatagramPacket(output, output.length, utPeerAddress, utPeerPort);
            utSocket.send(packet);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } 
        
        return false;
    }

    @Override
    public void dispose() {
        if(running) {           
            running = false;
            if(utThread != null) {
                try {
                    utSocket.close();
                    utThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private DatagramSocket utSocket;
    private Thread utThread;
    private InetAddress utPeerAddress = null;
    private int utPeerPort = 0;
    private Pattern settingsPattern = Pattern.compile(SETTINGS_PATTERN);
    
    /**
     * Indicates whether the port is still active. Setting this field to false will cause
     * the UDP communication with Upper Tester to be stopped
     */
    private volatile boolean running;
    
    private class UdpThread extends Thread {

        private DatagramSocket taSocket;
        
        public UdpThread(DatagramSocket taSocket) throws IOException {
            this.taSocket = taSocket;
        }

        @Override
        public void run() {
            
            while(running) {
                try {
                    byte[] buf = new byte[4096];
                    
                    // receive packet
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    taSocket.receive(packet);
                    
                    if (_isSecuredMode) { // Secure mode enabled
                        byte[] message = ByteHelper.extract(packet.getData(), packet.getOffset(), packet.getLength());
                        byte[] payload = checkSecuredOtherProfileAndExtractPayload(message);
                        
                        if (payload != null) { // Notify received payload
                            setChanged();
                            notifyObservers(new PortEvent(payload, getPortName(), getComponentName()));
                        } // else, packet was dropped
                    } else { // Notify received payload
                    setChanged();
                    notifyObservers(new PortEvent(ByteHelper.extract(packet.getData(), packet.getOffset(), packet.getLength()), getPortName(), getComponentName()));
                    }
                } catch (IOException e) {
                    running = false;
                }
            }
            taSocket.close();
        }
        
    } // End of class UdpThread
    
    private byte[] checkSecuredOtherProfileAndExtractPayload(final byte[] p_message) {
        System.out.println(">>> UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: " + ByteHelper.byteArrayToString(p_message));
        ByteHelper.dump("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: ", p_message);
        
        ByteArrayInputStream decvalue = new ByteArrayInputStream(p_message);
        
        // Check version
        if (decvalue.read() != 2) {
            // Drop it
            System.err.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: Drop packet - Wrong version number");
            return null;
        }
        // Extract header fields length and header fields
        long headerFieldsLength = SecurityHelper.getInstance().tls2size(decvalue);
        System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: headerFieldsLength:" + headerFieldsLength);
        byte[] headerFields = new byte[(int) headerFieldsLength];
        decvalue.read(headerFields, 0, (int) headerFieldsLength);
        ByteArrayOutputStream certificateKeys = new ByteArrayOutputStream();
        if (!checkHeaderfields(headerFields, certificateKeys)) {
            // Drop it
            System.err.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: Drop packet - Wrong Headerfields");
            return null;
        }
        byte[] aaSigningPublicKeyX, aaSigningPublicKeyY;
        aaSigningPublicKeyX = ByteHelper.extract(certificateKeys.toByteArray(), 0, 32);
        System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: aaSigningPublicKeyX:" + ByteHelper.byteArrayToString(aaSigningPublicKeyX));
        aaSigningPublicKeyY = ByteHelper.extract(certificateKeys.toByteArray(), 32, 32);
        System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: aaSigningPublicKeyX:" + ByteHelper.byteArrayToString(aaSigningPublicKeyX));
        System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: headerFields:" + ByteHelper.byteArrayToString(headerFields));
        // Extract payload, decvalue is updated with the payload
        if (decvalue.read() != 1) {
            // Drop it
            System.err.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: Drop packet - Wrong Payload type");
            return null;
        }
        long payloadLength = SecurityHelper.getInstance().tls2size(decvalue);
        System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: payloadLength:" + payloadLength);
        byte[] payload = new byte[(int) payloadLength];
        decvalue.read(payload, 0, (int) payloadLength);
        System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: payload:" + ByteHelper.byteArrayToString(payload));
        // Extract Secure Trailer
        long secureTrailerLength = SecurityHelper.getInstance().tls2size(decvalue);
        byte[] secureTrailer = new byte[(int) secureTrailerLength];
        decvalue.read(secureTrailer, 0, secureTrailer.length);
        ByteArrayOutputStream signature = new ByteArrayOutputStream();
        if (!extractMessageSignature(secureTrailer, signature)) {
            // Drop it
            System.err.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: Drop packet - Wrong Signatures");
            return null;
        }
        System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: signature:" + ByteHelper.byteArrayToString(signature.toByteArray()));
        // Build signed data
        byte[] toBeVerifiedData = ByteHelper.extract(
            p_message, 
            0, 
            p_message.length - (int)(secureTrailerLength - 1 /* Exclude signature structure but keep signature type and signature length */)
        );
        System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload:" + ByteHelper.byteArrayToString(toBeVerifiedData));
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
            System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: Verify signature: " + new Boolean(result));
            if (!result) {
                // Drop packet
                System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: toBeVerifiedData   :" + ByteHelper.byteArrayToString(toBeVerifiedData));
                System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: Hash               :" + ByteHelper.byteArrayToString(hash));
                System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: signature          :" + ByteHelper.byteArrayToString(signature.toByteArray()));
                System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: aaSigningPublicKeyX:" + ByteHelper.byteArrayToString(aaSigningPublicKeyX));
                System.out.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: aaSigningPublicKeyY:" + ByteHelper.byteArrayToString(aaSigningPublicKeyY));
                System.err.println("UpperTesterPort.checkSecuredOtherProfileAndExtractPayload: Drop packet - Invalid signature");
                return null;
            }
            
            return payload;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Drop packet
        return null;
    }
    
    private boolean checkHeaderfields(byte[] p_headerfields, final ByteArrayOutputStream p_keys) { // TODO Common with GnLayer and UpperTester, to be grouped
        System.out.println(">>> UpperTesterPort.checkHeaderfields: " + ByteHelper.byteArrayToString(p_headerfields));
        
        // Sanity check
        if (p_headerfields.length == 0) {
            return false;
        }
        // Extract digest or certificate
        int signerInfoTypeIndex = 0;
        if (
            ((p_headerfields[signerInfoTypeIndex++] & 0x80) != 0x80) ||  // SignerInfo Type: certificate digest with ecdsap256 (1)
            (p_headerfields[signerInfoTypeIndex++] != 0x02) // SignerInfo Type: certificate (2)
        ) {
            // Drop it
            System.err.println("UpperTesterPort.checkHeaderfields: Drop packet - Certificate");
            return false;
        }
        // Extract certificate because of it is an Other message profile
        byte[] certificate = decodeCertificate(p_headerfields, signerInfoTypeIndex, p_keys);
        signerInfoTypeIndex += certificate.length;
        System.out.println("UpperTesterPort.checkHeaderfields: Certificate:" + ByteHelper.byteArrayToString(certificate));
        // TODO check other fields
        
        return true;
    }
    
    private byte[] decodeCertificate(final byte[] p_headerfields, final int p_offset, final ByteArrayOutputStream p_keys) { // TODO Common with GnLayer and UpperTester, to be grouped
        System.out.println("UpperTesterPort.decodeCertificate");
        
        ByteArrayInputStream headerfields = new ByteArrayInputStream(p_headerfields, p_offset, p_headerfields.length - p_offset);
        ByteArrayOutputStream cert = new ByteArrayOutputStream(); // FIXME To be removed
        try {
            // Version
            cert.write((byte)headerfields.read());
            if (cert.toByteArray()[0] != 0x02) {
                System.out.println("UpperTesterPort.decodeCertificate: Wrong version number");
                return null;
            }
            // SignerInfo type
            byte signerInfoType = (byte)headerfields.read();
            cert.write(signerInfoType);
            switch (signerInfoType) {
                case 0x01:
                    byte[] digest = new byte[8];
                    headerfields.read(digest, 0, digest.length);
                    cert.write(digest);
                    break;
                // FIXME To be continued
            } // End of 'switch' statement
            // SubjectInfo type
            byte subjectInfoType = (byte)headerfields.read();
            cert.write(subjectInfoType);
            long length = SecurityHelper.getInstance().tls2size(headerfields);
            if (length != 0) {
                // FIXME To be continued
            } else {
                cert.write(0x00);
            }
            // Subject Attributes length
            length = SecurityHelper.getInstance().tls2size(headerfields);
            byte[] b = SecurityHelper.getInstance().size2tls((int) length);
            cert.write(b);
            // Subject Attributes
            b = new byte[(int) length];
            headerfields.read(b, 0, b.length);
            cert.write(b);
            int offset = 0;
            if (b[offset++] == 0x00) { // Subject Attribute: verification key (0)
                if (b[offset++] == 0x00) { // Public Key Alg: ecdsa nistp256 with sha256 (0)
                    if (b[offset++] == 0x04) { // ECC Point Type: uncompressed (4)
                        p_keys.write(b, offset, 32);
                        offset += 32;
                        p_keys.write(b, offset, 32);
                    } // FIXME To be continued
                } // FIXME To be continued
            } // FIXME To be continued
            // Validity Restriction
            length = SecurityHelper.getInstance().tls2size(headerfields);
            if (length != 0) {
                b = SecurityHelper.getInstance().size2tls((int) length);
                cert.write(b);
                b = new byte[(int) length];
                headerfields.read(b, 0, b.length);
                cert.write(b);
            } else {
                cert.write((byte)0x00);
            } // TODO Process Validity Restriction
//            // Geographical region
//            length = SecurityHelper.getInstance().tls2size(buf);
//            if (length != 0) {
//                b = SecurityHelper.getInstance().size2tls((int) length);
//                cert.write(b);
//                b = new byte[(int) length];
//                buf.read(b, 0, b.length);
//                cert.write(b);
//            } else {
//                cert.write((byte)0x00);
//            } // TODO Process Geographical region
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
                            break;
                    } // End of 'switch' statement
                    break;
            } // End of 'switch' statement
            // TODO Check certificate signature
            
            return cert.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("UpperTesterPort.decodeCertificate: Unsupported certificate");
        return null;
    }
    
    private boolean extractMessageSignature(final byte[] p_secureTrailer, final ByteArrayOutputStream p_signature) { // TODO Common with GnLayer and UpperTester, to be grouped
        System.out.println(">>> UpperTesterPort.extractMessageSignature: " + ByteHelper.byteArrayToString(p_secureTrailer));
        
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
                            
                            System.out.println("UpperTesterPort.extractMessageSignature: true");
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } // FIXME To be continued
                } // FIXME To be continued
            } // FIXME To be continued
        } // FIXME To be continued
        
        // Else, drop it
        System.err.println("UpperTesterPort.extractMessageSignature: Drop packet - Wrong signature");
        return false;
    }
    
    private void buildToBeSignedData(final ByteArrayOutputStream p_securedData, final ByteArrayOutputStream p_toBeSignedData) throws IOException {
        
        // Build the SignerInfo field
        byte[] signerInfo = null;
        signerInfo = ByteHelper.concat(
            new byte[] {
                (byte)0x80,                                     // signerInfo
                (byte)0x02                                      //     Certificate
            },
            _certificate.toByteArray()                          //         Certificate value
        );
        
        // For debug purpose Extract signature from secured 'Other message' 
        byte[] aaSigningPublicKeyX = ByteHelper.extract(
            _certificate.toByteArray(),
            16, // Set position at the beginning of the public keys
            32
        );
//        System.out.println("UpperTesterPort.DispatchMessage: aaSigningPublicKeyX:" + ByteHelper.byteArrayToString(aaSigningPublicKeyX));
        byte[] aaSigningPublicKeyY = ByteHelper.extract(
            _certificate.toByteArray(),
            16 + 32,
            32
        );
//        System.out.println("UpperTesterPort.DispatchMessage: aaSigningPublicKeyY:" + ByteHelper.byteArrayToString(aaSigningPublicKeyY));
        
        // Build the generation time value
        byte[] generationTime = ByteHelper.longToByteArray(
            System.currentTimeMillis(),
            Long.SIZE / Byte.SIZE
        ); // In microseconds
//        System.out.println("UpperTesterPort.buildToBeSignedData: generationTime=" + ByteHelper.byteArrayToString(generationTime));
        byte[] headersField = ByteHelper.concat(
            ByteHelper.concat(                                // SecuredMessage HeaderFields
                signerInfo,                                   // signerInfo
                new byte[] {
                    (byte)0x00,                               // generationTime
                },
                generationTime                                //    Time64 value
            )
        );
        // Add Its-Aid for Other profile
        int itsAid = 0x38; // FIXME To be refined
        byte[] b;
        if (itsAid < 128) {
            b = new byte[] { (byte)itsAid }; 
        } else if (itsAid < Short.MAX_VALUE) {
            b = ByteHelper.intToByteArray(itsAid, Short.SIZE / Byte.SIZE);
            b = ByteHelper.concat(
                SecurityHelper.getInstance().size2tls(b.length),
                b
            );
        } else {
            b = ByteHelper.intToByteArray(itsAid, Integer.SIZE / Integer.SIZE);
            b = ByteHelper.concat(
                SecurityHelper.getInstance().size2tls(b.length),
                b
            );
        }
        headersField = ByteHelper.concat(
            headersField,
            new byte[] { 
                (byte)0x05                                // Its-aid
            },
            b
        );
        byte[] headersFieldLength = SecurityHelper.getInstance().size2tls(headersField.length);
//        System.out.println("UpperTesterPort.buildToBeSignedData: headersField=" + ByteHelper.byteArrayToString(headersField));
        byte[] payload = p_securedData.toByteArray();
        byte[] toBeSignedData = ByteHelper.concat(
            new byte[] {                                      // SecuredMessage version 
                (byte)0x02                                    //     version
            },
            headersFieldLength,                               // HeadersField length
            headersField,                                     // HeaderFields
            new byte[] {                                      // SecuredMessage Payloads
                (byte)0x01,                                   //     Secured payload type: signed (1)
                (byte)payload.length                          //     Data payload length
            },
            payload,                                          // End of SecuredMessage Payloads
            new byte[] { (byte)0x43 },                        // Signature length
            new byte[] { (byte)0x01 }                         // Signature
        );
//        System.out.println("UpperTesterPort.buildToBeSignedData: toBeSignedData=" + ByteHelper.byteArrayToString(toBeSignedData));
        
        p_toBeSignedData.write(toBeSignedData);
    }
    
    private void signSecuredMessage(final ByteArrayOutputStream p_toBeSignedData, final ByteArrayOutputStream p_securedMessage) throws Exception {
//        System.out.println("UpperTesterPort.signSecuredMessage: toBeSignedData: " + ByteHelper.byteArrayToString(p_toBeSignedData.toByteArray()));
          // Calculate the hash
        byte[] hash = CryptoLib.hashWithSha256(p_toBeSignedData.toByteArray());
//        System.out.println("UpperTesterPort.signSecuredMessage: hash=" + ByteHelper.byteArrayToString(hash));
        byte[] securedBeaconHeader = null;
        // Signed the hash
        byte[] signatureBytes = CryptoLib.signWithEcdsaNistp256WithSha256(hash, new BigInteger(_signingPrivateKey.toByteArray()));
//        System.out.println("UpperTesterPort.signSecuredMessage: signatureBytes=" + ByteHelper.byteArrayToString(signatureBytes));
        // Add signature
        securedBeaconHeader = ByteHelper.concat(
            p_toBeSignedData.toByteArray(),
            new byte[] { 
                (byte)0x00, // Public Key Alg: ecdsa nistp256 with sha256 (0)
                (byte)0x02  // ECC Point Type: compressed lsb y-0 (2)
            }, // Signature header
            ByteHelper.extract(signatureBytes, 2, signatureBytes.length - 2)
        );
        p_securedMessage.write(securedBeaconHeader);
//        System.out.println("<<< UpperTesterPort.signSecuredMessage: sendBeacon: " + ByteHelper.byteArrayToString(p_securedMessage.toByteArray()));
    }
    
} // End of class UpperTesterPort
