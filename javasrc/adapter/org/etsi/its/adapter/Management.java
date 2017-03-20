/**
 *  This class is used to centralise test adapter configuration and execution parameters 
 *  All settings are component specific (multiton)
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/Management.java $
 *              $Id: Management.java 2230 2015-06-03 09:11:02Z mullers $
 *
 */

package org.etsi.its.adapter;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.etsi.adapter.TERFactory;
import org.etsi.certificates.CertificatesIOFactory;
import org.etsi.certificates.io.ICertificatesIO;
import org.etsi.common.ByteHelper;
import org.etsi.common.ITuple;
import org.etsi.common.Tuple;
import org.etsi.its.adapter.ports.FsapPort;
import org.etsi.its.adapter.ports.GnPort;
import org.etsi.ttcn.tci.CharstringValue;

import de.fraunhofer.sit.c2x.CryptoLib;

/**
 *  This class is used to centralise test adapter configuration and execution parameters 
 *  All settings are component specific (multiton)
 */
public class Management implements IManagementTA, IManagementLayers {

    /**
     * Instances of Management
     */
    private static final ConcurrentMap<String, Management> instances = new ConcurrentHashMap<String, Management>();
    
    /**
     * GeoNetworking beaconning interval
     */
    private static final int GN_BEACON_INTERVAL = Integer.decode(((CharstringValue)TERFactory.getInstance().getTaParameter("TsBeaconInterval")).getString());

    /**
     * Maximum time for getting Long position vector (in seconds)
     */
    private static final int GET_LPV_TIMEOUT = 10;

    /**
     * Interval for polling the location table during GetLpv (in ms)
     */
    private static final long GET_LPV_POLL_INTERVAL = 1000;
    
    /**
     * Test system latitude 
     */
    private static final int latitude = Integer.decode(((CharstringValue)TERFactory.getInstance().getTaParameter("TsLatitude")).getString());
    
    /**
     * Test system longitude
     */
    private static final int longitude = Integer.decode(((CharstringValue)TERFactory.getInstance().getTaParameter("TsLongitude")).getString());
    
    /**
     * Secured mode status 
     */
    private static final String TsSecuredMode = ((CharstringValue)TERFactory.getInstance().getTaParameter("TsSecuredMode")).getString();
    
    /**
     * Secured root path to access certificates & private keys 
     */
    private static final String TsSecuredRootPath = ((CharstringValue)TERFactory.getInstance().getTaParameter("TsSecuredRootPath")).getString();
    
    /**
     * Secured configuration identifier 
     */
    private static final String TsSecuredConfiId = ((CharstringValue)TERFactory.getInstance().getTaParameter("TsSecuredConfiId")).getString();
    
    /**
     * ITS-AID for Secure other profile 
     */
    private static final String TsItsAidOther = ((CharstringValue)TERFactory.getInstance().getTaParameter("TsItsAidOther")).getString();
    
    /**
     * Link-layer address of Component
     */
    private byte[] linkLayerAddress = null;

    /**
     * Registered GN Port
     */
    private GnPort gnPort = null;

    /**
     * Registered FSAP Port
     */
    private FsapPort fsapPort = null; // FIXME Enhance this using Fsap.send() method
    
    /**
     * Set to true is secured mode is set
     */
    private boolean securedMode = false;
    
    /**
     * The certificate identifier to used
     */
    private String certificateId = "CERT_TS_A_AT";
    
    /**
     * The AT certificate
     */
    private byte[] atCertificate = null;
    
    /**
     * The certificate digest to used
     */
    private byte[] atCertificateDigest = null;
    
    /**
     * The private signing key to used
     */
    private byte[] signingPrivateKey = null;
    
    /**
     * The public signing key X to used
     */
    private byte[] signingPublicKeyX = null;
    
    /**
     * The public signing key Y to used
     */
    private byte[] signingPublicKeyY = null;
//    private byte[] toBeSignedDataDigest = null;
//    private byte[] toBeSignedDataCertificate = null;
    
