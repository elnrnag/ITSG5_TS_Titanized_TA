package org.etsi.geodesic;

import java.util.ArrayList;

/**
 * @desc 
 * @see To validate GPS location: http://www.gps-coordinates.net
 */
public class Positioning implements IPositioning {
    
    public static double TWOPI = 2 * Math.PI;
    
    /**
     * Unique reference to this object
     */
    private static IPositioning Instance;
    
    /**
     * Singleton pattern implementation
     */
    public static IPositioning getInstance() {
        if (Instance == null) {
            Instance = new Positioning();
        }
        
        return Instance;
    }
    
    /**
     * Private constructor required for Singleton pattern implementation
     */
    private Positioning() {
        
    }
    
    @Override
    public boolean isLocationInsideEllipticArea(final WGS84 p_location, final ArrayList<WGS84> p_ellipticArea) {
//        System.out.println(">>> Positioning.isLocationInsideEllipticArea: " + p_location);
        
        // TODO: implement it
        // Do not forget aboout passing throw 0 meridian
        return false;
    }

    @Override
    public boolean isLocationInsidePolygonalArea(final WGS84 p_location, final ArrayList<WGS84> p_polygonalArea) {
//        System.out.println(">>> Positioning.isLocationInsidePolygonalArea: " + p_location + ", " + p_polygonalArea);
        
        // TODO: Check passing throw 0 meridian and Equador
        
        double angle = 0;
        int poleNum = p_polygonalArea.size();
        for (int index = 0; index < poleNum; index++) {
            WGS84 delta1 = new WGS84(
                p_polygonalArea.get(index).getLatitude() - p_location.getLatitude(),
                p_polygonalArea.get(index).getLongitude() - p_location.getLongitude(),
                0
            );
            WGS84 delta2 = new WGS84(
                p_polygonalArea.get((index + 1) % poleNum).getLatitude() - p_location.getLatitude(),
                p_polygonalArea.get((index + 1) % poleNum).getLongitude() - p_location.getLongitude(),
                0
            );
            angle += calcAngle(delta1, delta2);
        } // End of 'for' statement
        
//        System.out.println("<<< Positioning.isLocationInsidePolygonalArea: " + (boolean)((Math.abs(angle) < Math.PI) ? false : true));
        return (boolean)((Math.abs(angle) < Math.PI) ? false : true);
    }

    @Override
    public boolean isLocationInsidePolygonalAreas(final WGS84 p_location, final ArrayList< ArrayList<WGS84> > p_polygonalAreas) {
//        System.out.println(">>> Positioning.isLocationInsidePolygonalAreas: " + p_location);
        
        for (int index = 0; index < p_polygonalAreas.size(); index++) {
            if (isLocationInsidePolygonalArea(p_location, p_polygonalAreas.get(index))) {
                return true;
            }
        } // End of 'for' statement
        
        return false; // Not found
    }

    @Override
    public boolean isLocationInsideCircularArea(final WGS84 p_location, final WGS84 p_center, final int p_radius) {
//        System.out.println(">>> Positioning.isLocationInsideCircularArea: " + p_location + ", " + p_center + ", " + p_radius);
        
        // TODO: Check passing throw 0 meridian and Equador
        
        double theta = p_center.getLongitude() - p_location.getLongitude();
        double distance = 
            Math.sin(dd2rad(p_center.getLatitude())) * Math.sin(dd2rad(p_location.getLatitude())) + 
            Math.cos(dd2rad(p_center.getLatitude())) * Math.cos(dd2rad(p_location.getLatitude())) * Math.cos(dd2rad(theta));
        distance = rad2dd(Math.acos(distance));
        distance *= 60 * 1.1515 * 1.609344 /*Kilometers*/;
//        System.out.println("Positioning.isLocationInsideCircularArea: distance=" + distance);
        
//        System.out.println("<<< Positioning.isLocationInsideCircularArea: " + (boolean)(distance <= (p_radius / 1000.0)));
        return (boolean)(distance <= (p_radius / 1000.0));
    }
    
    @Override
    public boolean isValidPolygonArea(final ArrayList<WGS84> p_polygonalArea) {
//        System.out.println(">>> Positioning.isValidPolygonArea");
        
        // Check if polygon coordinates are valid
        for (int i = 0; i < p_polygonalArea.size(); i++) {
            if (!p_polygonalArea.get(i).isValidPosition()) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean isLocationInsideIdentifiedRegion(final int p_regionDictionary, final int p_regionId, final long p_localRegion, final WGS84 p_location) {
//        System.out.println(">>> Positioning.isLocationInsideIdentifiedRegion: " + p_regionDictionary + ", " + p_regionId + ", " + p_localRegion + ", " + p_location);
        
        return CountriesAreas.getInstance().isLocationInsideIdentifiedRegion(p_regionDictionary, p_regionId, p_localRegion, p_location);
    }
    
    @Override
    public boolean isPolygonalRegionInside(final ArrayList<WGS84> p_parentArea, final ArrayList<WGS84> p_regionArea) {
//        System.out.println(">>> Positioning.isPolygonalRegionInside: " + p_parentArea + ", " + p_regionArea);
        
        for (int i = 0; i < p_regionArea.size(); i++) {
            if (!isLocationInsidePolygonalArea(p_regionArea.get(i), p_parentArea)) {
                return false;
            }
        } // End of 'for' statement
        
        return true;
    }
    
    private double calcAngle(final WGS84 p_origin, final WGS84 p_end) {
//        System.out.println(">>> calcAngle: " + p_origin.getLatitude() + " - " + p_origin.getLongitude() + " - " + p_end.getLatitude() + " - " + p_end.getLongitude());
        double theta1 = Math.atan2(p_origin.getLatitude(), p_origin.getLongitude());
//        System.out.println("Angle2D: theta1=" + theta1);
        double theta2 = Math.atan2(p_end.getLatitude(), p_end.getLongitude());
//        System.out.println("Angle2D: theta2=" + theta2);
        double dtheta = theta2 - theta1;
//        System.out.println("calcAngle: dtheta" + dtheta);
        while (dtheta > Math.PI) {
            dtheta -= TWOPI;
        } // End of 'while' statement
        while (dtheta < -Math.PI) {
            dtheta += TWOPI;
        } // End of 'while' statement
        
//        System.out.println("<<< calcAngle: " + dtheta);
        return dtheta;
    }
    
    /**
     * @desc Convert a decimal degrees value into a decimal radian 
     * @param p_decimalDegrees The value to convert
     * @return The converted value in radians
     */
    private double dd2rad(final double p_decimalDegrees) {
        return p_decimalDegrees * Math.PI / 180;
    }
    
    /**
     * @desc Convert a decimal radians value into a decimal degrees
     * @param p_decimalRadians The value to convert
     * @return The converted value in degrees
     */
    private double rad2dd(final double p_decimalRadians) {
        return p_decimalRadians * 180.0 / Math.PI;
    }
    
} // End of class Positioning
