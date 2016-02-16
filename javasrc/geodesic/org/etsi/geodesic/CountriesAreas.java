/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/geodesic/org/etsi/geodesic/CountriesAreas.java $
 *              $Id: CountriesAreas.java 1789 2014-11-04 13:09:48Z garciay $
 */
package org.etsi.geodesic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


public class CountriesAreas implements ICountriesAreas {
    
    /**
     * Unique reference to this object
     */
    private static ICountriesAreas Instance;
    
    /**
     * The ISO-3166-1 region dictionary identifier
     * @see Draft ETSI TS 103 097 V1.1.14 Clause 4.2.25  IdentifiedRegion
     */
    private final int Iso_3166_1 = 0;
    
    /**
     * The United Nations region dictionary identifier
     * @see Draft ETSI TS 103 097 V1.1.14 Clause 4.2.25  IdentifiedRegion
     */
    private final int Un_stats = 1;
    
    /**
     * Reference to the Google JSon parser
     */
    private JsonParser _jsonParser = null;
    
    /**
     * Conversion from UN to ISO-3166 numerical code
     * @see http://unstats.un.org/unsd/methods/m49/m49alphaf.htm
     */
    private Map<Integer, String> _un2iso3611;
    
    /**
     * Conversion from ISO-3166 numerical code to alpha-2 code
     * @see http://fr.wikipedia.org/wiki/ISO_3166-1
     */
    private Map<Integer, String> _in2str4iso3611;
    
    /**
     * Polygon areas for each country referenced by ISO 3166
     */
    private Map<String, ArrayList<ArrayList<WGS84>>> _countriesPolygons;
    
    /**
     * Singleton pattern implementation
     */
    public static ICountriesAreas getInstance() {
        if (Instance == null) {
            Instance = new CountriesAreas();
            Instance.initialise();
        }
        
        return Instance;
    }
    
