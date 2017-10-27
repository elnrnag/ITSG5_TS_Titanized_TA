/**
 *  GeoNetworking port implementation (background thread)
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/ports/GnPort.java $
 *              $Id: GnPort.java 2230 2015-06-03 09:11:02Z mullers $
 *
 */
package org.etsi.its.adapter.ports;

import java.util.HashMap;
import java.util.Map;

import org.etsi.adapter.TERFactory;
import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.SecurityHelper;
import org.etsi.its.adapter.layers.EthernetLayer;
import org.etsi.its.adapter.layers.IEthernetSpecific;
import org.etsi.ttcn.tci.CharstringValue;

import de.fraunhofer.sit.c2x.CryptoLib;

/**
 *  GeoNetworking port implementation (background thread)
 */
public class GnPort extends ProtocolPort implements Runnable, IEthernetSpecific {

    /**
     * GeoNetworking header type for beacon messages
     */
    private static final int HT_BEACON = 1;
    private static final int HT_TSB = 5;
    private static final int HST_SHB = 0;
    
    /**
     * Constructor
     * @param   portName        Name of the port
     * @param   componentName   Name of the component owning this port instance
     * @param   lowerStackDesc  Description of the port's lower stack in the form "Layer/Layer/Layer/..."
     * @param  linkLayerAddress    Link-layer address to be used by this port as source address (null if not applicable)
     */
    public GnPort(String portName, String componentName, String lowerStackDesc, String linkLayerAddress) {
        super(portName, componentName, lowerStackDesc, linkLayerAddress);
        running = true;
        management.registerGnPort(this);
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.IEthernetSpecific#getEthernetType()
     */
    @Override
    public short getEthernetType() {
        
        // Retrieve EthernetType value
        Integer iutEthernetTypeValue = Integer.decode(((CharstringValue)TERFactory.getInstance().getTaParameter("IutEthernetTypeValue")).getString());
        return iutEthernetTypeValue.shortValue();
    }

    /**
     * Starts beaconning
     */
    public void startBeaconning() {
        running = true;
        if(beaconThread == null) {
            beaconThread = new Thread(this);
            beaconThread.start();
        }
    }
    
    /**
     * Stops beaconning
     */
    public void stopBeaconning() {
        if(beaconThread != null) {
            running = false;
            try {
                beaconThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            beaconThread = null;
        }
    }
    
    /**
     * Thread function for sending periodic beacons
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        byte[] beaconHeader = management.getGnBeacon();
        if(beaconHeader != null) {
            long modulo = (long)Math.pow(2,32);
            Map<String, Object> params = new HashMap<String, Object>();
//            long triggerTime = System.currentTimeMillis();
            while(running) {
                // Update timestamp. Timestamp is 1s older than current time to avoid sending beacons coming from the future (time sync between nodes)
                long currentTime = System.currentTimeMillis();
                byte[] tst = ByteHelper.intToByteArray((int)(((currentTime - 1072915200000L) % modulo) - 3000), 4);
                System.arraycopy(tst, 0, beaconHeader, 20, 4);
                // TODO Uncomment to get secured beacon if (!management.isSecuredModeSet()) { // Secure mode disabled
                    send(beaconHeader, params);
                /* TODO Uncomment to get secured beacon
                } else { // Send a secured beacon
                    byte[] securedBeaconHeader = buildSecuredBeacon(beaconHeader, tst, currentTime, false); 
//                    triggerTime = currentTime;
                    // Send the secured beacon
                    System.out.println("GnPort: Call send " + ByteHelper.byteArrayToString(securedBeaconHeader));
                    send(securedBeaconHeader, params);
                }
                TODO Uncomment to get secured beacon*/
                try {
                    Thread.sleep(management.getGnBeaconInterval());
                } catch (InterruptedException e) {
                    // Do nothing, we do not care
                }
            }
        }
    }
    
    private byte[] buildSecuredBeacon(final byte[] p_beacon, byte[] p_tst, final long p_currentTime, final boolean p_sendDigest) {
        System.out.println("GnPort.buildSecuredBeacon (1): " + ByteHelper.byteArrayToString(p_beacon));
        
        byte[] basicHeader = ByteHelper.extract(p_beacon, 0, 4);
        basicHeader[0] &= 0xFE;
        basicHeader[0] |= 0x02; // Secured mode
        
        // Update the beacon timestamp field
        byte[] beacon = ByteHelper.concat(
            ByteHelper.extract(p_beacon, 4, 16),                    // Extract Common Header + GN address
            p_tst,                                                  // Update the timestamp field
            ByteHelper.extract(p_beacon, 20, p_beacon.length - 4 - 20)  // Add remaining bytes
        );
        System.out.println("GnPort.buildSecuredBeacon (2): " + ByteHelper.byteArrayToString(beacon));
        // Prepare the message to be signed
        byte[] toBeSignedData = buildToBeSignedData(beacon, p_currentTime, p_sendDigest);
        System.out.println("GnPort.buildSecuredBeacon: toBeSignedData " + ByteHelper.byteArrayToString(toBeSignedData));
        // Sign the message
        byte[] securedBeaconHeader = signSecuredMessage(toBeSignedData);
        
        // Return the complete message to be sent
        return ByteHelper.concat(basicHeader, securedBeaconHeader);
    }
    
    private byte[] buildToBeSignedData(final byte[] p_beacon, final long p_currentTime, final boolean p_sendDigest) {
        System.out.println("GnPort.buildToBeSignedData: " + ByteHelper.byteArrayToString(p_beacon));
        
        // Build the SignerInfo field
        byte[] signerInfo = null;
        if (!p_sendDigest) {
            signerInfo = ByteHelper.concat(
                new byte[] {
                    (byte)0x80,                                    // signerInfo
                    (byte)0x02                                     //     Certificate
                },
                management.getAtCertificate()                      //         Certificate value
            );
        } else {
            signerInfo = ByteHelper.concat(
                new byte[] {
                    (byte)0x80,                                     // signerInfo
                    (byte)0x01                                      //     Certificate digest with ecdsap256
                },
                management.getAtCertificateDigest()                 //         Hashed8
            );
        }
        
        // Build the generation time value
        byte[] generationTime = ByteHelper.longToByteArray((long)(p_currentTime - 1072915200000L) * 1000L, Long.SIZE / Byte.SIZE); // In microseconds
        System.out.println("GnPort.buildToBeSignedData: generationTime=" + ByteHelper.byteArrayToString(generationTime));
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
        int itsAid = management.getItsAidOther();
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
        System.out.println("GnPort.buildToBeSignedData: headersField=" + ByteHelper.byteArrayToString(headersField));
        byte[] toBeSignedData = ByteHelper.concat(
            new byte[] {                                      // SecuredMessage version 
                (byte)0x02                                    //     version
            },
            headersFieldLength,                               // HeadersField length
            headersField,                                     // HeaderFields
            new byte[] {                                      // SecuredMessage Payloads
                (byte)0x01,                                   //     Secured payload type: signed (1)
                (byte)p_beacon.length                         //     Data payload length
            },
            p_beacon,                                         // End of SecuredMessage Payloads
            new byte[] { (byte)0x43 },                        // Signature length
            new byte[] { (byte)0x01 }                         // Signature
        );
        System.out.println("GnPort.buildToBeSignedData: toBeSignedData=" + ByteHelper.byteArrayToString(toBeSignedData));
        
        return toBeSignedData;
    }
    
    private byte[] signSecuredMessage(final byte[] p_toBeSignedData) {
        System.out.println("GnPort.signSecuredMessage: toBeSignedData: " + ByteHelper.byteArrayToString(p_toBeSignedData));
        
        // Calculate the hash
        byte[] hash = CryptoLib.hashWithSha256(p_toBeSignedData);
        System.out.println("GnPort.signSecuredMessage: hash=" + ByteHelper.byteArrayToString(hash));
        byte[] securedBeaconHeader = null;
        // Signed the hash
        byte[] signatureBytes;
        try {
            signatureBytes = CryptoLib.signWithEcdsaNistp256WithSha256(hash, management.getSigningPrivateKey());
            System.out.println("GnPort.signSecuredMessage: signatureBytes=" + ByteHelper.byteArrayToString(signatureBytes));
            // Add signature
            securedBeaconHeader = ByteHelper.concat(
                p_toBeSignedData,
                new byte[] { 
                    (byte)0x00, // Public Key Alg: ecdsa nistp256 with sha256 (0)
                    (byte)0x02  // ECC Point Type: compressed lsb y-0 (2)
                }, // Signature header
                ByteHelper.extract(signatureBytes, 2, signatureBytes.length - 2)
            );
            
            
/*            boolean result = CryptoLib.verifyWithEcdsaNistp256WithSha256(p_toBeSignedData, signatureBytes, management.getSigningPublicKeyX(), management.getSigningPublicKeyY());
            System.out.println("GnPort.signSecuredMessage: Verify signature: pubX" + ByteHelper.byteArrayToString(management.getSigningPublicKeyX()));
            System.out.println("GnPort.signSecuredMessage: Verify signature: pubY" + ByteHelper.byteArrayToString(management.getSigningPublicKeyY()));
            System.out.println("GnPort.signSecuredMessage: Verify signature: " + new Boolean(result));*/
            
            System.out.println("<<< GnPort.signSecuredMessage: sendBeacon: " + ByteHelper.byteArrayToString(securedBeaconHeader));
            return securedBeaconHeader;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.IPort#send(byte[])
     */
    @Override
    public boolean send(byte[] message) {
        System.out.println(">>> GnPort.send: " + ByteHelper.byteArrayToString(message));
        
        HashMap<String, Object> params = new HashMap<String, Object>();
        
        byte[] destMacAddress = ByteHelper.extract(message, message.length - 6, 6);
        message = ByteHelper.extract(message, 0, message.length - 6);
        params.put(EthernetLayer.LINK_LAYER_DESTINATION, destMacAddress);
        if (management.isSecuredModeSet()) { // Secure mode disabled
            message = createSecuredMessage(message);
        }
        
//        ByteHelper.dump("GnPort.send", message);
        System.out.println("\n\n");
        return send(message, params);
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.ProtocolPort#receive(byte[])
     */
    @Override
    public void receive(byte[] message, Map<String, Object> lowerInfo) {
        System.out.println(">>> GnPort.receive: " + ByteHelper.byteArrayToString(message));
        
        byte[] basicHdr = new byte[4];
        System.arraycopy(message, 0, basicHdr, 0, 4);
        int headerType = -1;
        int headerSubType = -1;
        byte[] sopv = new byte[24];
        if ((basicHdr[0] & 0x0f) == 0x01) { // Common header - Secure mode disabled
            byte[] commonHdr = new byte[8];
            System.arraycopy(message, 4, commonHdr, 0, 8);
            byte[] htHst = new byte[1];
            System.arraycopy(commonHdr, 1, htHst, 0, 1);
            headerType = (int)(htHst[0] >> 4);
            headerSubType = (int)(htHst[0] & 0x000000000F);
            // Update LPV table
            int sopvPos = 12;
            if(headerType != HT_BEACON && !(headerType == HT_TSB && headerSubType == HST_SHB) ) {
                sopvPos += 4;
            }        
            System.arraycopy(message, sopvPos, sopv, 0, 24);
        } else if ((basicHdr[0] & 0x0f) == 0x02) { // Secured tag
            byte[] payload = SecurityHelper.getInstance().checkSecuredProfileAndExtractPayload(message, basicHdr.length, management.isEnforceSecuredModeSet(), management.getItsAidOther());
            if (payload != null) {
//                    System.out.println("GnPort.receive: payload=" + ByteHelper.byteArrayToString(payload));
                byte[] commonHdr = new byte[8];
                System.arraycopy(payload, 0, commonHdr, 0, 8);
//                    System.out.println("GnPort.receive: commonHdr=" + ByteHelper.byteArrayToString(commonHdr));
                byte[] htHst = new byte[1];
                System.arraycopy(commonHdr, 1, htHst, 0, 1);
                headerType = (int)(htHst[0] >> 4);
                headerSubType = (int)(htHst[0] & 0x000000000F);
                // Update LPV table
                int sopvPos = commonHdr.length;
                if(headerType != HT_BEACON && !(headerType == HT_TSB && headerSubType == HST_SHB) ) {
                    sopvPos += 4;
                }
                System.arraycopy(payload, sopvPos, sopv, 0, 24);
            } else {
                // Drop it
                System.err.println("GnPort.receive: Invalid packet");
                return;
            }
        } else {
            // Drop it
            System.err.println("GnPort.receive: Invalid basic header type");
            return;
        }
        System.out.println("GnPort.receive: sopv=" + ByteHelper.byteArrayToString(sopv));
        
        byte[] gn = new byte[8];
        System.arraycopy(sopv, 0, gn, 0, 8);
        byte[] mid = new byte[6];
        System.arraycopy(gn, 2, mid, 0, 6);
        byte[] tst = new byte[4];
        System.arraycopy(sopv, 8, tst, 0, 4);
        management.gnUpdateLocTable(mid, ByteHelper.byteArrayToInt(tst), sopv);
        
        // Filter beacons
        byte [] beaconFilter = management.getGnEnqueueBeacon();
        if(headerType != HT_BEACON || beaconFilter != null) {
            if(headerType == HT_BEACON) {
                byte[] filterMid = new byte[6];
                System.arraycopy(beaconFilter, 2, filterMid, 0, 6);
                if(java.util.Arrays.equals(mid, filterMid) == false) {
                	System.out.println("Received beacon does not match filterMid: " +  
                			ByteHelper.byteArrayToString(filterMid) + " (incoming mid: "+ ByteHelper.byteArrayToString(mid)+")");
                    return;
                }
            }    
            // Encode with GN indication header
            // Extract LINK_LAYER_DESTINATION
            byte[] msgInd = ByteHelper.concat(message, (byte[])lowerInfo.get(EthernetLayer.LINK_LAYER_DESTINATION));
            super.receive(msgInd, lowerInfo);
        }
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.ports.ProtocolPort#dispose()
     */
    @Override
    public void dispose() {
        if(running && beaconThread != null) {
            running = false;
            try {
                beaconThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.dispose();
    }
    
    private byte[] createSecuredMessage(final byte[] message) {
        System.out.println(">>> GnPort.createSecuredMessage: " + ByteHelper.byteArrayToString(message));
        
        // Extract and update the basicHeader
        byte[] basicHdr = ByteHelper.extract(message, 0, 4);
        if ((basicHdr[0] & 0x02) == 0x02) { // Message already secured by the TE
            return message;
        }
        // Set nextHeader to secured 
        basicHdr[0] &= 0xFE;
        basicHdr[0] |= 0x02;
        System.out.println("GnPort.createSecuredMessage: basicHdr=" + ByteHelper.byteArrayToString(basicHdr));
        
        // Extract and update the basicHeader
        byte[] commonHdr = ByteHelper.extract(message, 4, 8);
        System.out.println("GnPort.createSecuredMessage: commonHdr=" + ByteHelper.byteArrayToString(commonHdr));
        
        // Extract SecuredMessage payload
        byte[] securedPayload = ByteHelper.extract(message, 4, message.length - 4);
        int payloadLength = securedPayload.length;
        
        // Build the generation time value
        long curtime = System.currentTimeMillis();
        byte[] generationTime = ByteHelper.longToByteArray((long)(curtime - 1072915200000L) * 1000L, Long.SIZE / Byte.SIZE); // In microseconds
        // Build the payload to be signed
        byte[] headersField = ByteHelper.concat(
            ByteHelper.concat(                                // SecuredMessage HeaderFields
                new byte[] {
                    (byte)0x80,                               // signerInfo
                    (byte)0x01                                //     Certificate digest with ecdsap256
                },
                management.getAtCertificateDigest(),          //         HashedId8
                new byte[] {
                    (byte)0x00,                               // generationTime
                },
                generationTime                                //    Time64 value
            )
        );
        if ((commonHdr[0] & 0xF0) == 0x10) { // Next header = Btp-A
            headersField = ByteHelper.concat(
                headersField,
                new byte[] {
                    (byte)0x05,                               // its-aid
                    (byte)0x24                                //     26 = CAM
                }
            );
        } else if ((commonHdr[0] & 0xF0) == 0x20) { // Next header = Btp-B
            headersField = ByteHelper.concat(
                headersField,
                new byte[] { 
                    (byte)0x03                                // GenerationLocation
                },
                management.getLatitude(),                     //     Latitude
                management.getLongitude(),                    //     Longitude
                new byte[] { (byte)0x00, (byte)0x00 },        //     Elevation
                new byte[] {
                    (byte)0x05,                               // its-aid
                    (byte)0x25                                //     27 = DENM
                }
            );
        } else { // Add Its-Aid for Other profile
            int itsAid = management.getItsAidOther();
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
        }
        byte[] toBeSignedData = ByteHelper.concat(
            new byte[] {                                      // SecuredMessage version 
                (byte)0x02                                    //     version
            },
            new byte[] { (byte)headersField.length },         // HeadersField length
            headersField,                                     // HeaderFields
            new byte[] {                                      // SecuredMessage Payloads
                (byte)0x01,                                   //     Secured payload type: signed (1)
                (byte)payloadLength,                          //     Data payload length
            },
            securedPayload,
            new byte[] { (byte)0x01 },                        // Signature
            new byte[] { (byte)0x43 }                         // Signature length
        );
        System.out.println("GnPort.createSecuredMessage: toBeSignedData=" + ByteHelper.byteArrayToString(toBeSignedData));
        
        byte[] toBeSent = null;
        try {
            // Calculate the hash
            byte[] hash = CryptoLib.hashWithSha256(toBeSignedData);
            System.out.println("GnPort.createSecuredMessage: hash=" + ByteHelper.byteArrayToString(hash));
            // Signed the hash
            byte[] signatureBytes = CryptoLib.signWithEcdsaNistp256WithSha256(hash, management.getSigningPrivateKey());
            System.out.println("GnPort.createSecuredMessage: signatureBytes=" + ByteHelper.byteArrayToString(signatureBytes));
            // Add signature
            toBeSent  = ByteHelper.concat(
                basicHdr,
                toBeSignedData,
                new byte[] { 0x01, 0x00, 0x02 }, // Signature header
                ByteHelper.extract(signatureBytes, 2, signatureBytes.length - 2)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("GnPort.createSecuredMessage: toBeSent=" + ByteHelper.byteArrayToString(toBeSent));
        System.out.println("\n\n");
        return toBeSent;
    }
    
    /**
     * Indicates whether the port is still active. Setting this field to false will cause
     * the beaconing thread to stop its execution.
     */
    private volatile boolean running;
    
    /**
     * Beaconing thread instance.
     */
    private Thread beaconThread = null;
}

