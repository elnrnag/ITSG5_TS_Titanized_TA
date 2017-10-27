#include "LibItsCommon_Functions.hh"
#include "LibItsGeoNetworking_Functions.hh"
#include <time.h>
#include <math.h>

namespace LibItsGeoNetworking__Functions {

  /**
   * @desc    External function to compute timestamp based on current time
   * @return  Unix-Epoch-Time mod 2^32
   */
  INTEGER fx__computeGnTimestamp() {
    struct timeval tv;
    gettimeofday(&tv, NULL);

    long long timestampNow = tv.tv_sec*1000 + tv.tv_usec/1000;

    INTEGER i = INTEGER();
    i.set_long_long_val(timestampNow % 4294967296);
    return i;
  }

}

namespace LibItsCommon__Functions {

  /**
   * @desc    This external function gets the current time
   * @return  Timestamp - current time since 01/01/2014 in milliseconds
   */
  INTEGER fx__getCurrentTime() {
    unsigned long long timestamp2004 = 1072915200000 ;
    struct timeval tv;
    gettimeofday(&tv, NULL);

    unsigned long long timestampNow = tv.tv_sec*1000 + tv.tv_usec/1000;
    INTEGER i = INTEGER();
    i.set_long_long_val(timestampNow - timestamp2004);
    TTCN_Logger::begin_event(TTCN_DEBUG);
    i.log();
    TTCN_Logger::end_event();
    return i;
  }

        
  /**
   * @desc    External function to compute distance between two points
   * @param   p_latitudeA   Latitude of first point
   * @param   p_longitudeA  Longitude of first point
   * @param   p_latitudeB   Latitude of second point
   * @param   p_longitudeB  Longitude of second point
   * @return  Computed distance in meters
   */
  FLOAT fx__computeDistance(const INTEGER& p__latitudeA, const INTEGER& p__longitudeA, const INTEGER& p__latitudeB, const INTEGER& p__longitudeB) {

    double d_latA = ((int)p__latitudeA)/10000000.0;
    double d_latB = ((int)p__latitudeB)/10000000.0;

    double d_lonA = ((int)p__longitudeA)/10000000.0;
    double d_lonB = ((int)p__longitudeB)/10000000.0;

    double earth_radius = 6371000.0; //meters

    double d_lat = (d_latB  - d_latA) *M_PI/180.0;
    double d_lon = (d_lonB - d_lonA)*M_PI/180.0;
  
    double a = sin(d_lat/2)*sin(d_lat/2) + cos(d_latA*M_PI/180.0)*cos(d_latB*M_PI/180.0)*sin(d_lon/2)*sin(d_lon/2);
    double c = 2*atan2(sqrt(a), sqrt(1-a));

    return FLOAT(earth_radius*c);
  }
        
  /**
   * @desc    External function to compute a position using a reference position, a distance and an orientation 
   * @param   p_iutLongPosVector  Reference position
   * @param   p_distance          Distance to the reference position (in meter)
   * @param   p_orientation       Direction of the computed position (0 to 359; 0 means North)
   * @param   p_latitude          Computed position's latitude
   * @param   p_longitude         Computed position's longitude
   */        
  void fx__computePositionUsingDistance(const INTEGER& p__refLatitude, const INTEGER& p__refLongitude, const INTEGER& p__distance, const INTEGER& p__orientation, INTEGER& p__latitude, INTEGER& p__longitude) {
    double distance = p__distance/6371000.0;
    double angle = p__orientation*M_PI/180.0;

    double ref_lat = p__refLatitude*M_PI/180.0;
    double ref_lon = p__refLongitude*M_PI/180.0;

    p__latitude = asin(sin(ref_lat)*cos(distance) + cos(ref_lat)*sin(distance)*cos(angle))*180/M_PI;
    p__longitude = p__refLongitude + atan2(sin(angle)*sin(distance)*cos(ref_lat), cos(distance) - sin(ref_lat)*sin(p__latitude))*180/M_PI;
  }
        
  /**
   * @desc    External function to compute radius of a given circular area
   * @param   p_squareMeters  Square meters of an circular area
   * @return  Computed radius in meters
   */
  FLOAT fx__computeRadiusFromCircularArea(const FLOAT& p__squareMeters){
    return FLOAT(sqrt(p__squareMeters/M_PI));
  }
}