    private CountriesAreas() {
        super();
        
        _jsonParser = new JsonParser();
        
        _countriesPolygons = new HashMap<String, ArrayList<ArrayList<WGS84>>>();
        
        _in2str4iso3611 = new HashMap<Integer, String>(); 
        _in2str4iso3611.put(new Integer(4), "AF");
        _in2str4iso3611.put(new Integer(8), "AL");
        _in2str4iso3611.put(new Integer(10), "AQ");
        _in2str4iso3611.put(new Integer(12), "DZ");
        _in2str4iso3611.put(new Integer(16), "AS");
        _in2str4iso3611.put(new Integer(20), "AD");
        _in2str4iso3611.put(new Integer(24), "AO");
        _in2str4iso3611.put(new Integer(28), "AG");
        _in2str4iso3611.put(new Integer(31), "AZ");
        _in2str4iso3611.put(new Integer(32), "AR");
        _in2str4iso3611.put(new Integer(36), "AU");
        _in2str4iso3611.put(new Integer(40), "AT");
        _in2str4iso3611.put(new Integer(44), "BS");
        _in2str4iso3611.put(new Integer(48), "BH");
        _in2str4iso3611.put(new Integer(50), "BD");
        _in2str4iso3611.put(new Integer(51), "AM");
        _in2str4iso3611.put(new Integer(52), "BB");
        _in2str4iso3611.put(new Integer(56), "BE");
        _in2str4iso3611.put(new Integer(60), "BM");
        _in2str4iso3611.put(new Integer(64), "BT");
        _in2str4iso3611.put(new Integer(68), "BO");
        _in2str4iso3611.put(new Integer(70), "BA");
        _in2str4iso3611.put(new Integer(72), "BA");
        _in2str4iso3611.put(new Integer(74), "BV");
        _in2str4iso3611.put(new Integer(76), "BR");
        _in2str4iso3611.put(new Integer(84), "BZ");
        _in2str4iso3611.put(new Integer(86), "IO"); 
        _in2str4iso3611.put(new Integer(90), "SB");
        _in2str4iso3611.put(new Integer(92), "VG");
        _in2str4iso3611.put(new Integer(96), "BN");
        _in2str4iso3611.put(new Integer(100), "BG");
        _in2str4iso3611.put(new Integer(104), "MM");
        _in2str4iso3611.put(new Integer(108), "BI");
        _in2str4iso3611.put(new Integer(112), "BY");
        _in2str4iso3611.put(new Integer(116), "KH");
        _in2str4iso3611.put(new Integer(120), "CM");
        _in2str4iso3611.put(new Integer(124), "CA");
        _in2str4iso3611.put(new Integer(132), "CV");
        _in2str4iso3611.put(new Integer(136), "CY");
        _in2str4iso3611.put(new Integer(140), "CF");
        _in2str4iso3611.put(new Integer(144), "LK");
        _in2str4iso3611.put(new Integer(148), "TD");
        _in2str4iso3611.put(new Integer(152), "CL");
        _in2str4iso3611.put(new Integer(156), "CN");
        _in2str4iso3611.put(new Integer(158), "TW");
        _in2str4iso3611.put(new Integer(162), "CX");
        _in2str4iso3611.put(new Integer(166), "CC");
        _in2str4iso3611.put(new Integer(170), "CO");
        _in2str4iso3611.put(new Integer(174), "KM");
        _in2str4iso3611.put(new Integer(175), "YT");
        _in2str4iso3611.put(new Integer(178), "CG");
        _in2str4iso3611.put(new Integer(180), "CD");
        _in2str4iso3611.put(new Integer(184), "CK");
        _in2str4iso3611.put(new Integer(188), "CR");
        _in2str4iso3611.put(new Integer(191), "HR");
        _in2str4iso3611.put(new Integer(192), "CU");
        _in2str4iso3611.put(new Integer(196), "CY");
        _in2str4iso3611.put(new Integer(203), "CZ");
        _in2str4iso3611.put(new Integer(204), "BJ");
        _in2str4iso3611.put(new Integer(208), "DK");
        _in2str4iso3611.put(new Integer(212), "DM");
        _in2str4iso3611.put(new Integer(214), "DO");
        _in2str4iso3611.put(new Integer(218), "EC");
        _in2str4iso3611.put(new Integer(222), "SV");
        _in2str4iso3611.put(new Integer(226), "GQ");
        _in2str4iso3611.put(new Integer(231), "ET");
        _in2str4iso3611.put(new Integer(232), "ER");
        _in2str4iso3611.put(new Integer(233), "EE");
        _in2str4iso3611.put(new Integer(234), "FO");
        _in2str4iso3611.put(new Integer(238), "FK");
        _in2str4iso3611.put(new Integer(239), "GS");
        _in2str4iso3611.put(new Integer(242), "FJ");
        _in2str4iso3611.put(new Integer(246), "FI");
        _in2str4iso3611.put(new Integer(248), "AX");
        _in2str4iso3611.put(new Integer(250), "FR");
        _in2str4iso3611.put(new Integer(254), "GF");
        _in2str4iso3611.put(new Integer(258), "PF"); 
        _in2str4iso3611.put(new Integer(260), "TF");
        _in2str4iso3611.put(new Integer(262), "DJ");
        _in2str4iso3611.put(new Integer(266), "GA");
        _in2str4iso3611.put(new Integer(268), "GE");
        _in2str4iso3611.put(new Integer(270), "GM");
        _in2str4iso3611.put(new Integer(275), "PS");
        _in2str4iso3611.put(new Integer(276), "DE");
        _in2str4iso3611.put(new Integer(288), "GH");
        _in2str4iso3611.put(new Integer(292), "GI"); 
        _in2str4iso3611.put(new Integer(296), "KI");
        _in2str4iso3611.put(new Integer(300), "GR");
        _in2str4iso3611.put(new Integer(304), "GL");
        _in2str4iso3611.put(new Integer(308), "GD");
        _in2str4iso3611.put(new Integer(312), "GP"); 
        _in2str4iso3611.put(new Integer(316), "GU");
        _in2str4iso3611.put(new Integer(320), "GT");
        _in2str4iso3611.put(new Integer(324), "GI");
        _in2str4iso3611.put(new Integer(328), "GY");
        _in2str4iso3611.put(new Integer(332), "HT");
        _in2str4iso3611.put(new Integer(334), "HM");
        _in2str4iso3611.put(new Integer(336), "VA");
        _in2str4iso3611.put(new Integer(340), "HN");
        _in2str4iso3611.put(new Integer(344), "HK");
        _in2str4iso3611.put(new Integer(348), "HU");
        _in2str4iso3611.put(new Integer(352), "IS");
        _in2str4iso3611.put(new Integer(356), "IN");
        _in2str4iso3611.put(new Integer(360), "ID");
        _in2str4iso3611.put(new Integer(364), "IR");
        _in2str4iso3611.put(new Integer(368), "IR");
        _in2str4iso3611.put(new Integer(372), "IE");
        _in2str4iso3611.put(new Integer(376), "IL");
        _in2str4iso3611.put(new Integer(380), "IT");
        _in2str4iso3611.put(new Integer(384), "CI");
        _in2str4iso3611.put(new Integer(388), "JM");
        _in2str4iso3611.put(new Integer(392), "JP");
        _in2str4iso3611.put(new Integer(398), "KZ");
        _in2str4iso3611.put(new Integer(400), "JO");
        _in2str4iso3611.put(new Integer(404), "KE");
        _in2str4iso3611.put(new Integer(408), "KP");
        _in2str4iso3611.put(new Integer(410), "KR");
        _in2str4iso3611.put(new Integer(414), "KW");
        _in2str4iso3611.put(new Integer(417), "KG");
        _in2str4iso3611.put(new Integer(418), "LA");
        _in2str4iso3611.put(new Integer(422), "LB");
        _in2str4iso3611.put(new Integer(426), "LS");
        _in2str4iso3611.put(new Integer(428), "LV");
        _in2str4iso3611.put(new Integer(430), "LR");
        _in2str4iso3611.put(new Integer(434), "LY");
        _in2str4iso3611.put(new Integer(438), "LI");
        _in2str4iso3611.put(new Integer(440), "LT");
        _in2str4iso3611.put(new Integer(442), "LU");
        _in2str4iso3611.put(new Integer(446), "MO");
        _in2str4iso3611.put(new Integer(450), "MG");
        _in2str4iso3611.put(new Integer(454), "MW");
        _in2str4iso3611.put(new Integer(458), "MY");
        _in2str4iso3611.put(new Integer(462), "MV");
        _in2str4iso3611.put(new Integer(466), "ML");
        _in2str4iso3611.put(new Integer(470), "MT");
        _in2str4iso3611.put(new Integer(474), "MQ"); 
        _in2str4iso3611.put(new Integer(478), "MR");
        _in2str4iso3611.put(new Integer(480), "MU"); 
        _in2str4iso3611.put(new Integer(484), "MX");
        _in2str4iso3611.put(new Integer(492), "MC"); 
        _in2str4iso3611.put(new Integer(496), "MN");
        _in2str4iso3611.put(new Integer(498), "MD");
        _in2str4iso3611.put(new Integer(499), "ME");
        _in2str4iso3611.put(new Integer(500), "MS");
        _in2str4iso3611.put(new Integer(504), "MA");
        _in2str4iso3611.put(new Integer(508), "MZ");
        _in2str4iso3611.put(new Integer(512), "OM");
        _in2str4iso3611.put(new Integer(516), "NA");
        _in2str4iso3611.put(new Integer(520), "NR");
        _in2str4iso3611.put(new Integer(524), "NP");
        _in2str4iso3611.put(new Integer(528), "NL");
        _in2str4iso3611.put(new Integer(531), "CW");
        _in2str4iso3611.put(new Integer(533), "AW");
        _in2str4iso3611.put(new Integer(534), "SX");  
        _in2str4iso3611.put(new Integer(535), "BQ");
        _in2str4iso3611.put(new Integer(540), "NC"); 
        _in2str4iso3611.put(new Integer(548), "VU");
        _in2str4iso3611.put(new Integer(554), "NZ");
        _in2str4iso3611.put(new Integer(558), "NI");
        _in2str4iso3611.put(new Integer(562), "NE"); 
        _in2str4iso3611.put(new Integer(566), "NG"); 
        _in2str4iso3611.put(new Integer(570), "NU");
        _in2str4iso3611.put(new Integer(574), "NF");
        _in2str4iso3611.put(new Integer(578), "NO");
        _in2str4iso3611.put(new Integer(580), "MP");
        _in2str4iso3611.put(new Integer(581), "UM");
        _in2str4iso3611.put(new Integer(583), "FM");
        _in2str4iso3611.put(new Integer(584), "MH");
        _in2str4iso3611.put(new Integer(585), "PW");
        _in2str4iso3611.put(new Integer(586), "PK");
        _in2str4iso3611.put(new Integer(591), "PA");
        _in2str4iso3611.put(new Integer(598), "PG");
        _in2str4iso3611.put(new Integer(600), "PY");
        _in2str4iso3611.put(new Integer(604), "PE");
        _in2str4iso3611.put(new Integer(608), "PH");
        _in2str4iso3611.put(new Integer(612), "PN");
        _in2str4iso3611.put(new Integer(616), "PL");
        _in2str4iso3611.put(new Integer(620), "PT");
        _in2str4iso3611.put(new Integer(624), "GW");
        _in2str4iso3611.put(new Integer(626), "TL");
        _in2str4iso3611.put(new Integer(630), "PR");
        _in2str4iso3611.put(new Integer(634), "QA");
        _in2str4iso3611.put(new Integer(638), "RE");
        _in2str4iso3611.put(new Integer(642), "RO");
        _in2str4iso3611.put(new Integer(643), "RU");
        _in2str4iso3611.put(new Integer(646), "RW");
        _in2str4iso3611.put(new Integer(652), "BL"); 
        _in2str4iso3611.put(new Integer(654), "SH");
        _in2str4iso3611.put(new Integer(659), "KN"); 
        _in2str4iso3611.put(new Integer(660), "AI");
        _in2str4iso3611.put(new Integer(662), "LC");
        _in2str4iso3611.put(new Integer(663), "MF"); 
        _in2str4iso3611.put(new Integer(666), "PM"); 
        _in2str4iso3611.put(new Integer(670), "VC");
        _in2str4iso3611.put(new Integer(674), "SM");
        _in2str4iso3611.put(new Integer(678), "ST");
        _in2str4iso3611.put(new Integer(682), "SA");
        _in2str4iso3611.put(new Integer(686), "SN");
        _in2str4iso3611.put(new Integer(688), "RS");
        _in2str4iso3611.put(new Integer(690), "SC");
        _in2str4iso3611.put(new Integer(694), "SL");
        _in2str4iso3611.put(new Integer(702), "SG");
        _in2str4iso3611.put(new Integer(703), "SK");
        _in2str4iso3611.put(new Integer(704), "VN");
        _in2str4iso3611.put(new Integer(705), "SI");
        _in2str4iso3611.put(new Integer(706), "SO");
        _in2str4iso3611.put(new Integer(710), "ZA");
        _in2str4iso3611.put(new Integer(716), "ZW");
        _in2str4iso3611.put(new Integer(724), "ES");
        _in2str4iso3611.put(new Integer(728), "SS");
        _in2str4iso3611.put(new Integer(729), "SD");
        _in2str4iso3611.put(new Integer(732), "EH");
        _in2str4iso3611.put(new Integer(740), "SR");
        _in2str4iso3611.put(new Integer(744), "SJ");
        _in2str4iso3611.put(new Integer(748), "SZ");
        _in2str4iso3611.put(new Integer(752), "SE");
        _in2str4iso3611.put(new Integer(756), "CH");
        _in2str4iso3611.put(new Integer(760), "SY");
        _in2str4iso3611.put(new Integer(762), "TJ");
        _in2str4iso3611.put(new Integer(764), "TH");
        _in2str4iso3611.put(new Integer(768), "TG");
        _in2str4iso3611.put(new Integer(772), "TK");
        _in2str4iso3611.put(new Integer(776), "TO");
        _in2str4iso3611.put(new Integer(780), "TT");
        _in2str4iso3611.put(new Integer(784), "AE");
        _in2str4iso3611.put(new Integer(788), "TN");
        _in2str4iso3611.put(new Integer(792), "TR");
        _in2str4iso3611.put(new Integer(795), "TM");
        _in2str4iso3611.put(new Integer(796), "TC");
        _in2str4iso3611.put(new Integer(798), "TV");
        _in2str4iso3611.put(new Integer(800), "UG");
        _in2str4iso3611.put(new Integer(804), "UA");
        _in2str4iso3611.put(new Integer(807), "MK");
        _in2str4iso3611.put(new Integer(818), "EG");
        _in2str4iso3611.put(new Integer(826), "GB");
        _in2str4iso3611.put(new Integer(831), "GG");
        _in2str4iso3611.put(new Integer(832), "JE");
        _in2str4iso3611.put(new Integer(833), "IM");
        _in2str4iso3611.put(new Integer(834), "TZ");
        _in2str4iso3611.put(new Integer(840), "US");
        _in2str4iso3611.put(new Integer(850), "VI");
        _in2str4iso3611.put(new Integer(854), "BF");
        _in2str4iso3611.put(new Integer(858), "UY");
        _in2str4iso3611.put(new Integer(860), "UZ");
        _in2str4iso3611.put(new Integer(862), "VE");
        _in2str4iso3611.put(new Integer(876), "WF");
        _in2str4iso3611.put(new Integer(882), "WS");
        _in2str4iso3611.put(new Integer(887), "YE");
        _in2str4iso3611.put(new Integer(894), "ZM");
        
        _un2iso3611 = _in2str4iso3611; // Until today, both tables are identical
        
    } // End of constructor
    
