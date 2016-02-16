/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/geodesic/org/etsi/geodesic/DMS.java $
 *              $Id: DMS.java 1641 2014-09-24 14:39:22Z garciay $
 */
package org.etsi.geodesic;

public class DMS {
    
    private int _degrees = Integer.MAX_VALUE;
    
    private int _minutes = Integer.MAX_VALUE;
    
    private double _seconds = Double.NaN;
    
    private char _latlon = (char)0x00;

    private double _wgs84 = Double.NaN;
    
    /**
     * @desc Constructor
     * @param p_degrees The degrees (D)
     * @param p_minutes The minutes (M)
     * @param p_seconds The seconds (S)
     * @param p_latlon  The latitude/longitude: (N|S|E|W)
     */
    public DMS(final int p_degrees, final int p_minutes, final double p_seconds, final char p_latlon) {
        setValue(p_degrees, p_minutes, p_seconds, p_latlon);
    } // End of DMS ctor
    
    /**
     * @desc Update location
     * @param p_degrees The degrees (D)
     * @param p_minutes The minutes (M)
     * @param p_seconds The seconds (S)
     * @param p_latlon  The latitude/longitude: (N|S|E|W)
     */
    public void setValue(final int p_degrees, final int p_minutes, final double p_seconds, final char p_latlon) {
        _degrees = p_degrees;
        _minutes = p_minutes;
        _seconds = p_seconds;
        _latlon = p_latlon;
        
        _wgs84 = 
            (double)p_degrees + 
            (double)p_minutes / 60 +
            (double)p_seconds / 3600;
        if ((p_latlon == 'W') || (p_latlon == 'S')) _wgs84 *= -1.0;
    }
    
    public double toDD() {
        return _wgs84;
    }
    
} // End of class DMS 
