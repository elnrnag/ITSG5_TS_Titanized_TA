/**
 *  Management interface for Test Adapter main class 
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/IManagementTA.java $
 *              $Id: IManagementTA.java 1827 2014-11-18 10:05:45Z berge $
 *
 */

package org.etsi.its.adapter;

/**
 *  Management interface for Test Adapter main class 
 */
public interface IManagementTA {

	/**
	 * Requests Test Adapter to start sending periodic beacons for the current component
	 * @param  beaconHeader    Beacon to be sent (TST field will be updated)
	 */
	public void startBeaconing(byte[] beaconHeader);
	
	/**
	 * Requests Test Adapter to stop sending periodic beacons for the current component
	 */
	public void stopBeaconing();
	
	/**
	 * Requests Test Adapter to start enqueueing beacon messages on the current component GN port
	 * @param  beaconHeader    Only messages matching this beacon header will be enqueued
	 */
	public void startEnqueueingBeacons(byte[] beaconHeader);
	
	/**
	 * Requests Test Adapter to stop enqueueing beacon messages on the current component GN port
	 */
	public void stopEnqueueingBeacons();
	
	/**
	 * Requests Test Adapter to start simulating neighbour presence by sending multiple periodic beacons for the current component
	 * @param  beaconHeader    Beacon to be sent (TST field will be updated, GN addresses will be random)
	 * @param  nbNeighbours    Number of neighbours to simulate
	 */
	public void startMultipleBeaconing(byte[] beaconHeader, int nbNeighbours);
	
	/**
	 * Requests Test Adapter to stop simulating neighbour presence 
	 */
	public void stopMultipleBeaconing();
	
	/**
	 * Gets the long position vector of a neighbour given its GN_Address
	 * @param  targetGnAddress GN_Address of the target neighbour
	 * @return Long position vector of the target neighbour as received in its last beacon, or null
	 */
	public byte[] getLongPositionVector(byte[] targetGnAddress);

	/**
	 * Requests Test Adapter to trigger a Service Provider InSapPrimitiveUp/SAM message sending 
	 * @param sam The SAM message to transmit 
	 */
	public void startSamTransmission(final byte[] sam); 
	
	/**
	 * Requests Test Adapter to stop a Service Provider InSapPrimitiveUp/SAM message sending 
	 * @param sam The SAM message to transmit 
	 */
	public void stopSamTransmission(); 
	
}
