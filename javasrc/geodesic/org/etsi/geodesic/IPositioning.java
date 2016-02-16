/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/geodesic/org/etsi/geodesic/IPositioning.java $
 *              $Id: IPositioning.java 1641 2014-09-24 14:39:22Z garciay $
 */
package org.etsi.geodesic;

import java.util.ArrayList;

public interface IPositioning {
    
    /**
     * @desc Check that the location is inside an elliptic area
     * @param p_location        The device location
     * @param p_ellipticArea    The circular area to consider
     * @return true on success, false otherwise
     */
    public boolean isLocationInsideEllipticArea(final WGS84 p_location, final ArrayList<WGS84> p_ellipticArea);
    
    /**
     * @desc Check that the location is inside a polygonal area
     * @param p_location        The device location
     * @param p_polygonalArea    The polygonal area to consider
     * @return true on success, false otherwise
     */
    public boolean isLocationInsidePolygonalArea(final WGS84 p_location, final ArrayList<WGS84> p_polygonalArea);
    
    /**
     * @desc Check that the location is inside a list of polygonal areas
     * @param p_location        The device location
     * @param p_polygonalAreas  The list of polygonal areas to consider
     * @return true on success, false otherwise
     */
    public boolean isLocationInsidePolygonalAreas(final WGS84 p_location, final ArrayList< ArrayList<WGS84> > p_polygonalAreas);
    
    /**
     * @desc Check that the location is inside a circular area
     * @param p_region    The circular area to consider
     * @param p_center    The circle center position
     * @param p_radius    The circle radius
     * @return true on success, false otherwise
     */
    public boolean isLocationInsideCircularArea(final WGS84 p_location, final WGS84 p_center, final int p_radius);
    
    /**
     * @desc Check if the polygonal area is valid (neither self-intersections nor holes)
     * @param p_polygonalArea      The polygonal area to consider
     * @return true on success, false otherwise
     */
    public boolean isValidPolygonArea(final ArrayList<WGS84> p_polygonalArea);
    
    /**
     * @desc Check if the location is inside an identified region
     * @param p_regionDictionary
     * @param p_regionId
     * @param p_localRegion
     * @param p_location
     * @return true on success, false otherwise
     */
    boolean isLocationInsideIdentifiedRegion(final int p_regionDictionary, final int p_regionId, final long p_localRegion, final WGS84 p_location);
    
    /**
     * @desc Check if a polygonal regin is inside another one
     * @param p_parent  The main polygonal region
     * @param p_region  The polygonal region to be included
     * @return true on success, false otherwise
     * @verdict Unchanged
     */
    public boolean isPolygonalRegionInside(final ArrayList<WGS84> p_parentArea, final ArrayList<WGS84> p_regionArea);
    
} // End of interface Positioning
