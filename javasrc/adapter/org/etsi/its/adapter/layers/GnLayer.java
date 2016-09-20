/**
 *  Implementation of ITS GeoNetworking layer (background thread)
 *  
 *  @author     ETSI / STF424
 *  @version    $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/adapter/org/etsi/its/adapter/layers/GnLayer.java $
 *              $Id: GnLayer.java 2230 2015-06-03 09:11:02Z mullers $
 *
 */
package org.etsi.its.adapter.layers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.etsi.adapter.TERFactory;
import org.etsi.common.ByteHelper;
import org.etsi.its.adapter.IManagementLayers;
import org.etsi.its.adapter.SecurityHelper;
import org.etsi.ttcn.tci.CharstringValue;

import de.fraunhofer.sit.c2x.CryptoLib;

/**
 *  Implementation of ITS GeoNetworking layer (background thread)
 */
public class GnLayer extends Layer implements Runnable, IEthernetSpecific {

	/**
	 * Implemented version of GeoNetworking specification
	 */
	private static final int GN_VERSION = 0;    

	/**
	 * Parameter name for GeoNetworking packet type
	 */
	public static final String GN_TYPE = "GnType";

	/**
	 * Parameter name for GeoNetworking packet sub-type
	 */
	public static final String GN_SUBTYPE = "GnSubType";

	/**
	 * Parameter name for GeoNetworking payload type
	 */
	public static final String GN_NEXTHEADER = "GnNextHeader";

	/**
	 * Parameter name for destination position vector
	 */
	public static final String GN_DEPV = "GnDePV";

	/**
	 * Parameter name for Location Service's Target GN_Address 
	 */
	public static final String GN_TARGETGNADDR = "GnTargetGnAddress";

	/**
	 * Parameter name for destination area's latitude
	 */
	public static final String GN_LATITUDE = "GnLatitude";

	/**
	 * Parameter name for destination area's longitude
	 */
	public static final String GN_LONGITUDE = "GnLongitude";

	/**
	 * Parameter name for destination area's distance A
	 */
	public static final String GN_DISTANCEA = "GnDistanceA";

	/**
	 * Parameter name for destination area's distance B
	 */
	public static final String GN_DISTANCEB = "GnDistanceB";

	/**
	 * Parameter name for destination area's angle
	 */
	public static final String GN_ANGLE = "GnAngle";

	/**
	 * Parameter name for traffic class
	 */
	public static final String GN_TRAFFICCLASS = "GnTrafficClass";

	/**
	 * Parameter name for packet's lifetime
	 */
	public static final String GN_LIFETIME = "GnLifetime";

	/**
	 * GeoNetworking header type for unknown messages
	 */
	public static final int HT_ANY = 0;

	/**
	 * GeoNetworking header type for beacon messages
	 */
	public static final int HT_BEACON = 1;

	/**
	 * GeoNetworking header type for GeoUnicast messages
	 */
	public static final int HT_GEOUNICAST = 2;

	/**
	 * GeoNetworking header type for GeoAnycast messages
	 */
	public static final int HT_GEOANYCAST = 3;

	/**
	 * GeoNetworking header type for GeoBroadcast messages
	 */
	public static final int HT_GEOBROADCAST = 4;

	/**
	 * GeoNetworking header type for Topology-scoped broadcast messages
	 */
	public static final int HT_TSB = 5;

	/**
	 * GeoNetworking header type for Location Service messages
	 */
	public static final int HT_LS = 6;


	/**
	 * Unspecified GeoNetworking header sub-type
	 */    
	public static final int HST_UNSPECIFIED = 0;

	/**
	 * Circle sub-type for GeoBroadcast/GeoAnycast messages
	 */    
	public static final int HST_CIRCLE = 0;

	/**
	 * Rectangle sub-type for GeoBroadcast/GeoAnycast messages
	 */    
	public static final int HST_RECT = 1;

	/**
	 * Ellipse sub-type for GeoBroadcast/GeoAnycast messages
	 */    
	public static final int HST_ELIPSE = 2;

	/**
	 * Single-hop sub-type for TSB messages
	 */    
	public static final int HST_SINGLEHOP = 0;

	/**
	 * Multi-hop sub-type for TSB messages
	 */    
	public static final int HST_MULTIHOP = 1;

	/**
	 * LS-Request sub-type for LS messages
	 */    
	public static final int HST_LSREQUEST = 0;

