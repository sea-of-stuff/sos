package uk.ac.standrews.cs.sos.impl.context;

/**
 * http://maxmind.github.io/GeoIP2-java/
 *
 * TODO - move to its own project
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IPGeoUtils {

//    private  DatabaseReader reader;
//
//    public IPGeoUtils() throws IOException {
//        File database = new File("third-party/geolite2-city/GeoLite2-City_20170801/GeoLite2-City.mmdb");
//
//        // This creates the DatabaseReader object, which should be reused across lookups.
//        reader = new DatabaseReader.Builder(database).withCache(new CHMCache()).build();
//    }
//
//    public boolean nodeIsInCountry(Node node, String countryCode) throws IOException, GeoIp2Exception {
//
//        InetAddress address = node.getHostAddress().getAddress();
//        String codeFound = resolveIPToCountryCode(address);
//
//        return countryCode.equalsIgnoreCase(codeFound);
//    }
//
//    public String resolveIPToCountryCode(InetAddress ipAddress) throws IOException, GeoIp2Exception {
//
//        CityResponse response = reader.city(ipAddress);
//
//        Country country = response.getCountry();
//        return country.getIsoCode();
//    }
//
//    // Example code from MaxMind http://maxmind.github.io/GeoIP2-java/
//    public void resolveIP(InetAddress ipAddress) throws IOException, GeoIp2Exception {
//
//        CityResponse response = reader.city(ipAddress);
//
//        Country country = response.getCountry();
//        System.out.println(country.getIsoCode());            // 'US'
//        System.out.println(country.getName());               // 'United States'
//
//        Subdivision subdivision = response.getMostSpecificSubdivision();
//        System.out.println(subdivision.getName());    // 'Minnesota'
//        System.out.println(subdivision.getIsoCode()); // 'MN'
//
//        City city = response.getCity();
//        System.out.println(city.getName()); // 'Minneapolis'
//
//        Postal postal = response.getPostal();
//        System.out.println(postal.getCode()); // '55455'
//
//        Location location = response.getLocation();
//        System.out.println(location.getLatitude());  // 44.9733
//        System.out.println(location.getLongitude()); // -93.2323
//    }
}
