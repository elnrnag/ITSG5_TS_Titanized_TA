/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/geodesic/org/etsi/geodesic/ICountriesAreas.java $
 *              $Id: ICountriesAreas.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.geodesic;

public interface ICountriesAreas {
    
    /**
     * @desc Initialise the object
     * @return true on success, false otherwise
     */
    public boolean initialise();
    
    /**
     * @desc Check if a location is inside a predefined geographic region determined by the region dictionary and the region identifier
     * @param p_regionDictionary    The region dictionary
     * @param p_regionId            The region identifier
     * @param p_localRegion         The whole region. 0 if the whole region is meant
     * @param p_location            The location to be checked
     * @return true on success, false otherwise
     * @see Draft ETSI TS 103 097 V1.1.14 Clause 4.2.25  IdentifiedRegion
     */
    public boolean isLocationInsideIdentifiedRegion(final int p_regionDictionary, final int p_regionId, final long p_localRegion, final WGS84 p_location);
    
} // End of interface ICountriesAreas 
