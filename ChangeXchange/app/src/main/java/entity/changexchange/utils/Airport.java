package entity.changexchange.utils;

import android.location.Location;

public enum Airport {

    STN("London-Stanstead", "STN", 51.8838888889, 0.2377777778),
    LTN("London-Luton", "LTN", 51.8769444444, -0.3705555556),
    LHR("London-Heathrow", "LHR", 51.47138888, -0.45277777),
    LGW("London-Gatwick", "LGW", 51.1522222222, -0.1825),
    //TODO: why doesnt it get the closest?
    //LCY("London-City", "LCY", 51.5052777778, 0.0552777778),
    DEFAULT("London-Heathrow", "LHR", 51.47138888, -0.45277777);

    // TODO: use https://en.wikipedia.org/wiki/List_of_airports_by_IATA_code:_A-Z to find ALL airports.
    // TODO: use https://www.travelmath.com/airport/ to find their lon / lat.

    private String fullName;
    private String shortcode;
    private double latitude;
    private double longitude;

    Airport(String fullName, String shortcode, double latitude, double longitude) {
        this.fullName = fullName;
        this.shortcode = shortcode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return getShortcode();
    }

    public String getShortcode() {
        return shortcode;
    }

    public String getFullName() {
        return fullName;
    }

    public Location getLocation() {
        Location location = new Location(fullName);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

}
