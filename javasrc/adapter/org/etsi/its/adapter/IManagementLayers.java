/**
 *  Management interface for protocol layers
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/IManagementLayers.java $
 *                $Id: IManagementLayers.java 2230 2015-06-03 09:11:02Z mullers $
 *
 */

package org.etsi.its.adapter;

import java.math.BigInteger;

import org.etsi.its.adapter.ports.FsapPort;
import org.etsi.its.adapter.ports.GnPort;

/**
 *  Management interface for protocol layers
 */
public interface IManagementLayers {

    /**
     * Gets the GeoNetworking beacon header to be sent by Test Adapter for the current component
     * @return Beacon header, or null if no Beacon shall be sent 
     */
    public byte[] getGnBeacon();
    
    /**
     * Registers a GN port
     */
    public void registerGnPort(GnPort gnPort);

    /**
     * Registers a FSAP port
     * @param The FSAP port to register 
     */
    public void registerFsapPort(final FsapPort fntpPort); 


    /**
     * Gets the GeoNetworking beaconing interval
     * @return GeoNetworking beaconing interval in ms
     */
    public int getGnBeaconInterval();

    /**
     * Gets the GeoNetworking beacon header acting as filter for enqueueing Beacons received from neighbours
     * @return Beacon header, or null if no Beacon shall be enqueued 
     */
    public byte[] getGnEnqueueBeacon();
    
    /**
     * Inserts or updates a neighbour position vector in Test Adapter internal tables
     * @param  mid         Mid part of the neighbour's GN_Address
     * @param  timestamp   Timestamp of the carrying message
     * @param  lpv         Long position vector of the neighbour
     */
    public void gnUpdateLocTable(byte[] mid, long timestamp, byte[] lpv);

    /**
     * Sets the link layer address of this component
     * param  linkLayerAddress   Link-layer address (6 bytes)
     */
    public void setLinkLayerAddress(byte[] linkLayerAddress);
    
    /**
     * Gets the link layer address of this component
     * @return Link-layer address (6 bytes)
     */
    public byte[] getLinkLayerAddress();

    /**
     * Gets the latitude of this component
     * @return Latitude
     */
    public byte[] getLatitude();

    /**
     * Gets the Longitude of this component
     * @return Longitude
     */
    public byte[] getLongitude();

    /**
     * Enable the secured mode
     * @param securityData    data required to execute the signing process on beacons
     */
    public void setSecuredMode(final byte[] securityData);

    /**
     * Disable the secured mode
     */
    public void unsetSecuredMode();
    
    /**
     * In case TA shall fully support secured mode, security checks shall be applied
     * Otherwise the Security ATS shall manage the security checks as part of testing
     * @return true if secured mode is set, false otherwise
     */
    public boolean isEnforceSecuredModeSet();
    
    /**
     * Gets the secured mode status
     * @return true if secured mode is set, false otherwise
     * 
     * @remark When the secured mode status is set, the TA shall at least filter secured packet to extract SOPV, LS_REQUEST and so on
     */
    public boolean isSecuredModeSet();
    
    /**
     * Gets the private key for signing process
     * @return The private key
     */
    public BigInteger getSigningPrivateKey();

    /**
     * Gets the public key X for signing check
     * @return The public key X
     */
    public byte[] getSigningPublicKeyX();
    
    /**
     * Gets the public key Y for signing check
     * @return The public key Y
     */
    public byte[] getSigningPublicKeyY();
    
    /**
     * Gets the AT certificate value
     * @return The AT certificate value
     * @remark It shall not be used when secured mode is set by the test execution
     */
    byte[] getAtCertificate();
    
    /**
     * Gets the Hashed8 value from the AT certificate
     * @return The Hashed8 value
     * @remark It shall not be used when secured mode is set by the test execution
     */
    byte[] getAtCertificateDigest();
    
    /**
     * Requests ITS-AID for other profile 
     */
    int getItsAidOther();
    
}