    /**
     * Private constructor (Multiton pattern)
     */
    private Management() {
        
        // Check for secured mode settings in TestAdapter configuration file
        if (TsSecuredMode.equals("true")) {
            setupSecuredMode();
        }
        
        // For debug only:
        byte[] mid = new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                                 (byte)0x00, (byte)0x00};
        byte[] lpv = new byte[] {(byte)0x0B, (byte)0x00, (byte)0x00, (byte)0x00, 
                                 (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                                 (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                                 (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                                 (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                                 (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
        gnUpdateLocTable(mid, 0, lpv);
    }

    /**
     * Gets the Management instance associated to a component  
     * @param   key Component name
     * @return  Management instance
     */
    public static Management getInstance(String key) {
        if (instances.get(key) == null){
            // Lazily create instance and try to add it to the map
            Management instance = new Management();
            instances.putIfAbsent(key, instance);
        }
        return instances.get(key);
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementTA#startBeaconing(byte[])
     */
    @Override
    public void startBeaconing(byte[] beaconHeader) {
        this.beaconHeader = beaconHeader;
        if(gnPort != null) {
            gnPort.startBeaconning();
        }
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementTA#stopBeaconing()
     */
    @Override
    public void stopBeaconing() {
        this.beaconHeader = null;
        if(gnPort != null) {
            gnPort.stopBeaconning();
        }
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementTA#startEnqueueingBeacons(byte[])
     */
    @Override
    public void startEnqueueingBeacons(byte[] beaconHeader) {
        this.enqueueBeacon = beaconHeader;
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementTA#stopEnqueueingBeacons()
     */
    @Override
    public void stopEnqueueingBeacons() {
        this.enqueueBeacon = null;
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementTA#startMultipleBeaconing(byte[], int)
     */
    @Override
    public void startMultipleBeaconing(byte[] beaconHeader, int nbNeighbours) {
        /* TODO: Multiple beacons */
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementTA#stopMultipleBeaconing()
     */
    @Override
    public void stopMultipleBeaconing() {
        /* TODO: Multiple beacons */
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementTA#getLongPositionVector(byte[])
     */
    @Override
    public byte[] getLongPositionVector(byte[] targetGnAddress) {
    	
    	System.out.println("locTable:");
		Iterator<Long> it = locTable.keySet().iterator();
		while (it.hasNext()) {
			Long l = it.next();
			ITuple<Long, byte[]> u = locTable.get(l);
			System.out.println(String.valueOf(l)+"->"+String.valueOf(u.getA())+":"+ByteHelper.byteArrayToString(u.getB()));
		}
		System.out.println("End locTable");
		
        byte[] mid = ByteHelper.extract(targetGnAddress, 2, 6);
        long key = ByteHelper.byteArrayToLong(mid);
        for(int i = 0; i < GET_LPV_TIMEOUT; ++i) {
            if (locTable.containsKey(key)) {
                ITuple<Long, byte[]> entry = locTable.get(key);
                return entry.getB();    
            }
            try {
                Thread.sleep(GET_LPV_POLL_INTERVAL);
            } catch (InterruptedException e) {
                // Do nothing, we do not care
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementLayers#getGnBeacon()
     */
    @Override
    public byte[] getGnBeacon() {
        return beaconHeader;
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementLayers#getGnBeaconInterval()
     */
    @Override
    public int getGnBeaconInterval() {
        return GN_BEACON_INTERVAL;
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementLayers#getGnEnqueueBeacon()
     */
    @Override
    public byte[] getGnEnqueueBeacon() {
        return enqueueBeacon;
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementLayers#gnUpdateLocTable(byte[], long, byte[])
     */
    @Override
    public void gnUpdateLocTable(byte[] mid, long timestamp, byte[] lpv) {
        // Java does not provide unsigned int
        timestamp &= 0xffffffffL;
        long key = ByteHelper.byteArrayToLong(mid);
        ITuple<Long, byte[]> entry = locTable.get(key);
        if(entry == null || entry.getA() < timestamp) {
            //System.out.println("gnUpdateLocTable: Adding Loc Entry for: " + ByteHelper.byteArrayToString(mid));
            locTable.put(key, new Tuple<Long, byte[]>(timestamp, lpv));
        }
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementLayers#getLinkLayerAddress()
     */
    @Override
    public byte[] getLinkLayerAddress() {
        return linkLayerAddress;
    }
    
    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementLayers#getLinkLayerAddress()
     */
    @Override
    public void setLinkLayerAddress(byte[] linkLayerAddress) {
        this.linkLayerAddress = linkLayerAddress;
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementLayers#getLatitude()
     */
    @Override
    public byte[] getLatitude() {
        return ByteHelper.intToByteArray(latitude, 4);
    }

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementLayers#getLongitude()
     */
    @Override
    public byte[] getLongitude() {
        return ByteHelper.intToByteArray(longitude, 4);
    }
    
    /**
     * Set to null in order to prevent Test Adapter from sending beacons
     * Otherwise, it shall be set to a valid encoded beacon header to be send periodically by Test Adapter
     * @see startEnqueueingBeacons
     * @see stopEnqueueingBeacons
     */
    private byte[] beaconHeader = null;
        
    /**
     * Set to null if received Beacon messages have to be discarded by test adapter (= not enqueued)
     * Otherwise, it shall be set to the to an encoded beacon header value acting as a filter for enqueueing received beacons
     * @see startBeaconing
     * @see stopBeaconing    
     */
    private byte[] enqueueBeacon = null;
    
    /**
     * Table used to store neighbours (= SUT) position vectors
     * @see gnUpdateLocTable
     * @see getLongPositionVector
     */
    private static Map<Long, ITuple<Long, byte[]>> locTable = new HashMap<Long, ITuple<Long, byte[]>>();

    /* (non-Javadoc)
     * @see org.etsi.its.adapter.IManagementLayers#registerBeaconThread(java.lang.Thread)
     */
    @Override
    public void registerGnPort(GnPort gnPort) {
        this.gnPort = gnPort;
        
        // Ensure that management settings are reset
        beaconHeader = null;
        enqueueBeacon = null;
    }

    @Override
    public  void setSecuredMode(final byte[] securityData) {
        certificateId = ByteHelper.byteArrayWithLengthToString(ByteHelper.concat(ByteHelper.intToByteArray(securityData.length, 4), securityData));
        setupSecuredMode();
    }

    @Override
    public void unsetSecuredMode() {
        securedMode = false;
        signingPrivateKey = null;
        signingPublicKeyX = null;
        signingPublicKeyY = null;
        atCertificate = null;
        atCertificateDigest = null;
    }
    
    @Override
    public boolean isEnforceSecuredModeSet() {
        return TsSecuredMode.equals("true");
    }
    
    @Override
    public boolean isSecuredModeSet() {
        return securedMode;
    }
    
    @Override
    public BigInteger getSigningPrivateKey() {
        return new BigInteger(signingPrivateKey);
    }
    
    @Override
    public byte[] getSigningPublicKeyX() {
        return signingPublicKeyX;
    }
    
    @Override
    public byte[] getSigningPublicKeyY() {
        return signingPublicKeyY;
    }
    
    @Override
    public byte[] getAtCertificate() {
        return atCertificate;
    }
    
    @Override
    public byte[] getAtCertificateDigest() {
        return atCertificateDigest;
    }
    
    @Override
    public int getItsAidOther() {
        return new Integer(TsItsAidOther).intValue();
    }
    
    /**
     * @desc This method setup secured mode according to the Test adapter settings (@see TsSecuredMode flags).
     *       The secured mode could be overrided by test case secured mode configuration through AC primitives
     * @remark This method shall be called by the constructor only
     */
    private void setupSecuredMode() {
//        System.out.println(">>> setupSecuredMode: " + certificateId);
        
        securedMode = true;
        ICertificatesIO _certCache = CertificatesIOFactory.getInstance();
        if (!_certCache.loadCertificates(TsSecuredRootPath, TsSecuredConfiId)) {
            securedMode = false;
        } else {
            ByteArrayOutputStream certificate = new ByteArrayOutputStream();
            _certCache.readCertificate(certificateId, certificate);
            // Extract public keys
            atCertificate = certificate.toByteArray();
//            System.out.println("Management.setupSecuredModeFromTaConfig: certificate=" + ByteHelper.byteArrayToString(atCertificate));
            // Compute AT certificate digest
            byte[] atHash = CryptoLib.hashWithSha256(atCertificate);
            atCertificateDigest = ByteHelper.extract(atHash, atHash.length - 8, 8);
//            System.out.println("Management.setupSecuredModeFromTaConfig: atCertificateDigest=" + ByteHelper.byteArrayToString(atCertificateDigest));
            int offset = 16; // FIXME To be enhanced
            // KeyX
            signingPublicKeyX = new byte[32];
            System.arraycopy(atCertificate, offset, signingPublicKeyX, 0, 32);
            offset += 32;
//            System.out.println("Management.setupSecuredModeFromTaConfig: signingPublicKeyX=" + ByteHelper.byteArrayToString(signingPublicKeyX));
            // KeyY
            signingPublicKeyY = new byte[32];
            System.arraycopy(atCertificate, offset, signingPublicKeyY, 0, 32);
//            System.out.println("Management.setupSecuredModeFromTaConfig: signingPublicKeyY=" + ByteHelper.byteArrayToString(signingPublicKeyY));
            // Extract private keys
            ByteArrayOutputStream signingPrivateKey = new ByteArrayOutputStream();
            _certCache.readSigningKey(certificateId, signingPrivateKey);
            this.signingPrivateKey = signingPrivateKey.toByteArray().clone();
//            System.out.println("Management.setupSecuredModeFromTaConfig: signingPrivateKey=" + ByteHelper.byteArrayToString(this.signingPrivateKey));
            // TODO Add support of encryption
        }
    }

    /**
     * Registers a FSAP port
     * @param The FSAP port to register 
     */
    @Override
    public void registerFsapPort(final FsapPort fsapPort) {
        this.fsapPort = fsapPort;
    }

    @Override
    public void startSamTransmission(final byte[] sam) {
        if(fsapPort != null) {
            fsapPort.startSamTransmission(sam);
        }
    }
    
    @Override
    public void stopSamTransmission() {
        if(fsapPort != null) {
            fsapPort.stopSamTransmission();
        }
    }
    
} // End of class Management