    @Override
    public boolean initialise() {
        try {
            // Load JSon countries areas and parse it
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            Map<String, String> systemProperties = runtimeBean.getSystemProperties();
            String fileName = systemProperties.get("user.dir") + systemProperties.get("file.separator") + "ne-countries-50m.json";
            BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
            StringBuilder str = new StringBuilder();
            String lineSeparator = System.getProperty("line.separator");
            String line;
            while ((line = in.readLine()) != null ) {
                str.append(line);
                str.append(lineSeparator);
            } // End of 'while' statement
            try {
                parseLine(str.toString());
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            in.close();
            
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * 
     * @param p_stringToParse The JSon file to parse
     * @see http://geojson.org/geojson-spec.html
     * @see http://ckan.it.ox.ac.uk/fr/dataset/geojson-by-country-code
     */
    private void parseLine(final String p_stringToParse) {
        JsonElement jelement = _jsonParser.parse(p_stringToParse);
        if (jelement != null) {
            JsonObject jobj = jelement.getAsJsonObject();
            if (jobj != null) {
                JsonArray jarray = jobj.getAsJsonArray("features");
                for (int i = 0; i < jarray.size(); i++) {
                    JsonObject properties = jarray.get(i).getAsJsonObject().getAsJsonObject("properties");
                    String regionId = properties.getAsJsonPrimitive("iso_a2").getAsString();
                    JsonObject geometry = jarray.get(i).getAsJsonObject().getAsJsonObject("geometry");
                    String type = geometry.getAsJsonPrimitive("type").getAsString();
                    _countriesPolygons.put(regionId, new ArrayList<ArrayList<WGS84>>());
                    if (type.equalsIgnoreCase("Polygon") ) {
                        ArrayList<WGS84> points = new ArrayList<WGS84>();
                        JsonArray coordinates = geometry.getAsJsonArray("coordinates").get(0).getAsJsonArray();
                        for (int j = 0; j < coordinates.size(); j++) {
                            points.add(
                                new WGS84(
                                    coordinates.get(j).getAsJsonArray().get(1).getAsDouble(), 
                                    coordinates.get(j).getAsJsonArray().get(0).getAsDouble(),
                                    0
                                )
                            );
                        } // End of 'for' statement
                        _countriesPolygons.get(regionId).add(points);
                    } else if (geometry.getAsJsonPrimitive("type").getAsString().equalsIgnoreCase("MultiPolygon")) {
                        ArrayList<WGS84> points = new ArrayList<WGS84>();
                        JsonArray coordinates = geometry.getAsJsonArray("coordinates");
                        for (int j = 0; j < coordinates.size(); j++) {
                            JsonArray subCoordinates = coordinates.get(j).getAsJsonArray();
                            for (int k = 0; k < subCoordinates.size(); k++) {
                                JsonArray subSubCoordinates = subCoordinates.get(k).getAsJsonArray();
                                for (int l = 0; l < subSubCoordinates.size(); l++) {
                                    points.add(
                                        new WGS84(
                                            subSubCoordinates.get(l).getAsJsonArray().get(1).getAsDouble(), 
                                            subSubCoordinates.get(l).getAsJsonArray().get(0).getAsDouble(),
                                            0
                                        )
                                    );
                                } // End of 'for' statement
                            } // End of 'for' statement
                            _countriesPolygons.get(regionId).add(points);
                        } // End of 'for' statement
                    } else if (geometry.getAsJsonPrimitive("type").getAsString().equalsIgnoreCase("LineString")) {
                        // TODO To be continued
                    } else if (geometry.getAsJsonPrimitive("type").getAsString().equalsIgnoreCase("MultiLineString")) {
                        // TODO To be continued
                    }
                } // End of 'for' statement
            }
        }
    }
    
    @Override
    public boolean isLocationInsideIdentifiedRegion(final int p_regionDictionary, final int p_regionId, final long p_localRegion, final WGS84 p_location) {
//        System.out.println(">>> CountriesAreas.isLocationInsideIdentifiedRegion: " + p_regionDictionary + ", " + p_regionId + ", " + p_localRegion + ", " + p_location);
        
        boolean result = false;
        if (p_regionDictionary == Iso_3166_1) {
            if (_in2str4iso3611.containsKey(p_regionId)) {
                result = process(_in2str4iso3611.get(p_regionId), p_localRegion, p_location);
            } // else, error
        } else if (p_regionDictionary == Un_stats) {
            if (_un2iso3611.containsKey(p_regionId)) {
                result = process(_in2str4iso3611.get(p_regionId), p_localRegion, p_location);
            } // else, error
        } // else, error
        
        return result;
    }
    
    public boolean process(final String p_regionId, final long p_localRegion, final WGS84 p_location) {
//        System.out.println(">>> CountriesAreas.process: " + p_regionId + ", " + p_localRegion + ", " + p_location);
        
        if (_countriesPolygons.containsKey(p_regionId)) {
            ArrayList<ArrayList<WGS84>> areas = _countriesPolygons.get(p_regionId);
            return Positioning.getInstance().isLocationInsidePolygonalAreas(p_location, areas);
        }
        
        return false;
    }
    
} // End of class CountriesAreas
