/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/geodesic/org/etsi/geodesic/WGS84.java $
 *              $Id: WGS84.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.geodesic;

public class WGS84 {
    
    /** Longitude of the location */
    private double _longitude = Double.NaN;
    
    /** Latitude of the location */
    private double _latitude = Double.NaN;
    
    /** Altitude of the location */
    private double _altitude = Double.NaN;
    
    private int _longitudeD = Integer.MAX_VALUE;
    
    private int _longitudeM = Integer.MAX_VALUE;
    
    private double _longitudeS = Double.NaN;
    
    private int _latitudeD = Integer.MAX_VALUE;
    
    private int _latitudeM = Integer.MAX_VALUE;
    
    private double _latitudeS = Double.NaN;
    
    /** Heading of the location */
//    private double _heading = Double.NaN;
    
    /**
     * @desc Constructor
     * @param p_longitude   Longitude of the location
     * @param p_latitude    Latitude of the location
     * @param p_altitude    Altitude of the location
     */
    public WGS84(final double p_latitude, final double p_longitude, final double p_altitude) {
        setValue(p_latitude, p_longitude, p_altitude);
    } // End of WGS84 ctor
    
    /**
     * Constructor used to convert WGSLatitude/WGSLongitude value into decimal degrees value
     * @param p_latitude    Latiude integer value (significand length shall be 6 digits length)
     * @param p_longitude   Longitude integer value (significand length shall 6 digits length)
     */
    public WGS84(final long p_latitude, final long p_longitude) {
        setValue(
            (double)p_latitude / 10000000, 
            (double)p_longitude / 10000000, 
            0.0
        );
    } // End of WGS84 ctor

    /**
     * @desc Update location
     * @param p_longitude   Longitude of the location
     * @param p_latitude    Latitude of the location
     * @param p_altitude    Altitude of the location
     */
    public void setValue(final double p_latitude, final double p_longitude, final double p_altitude) {
        _latitude = p_latitude;
        _latitudeD = (int)p_latitude; // Truncate the decimals
        double dv = (p_latitude - _latitudeD) * 60;
        _latitudeM = (int)dv;
        _latitudeS = (dv - _latitudeM) * 60;
        
        _longitude = p_longitude;
        _longitudeD = (int)p_longitude; // Truncate the decimals
        dv = (p_longitude - _longitudeD) * 60;
        _longitudeM = (int)dv;
        _longitudeS = (dv - _longitudeM) * 60;
        
        _altitude = p_altitude;
    }
    
    public double getLongitude() {
        return _longitude;
    }
    
    public double getLatitude() {
        return _latitude;
    }
    
    public double getAltitude() {
        return _altitude;
    }
    
    public boolean isValidPosition() {
        if ((_latitude > -90) && (_latitude < 90) && (_longitude > -180) && (_longitude < 180)) {
            return true;
        }
        
        return false;
    }
    
    public String toDMS() {
       return String.format("WGS84: l=%s°$s'%s'', L=%s°$s'%s'', A=%s", 
           String.valueOf(_latitudeD), String.valueOf(_latitudeM), String.valueOf(_latitudeS),
           String.valueOf(_longitudeD), String.valueOf(_longitudeM), String.valueOf(_longitudeS),
           String.valueOf(_altitude)
       );
    }
    
    @Override
    public String toString() {
       return String.format("WGS84: lat=%s, lon=%s, alt=%s", String.valueOf(_latitude), String.valueOf(_longitude), String.valueOf(_altitude));
    }
    
} // End of class WGS84