	/**
	 * LS-Reply sub-type for LS messages
	 */    
	public static final int HST_LSREPLY = 1;

	/**
	 * UTC ITS time reference: 01/01/2004 00:00:00 GMT
	 */
	private static final long ITS_REF_TIME = 1072915200000L;

	/**
	 * Constructor
	 * @param  management   Layer management instance
	 * @param  lowerStack   Lower protocol stack   
	 */
	public GnLayer(IManagementLayers management, Stack<String> lowerStack) {
		super(management, lowerStack);
		sequenceNumber = 0;
		running = true;
		beaconThread = new Thread(this);
		beaconThread.start();
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
	 * Thread function for sending periodic beacons
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(GN_TYPE, HT_BEACON);
		while(running) {
			if(management.getGnBeacon() != null) {
				send(null, params);
			}
			try {
				Thread.sleep(management.getGnBeaconInterval()); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#unregister(org.etsi.its.adapter.layers.Layer)
	 */
	@Override
	public void unregister(Layer upperLayer) {
		if(running) {
			running = false;
			try {
				beaconThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.unregister(upperLayer);
	}

	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#send(byte[], java.util.Map)
	 */
	@Override
	public boolean send(byte[] message, Map<String, Object> params) {
		System.out.println(">>> GnLayer.send: " + ByteHelper.byteArrayToString(message));

		byte [] extHdr = null;
		int ht;
		int hst;
		int hoplimit = 255;

		try {
			ht = (Integer)params.get(GN_TYPE);
		}
		catch (NullPointerException e) {
			ht = HT_ANY;
		}    

		try {
			hst = (Integer)params.get(GN_SUBTYPE);
		}
		catch (NullPointerException e) {
			hst = HST_UNSPECIFIED;
		}

		switch(ht) {
		case HT_LS:
			if(hst == HST_LSREPLY) {
				extHdr = createLsReplyHeader((byte[])params.get(GN_DEPV));
			}
			else {
				extHdr = createLsRequestHeader((byte[])params.get(GN_TARGETGNADDR));
			}
			break;    
		case HT_GEOUNICAST:
			extHdr = createGeoUnicastHeader((byte[])params.get(GN_DEPV));
			break;
		case HT_TSB:
			if(hst == HST_MULTIHOP) { 
				extHdr = createTsbHeader();
			}
			else {
				hoplimit = 1;
				extHdr = createShbHeader();
			}
			break;
		case HT_GEOBROADCAST:
		case HT_GEOANYCAST:
			extHdr = createGeoBroadcastHeader( 
					(Long)params.get(GN_LATITUDE),
					(Long)params.get(GN_LONGITUDE),
					(Integer)params.get(GN_DISTANCEA),
					(Integer)params.get(GN_DISTANCEB),
					(Integer)params.get(GN_ANGLE)
					);
		}

		byte[] toBeSent = null;
		byte[] basicHdr = createBasicHeader();
		byte[] commonHdr = createCommonHeader((String)params.get(GN_NEXTHEADER), ht, hst, (message == null)?0:message.length, hoplimit);
		if (!management.isSecuredModeSet()) { // Secure mode disabled
			toBeSent = ByteHelper.concat(basicHdr, commonHdr, extHdr, message);
		} else {
			toBeSent = createSecuredMessage(basicHdr, commonHdr, extHdr, message);
		}

		System.out.println("<<< GnLayer.send: " + ByteHelper.byteArrayToString(toBeSent));
		return super.send(toBeSent, params);
	}

	/* (non-Javadoc)
	 * @see org.etsi.its.adapter.layers.Layer#receive(byte[])
	 */
	@Override
	public void receive(byte[] message, Map<String, Object> lowerInfo) {
		System.out.println(">>> GnLayer.receive: " + ByteHelper.byteArrayToString(message));
		
		final int basicHeaderLength  = 4;
		
		byte[] basicHdr = new byte[basicHeaderLength]; 
		System.arraycopy(message, 0, basicHdr, 0, basicHeaderLength);
		byte[] versionNh = new byte[1];
		System.arraycopy(basicHdr, 0, versionNh, 0, 1);
		byte nextHeader = (byte)(versionNh[0] & (byte)0x0F);
		int lt_multiplier = ((basicHdr[2] & (byte)0xFC) >> 2)&0x3F;
		int lt_base = basicHdr[2] & (byte)0x03;
		int lifetime = computeGnLifeTime(lt_multiplier, lt_base);
		
		if (nextHeader == 0x01) { // Common header - Secure mode disabled
			final int commonHeaderLength = 8;
			byte[] commonHdr = new byte[commonHeaderLength];
			System.arraycopy(message, basicHeaderLength, commonHdr, 0, commonHeaderLength);
			nextHeader = (byte)((commonHdr[0] & (byte)0xF0) >> 4);
			int trafficClass = (int)(commonHdr[2]);

			byte[] htHst = new byte[1];
			System.arraycopy(commonHdr, 1, htHst, 0, 1);
			int headerSubType = (int)(htHst[0] & (byte)0x0F);
			int headerType = (int)(htHst[0] >> 4);

			byte[] pl = new byte[2];
			System.arraycopy(commonHdr, 4, pl , 0, 2);
			int payloadLength = ByteHelper.byteArrayToInt(pl);

			if(headerType == HT_LS) {
				// Process LS messages
				if(headerSubType == HST_LSREQUEST) {
					byte[] gnAddress = new byte[8];
					System.arraycopy(message, 68, gnAddress, 0, 8);
					byte[] mid = new byte[6];
					System.arraycopy(gnAddress, 2, mid, 0, 6);
					if(Arrays.equals(mid, management.getLinkLayerAddress()) == true) {
						// Send LS Reply
						byte[] depv = new byte[20];
						System.arraycopy(message, 40, depv, 0, 20);

						Map<String, Object> params = new HashMap<String, Object>();
						params.put(GN_DEPV, depv);
						params.put(GN_TYPE, HT_LS);
						params.put(GN_SUBTYPE, HST_LSREPLY);
						System.out.println("GnLayer.receive: Send LS_REPLAY in unsecured mode");
						send(null, params);
					}
				}
				else {
					// we are not interested in LS replies so far.
				}

//			} else if(headerType == HT_TSB) {
//				//For now we don't care about the TSB, but we need to overcome a strange behavior
//				//of the COHDA MK5 wireless router: It sends a 2bytes empty data at the end of the 
//				//btpIndication, which is not against the standard, however it is not required
//				//but it messes up the "message.length - payloadLength" position calculation
//				//TSB ExtendedHeader length = 28 -> payloadPosition = 4+8+28 = 40
//				if(payloadLength > 0) {
//					byte[] payload = new byte[payloadLength];
//					System.arraycopy(message, 40, payload, 0, payloadLength);
//					lowerInfo.put(GN_NEXTHEADER, nextHeader);
//					lowerInfo.put(GN_TYPE, headerType);
//					lowerInfo.put(GN_SUBTYPE, headerSubType);
//					lowerInfo.put(GN_LIFETIME, lifetime);
//					lowerInfo.put(GN_TRAFFICCLASS, trafficClass);
//					super.receive(payload, lowerInfo);
//				}
//
			} else {
				// Other messages
				if(payloadLength > 0) {
					int extendedHeaderLength = 0;
					if (headerType == 1) { // Beacon
						extendedHeaderLength = 24;
					} else if (headerType == 4) { // Geo Broadcast
						extendedHeaderLength = 44;
					} else if (headerType == 5) { // Topology-Scoped Broadcast
						extendedHeaderLength = 28;
					} // TODO To be continued

					byte[] payload = new byte[payloadLength];
					System.arraycopy(message, basicHeaderLength + commonHeaderLength + extendedHeaderLength, payload, 0, payloadLength);
					lowerInfo.put(GN_NEXTHEADER, nextHeader);
					lowerInfo.put(GN_TYPE, headerType);
					lowerInfo.put(GN_SUBTYPE, headerSubType);
					lowerInfo.put(GN_LIFETIME, lifetime);
					lowerInfo.put(GN_TRAFFICCLASS, trafficClass);
					super.receive(payload, lowerInfo);
				}
			}
		} else if (nextHeader == 0x02) { // Secured tag
			byte[] payload = SecurityHelper.getInstance().checkSecuredProfileAndExtractPayload(message, basicHdr.length, management.isEnforceSecuredModeSet(), management.getItsAidOther());
			if (payload != null) {
				//                    System.out.println("GnLayer.receive: payload=" + ByteHelper.byteArrayToString(payload));
				byte[] commonHdr = new byte[8];
				System.arraycopy(payload, 0, commonHdr, 0, 8);
				//                    System.out.println("GnLayer.receive: commonHdr=" + ByteHelper.byteArrayToString(commonHdr));
				nextHeader = (byte)((commonHdr[0] & (byte)0xF0) >> 4);
				int trafficClass = (int)(commonHdr[2]);

				byte[] htHst = new byte[1];
				System.arraycopy(commonHdr, 1, htHst, 0, 1);
				int headerType = (int)(htHst[0] >> 4);
				int headerSubType = (int)(htHst[0] & 0x000000000F);

				byte[] pl = new byte[2];
				System.arraycopy(commonHdr, 4, pl , 0, 2);
				int payloadLength = ByteHelper.byteArrayToInt(pl);
				//                    System.out.println("GnLayer.receive: Message payload length=" + payloadLength);

				if(headerType == HT_LS) {
					// Process LS messages
					if(headerSubType == HST_LSREQUEST) {
						int sopvPos = commonHdr.length + 3 + 8 + 3 * 4 + 2 * 2; // FIXME To be changed
						byte[] gnAddress = new byte[8];
						System.arraycopy(message, sopvPos, gnAddress, 0, 8);
						byte[] mid = new byte[6];
						System.arraycopy(gnAddress, 2, mid, 0, 6);
						if(Arrays.equals(mid, management.getLinkLayerAddress()) == true) {
							// Send LS Reply
							byte[] depv = new byte[20];
							System.arraycopy(payload, sopvPos, depv, 0, 20); // FIXME Check indexes 

							Map<String, Object> params = new HashMap<String, Object>();
							params.put(GN_DEPV, depv);
							params.put(GN_TYPE, HT_LS);
							params.put(GN_SUBTYPE, HST_LSREPLY);
							System.out.println("GnLayer.receive: Send LS_REPLAY in secured mode");
							send(null, params);
						}
					} else {
						// we are not interested in LS replies so far.
					}
				} else {
					// Other messages
					if(payloadLength > 0) {
						byte[] mpayload = new byte[payloadLength];
						System.arraycopy(payload, commonHdr.length + 28/*Topology-Scoped Broadcast*/, mpayload, 0, payloadLength);
						//                            System.out.println("GnLayer.receive: Message =" + ByteHelper.byteArrayToString(mpayload));
						lowerInfo.put(GN_NEXTHEADER, nextHeader);
						lowerInfo.put(GN_TYPE, headerType);
						lowerInfo.put(GN_SUBTYPE, headerSubType);
						lowerInfo.put(GN_LIFETIME, lifetime);
						lowerInfo.put(GN_TRAFFICCLASS, trafficClass);
						super.receive(mpayload, lowerInfo);
					}
				}
			} else {
				// Drop it
				System.err.println("GnLayer.receive: Invalid basic header type");
				return;
			}
		}
	} // End of method receive

	/**
	 * Computes GN lifetime value 
	 * @param lt_multiplier        GN LT multiplier
	 * @param lt_base            GN LT base
	 * @return GN lifetime value in ms    
	 */
	private int computeGnLifeTime(int lt_multiplier, int lt_base) {

		final int[] base = {50, 1000, 10000, 100000};

		return base[lt_base] * lt_multiplier;
	}

	/**
	 * Builds encoded Basic Header
	 * @return Encoded Basic Header
	 */
	private byte[] createBasicHeader() {
		// Version 4 bits
		// NextHeader 4 bits
		byte[] versionNh = new byte[1];
		int nh = 1;

		versionNh[0] = (byte)(GN_VERSION << 4); 
		versionNh[0] |= (byte)nh & 0x0F;
		if (management.isSecuredModeSet()) { // Secure mode enabled
			// Set nextHeader to secured 
			versionNh[0] &= 0xFE;
			versionNh[0] |= 0x02;
		}

		// Reserved 1 byte
		byte[] reserved = new byte[]{(byte)0x00};

		// Lifetime 1 byte
		byte[] lifetime = new byte[]{(byte)0x2B};

		// RHL 1 byte
		byte[] rhl = new byte[]{(byte)0x01};

		return ByteHelper.concat(versionNh, reserved, lifetime, rhl);
	}

	/**
	 * Builds encoded Common Header
	 * @param  nextHeader  Payload type 
	 * @param  ht          Packet type
	 * @param  hst         Packet sub-type
	 * @param  msgLength   Length of payload
	 * @return Encoded Common Header
	 */
	private byte[] createCommonHeader(String nextHeader, int ht, int hst, int msgLength, int hoplimit) {

		// Version 4 bits
		// NextHeader 4 bits
		byte[] nhReserved = new byte[1];
		int nh = 0;
		if(nextHeader != null) {
			if(nextHeader.equals("BTP-A")) {
				nh = 1;
			}    
			else if(nextHeader.equals("BTP-B")) {
				nh = 2;
			}
			else if(nextHeader.equals("IPv6")) {
				nh = 3;
			}
		}
		nhReserved[0] = (byte)(nh << 4); 

		// HeaderType 4 bits
		// HeaderSubType 4 bits
		byte[] htHst = new byte[1];
		htHst[0] = (byte)(ht << 4); 
		htHst[0] |= (byte)hst & 0x0F; 

		// Traffic Class 1 byte
		byte[] tc = new byte[]{(byte)0x00};

		// Flags 1 byte
		byte[] flags = new byte[]{(byte)0x00};

		// PayloadLength 2 bytes
		byte[] pl = ByteHelper.intToByteArray(msgLength, 2);

		// Maximum HopLimit 1 byte
		byte[] mhl = ByteHelper.intToByteArray(hoplimit, 1); 

		// Reserved 1 byte
		byte[] reserved = new byte[]{(byte)0x00};

		return ByteHelper.concat(nhReserved, htHst, tc, flags, pl, mhl, reserved);        
	}

	/**
	 * Builds self GN_Address based on link-layer address
	 * @return Encoded GN_Address
	 */
	private byte[] createMyGnAddress() {
		int ssc = 208; // France
		byte[] flags = new byte[2];
		flags[0] = (byte)(1 << 7); // Manual address
		flags[0] |= (byte)(9 << 3); // Ordinary RSU
		flags[0] |= (byte)(1 << 2); // Private
		flags[0] |= (byte)(ssc >> 10); // SSC
		flags[1] = (byte)ssc; // SSC
		byte[] mid = management.getLinkLayerAddress();
		return ByteHelper.concat(flags, mid);
	}

	/**
	 * Builds self long position vector
	 * @return Encoded long position vector
	 */
	private byte[] createMyLpv() { 

		byte[] gn = createMyGnAddress(); // 8 bytes 
		// Timestamp is 1s older than current time to avoid sending beacons coming from the future (time sync between nodes)
		byte[] tst = ByteHelper.intToByteArray((int)((System.currentTimeMillis() - ITS_REF_TIME) % (long)Math.pow(2,32) - 1000), 4);
		byte[] latitude = management.getLatitude();
		byte[] longitude = management.getLongitude();
		byte[] speed = ByteHelper.intToByteArray(0, 2);
		byte[] heading = ByteHelper.intToByteArray(0, 2);
		return ByteHelper.concat(gn, tst, latitude, longitude, speed, heading);
	}

	/**
	 * Builds GeoUnicast extension header
	 * @param  depv    Destination position vector (short position vector)
	 * @return Encoded GeoUnicast extension header 
	 */
	private byte[] createGeoUnicastHeader(byte[] depv) {
		byte[] tsb = createTsbHeader();
		return ByteHelper.concat(tsb, depv);        
	}

	/**
	 * Builds SHB extension header
	 * @return Encoded SHB extension header
	 */
	private byte[] createShbHeader() {
		byte[] sopv = createMyLpv();
		byte[] reserved = ByteHelper.intToByteArray(0, 4);
		return ByteHelper.concat(sopv, reserved);
	}

	/**
	 * Builds TSB extension header
	 * @return Encoded TSB extension header
	 */
	private byte[] createTsbHeader() {
		byte[] sn = ByteHelper.intToByteArray(sequenceNumber++ , 2);
		byte[] reserved = ByteHelper.intToByteArray(0, 2);
		byte[] sopv = createMyLpv();
		return ByteHelper.concat(sn, reserved, sopv);
	}

	/**
	 * Builds GeoBroadcast extension header
	 * @param geoAreaLatitude  Destination GeoArea's latitude
	 * @param geoAreaLongitude Destination GeoArea's longitude
	 * @param distanceA        Destination GeoArea's distance A
	 * @param distanceB        Destination GeoArea's distance B
	 * @param angle            Destination GeoArea's angle
	 * @return Encoded GeoBroadcast extension header
	 */
	private byte[] createGeoBroadcastHeader(long geoAreaLatitude, long geoAreaLongitude, int distanceA, int distanceB, int angle) {
		byte[] tsb = createTsbHeader();        
		byte[] lat = ByteHelper.longToByteArray(geoAreaLatitude & 0xffffffffL, 4);
		byte[] lng = ByteHelper.longToByteArray(geoAreaLongitude & 0xffffffffL, 4);
		byte[] distA = ByteHelper.intToByteArray(distanceA, 2);
		byte[] distB = ByteHelper.intToByteArray(distanceB, 2);
		byte[] ang = ByteHelper.intToByteArray(angle, 2);
		byte[] reserved = ByteHelper.intToByteArray(0, 2);
		return ByteHelper.concat(tsb, lat, lng, distA, distB, ang, reserved);
	}

	/**
	 * Builds LS Request extension header
	 * @param  gnAddress    Target GN_Address
	 * @return Encoded LS Request extension header
	 */
	private byte[] createLsRequestHeader(byte[] gnAddress) {
		byte[] tsb = createTsbHeader();
		return ByteHelper.concat(tsb, gnAddress);
	}

	/**
	 * Builds LS Reply extension header 
	 * @param  depv    Destination position vector (short position vector)
	 * @return Encoded LS Reply extension header
	 */
	private byte[] createLsReplyHeader(byte[] depv) {
		byte[] tsb = createTsbHeader();
		return ByteHelper.concat(tsb, depv);    
	}

	private byte[] createSecuredMessage(final byte[] basicHdr, final byte[] commonHdr, final byte[] extHdr, final byte[] message) {
		System.out.println(">>> GnLayer.createSecuredMessage: "+ ByteHelper.byteArrayToString(message));

		// SecuredMessage payload length
		int payloadLength = commonHdr.length + extHdr.length + message.length;

		// Build the generation time value
		long curtime = System.currentTimeMillis();
		byte[] generationTime = ByteHelper.longToByteArray((long)(curtime - 1072915200000L) * 1000L, Long.SIZE / Byte.SIZE); // In microseconds
		System.out.println("GnLayer.createSecuredMessage: generationTime=" + ByteHelper.byteArrayToString(generationTime));
		// Build the payload to be signed
		byte[] headersField = ByteHelper.concat(
				ByteHelper.concat(                                // SecuredMessage HeaderFields
						new byte[] {
								(byte)0x80,                               // signerInfo
								(byte)0x01                                //     Certificate digest with ecdsap256
						},
						management.getAtCertificateDigest(),          //         Hashed8
						new byte[] {
								(byte)0x00,                               // generationTime
						},
						generationTime                                //    Time64 value
						)
				);
		if ((commonHdr[0] & 0xF0) == 0x01) { // Next header = Btp-A
			headersField = ByteHelper.concat(
					headersField,
					new byte[] {
							(byte)0x05,                               // its-aid
							(byte)0x24                                //     36 = CAM
					}
					);
		} else if ((commonHdr[0] & 0xF0) == 0x02) { // Next header = Btp-B
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
							(byte)0x25                                //     37 = DENM
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
				commonHdr, 
				extHdr, 
				message,                                          // End of SecuredMessage Payloads
				new byte[] { (byte)0x01 },                        // Signature
				new byte[] { (byte)0x43 }                         // Signature length
				);
		System.out.println("GnLayer.createSecuredMessage: toBeSignedData=" + ByteHelper.byteArrayToString(toBeSignedData));

		byte[] toBeSent = null;
		try {
			// Calculate the hash
			byte[] hash = CryptoLib.hashWithSha256(toBeSignedData);
			System.out.println("GnLayer.createSecuredMessage: hash=" + ByteHelper.byteArrayToString(hash));
			// Signed the hash
			byte[] signatureBytes = CryptoLib.signWithEcdsaNistp256WithSha256(hash, management.getSigningPrivateKey());
			System.out.println("GnLayer.createSecuredMessage: signatureBytes=" + ByteHelper.byteArrayToString(signatureBytes));
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

		System.out.println("GnLayer.createSecuredMessage: toBeSent=" + ByteHelper.byteArrayToString(toBeSent));
		return toBeSent;
	}

	/**
	 * Indicates whether the layer is still active. Setting this field to false will cause
	 * the beaconning thread to stop its execution.
	 */
	private boolean running;

	/**
	 * Beaconning thread instance.
	 */
	private Thread beaconThread;

	/**
	 * Packet sequence number. Incremented after sending each unicast packet 
	 */
	private int sequenceNumber;
}

