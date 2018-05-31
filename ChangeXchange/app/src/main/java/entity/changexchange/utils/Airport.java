package entity.changexchange.utils;

public enum Airport {

    LTN("London-Luton", "LTN"), LHR("London-Heathrow", "LHR"), LGW("London-Gatwick", "LGW"),
    STD("London-Stanstead", "STD");

    private String fullName;
    private String shortcode;

    Airport(String fullName, String shortcode) {
        this.fullName = fullName;
        this.shortcode = shortcode;
    }

    @Override
    public String toString() {
        return getShortcode();
    }

    public String getShortcode() {return shortcode;}

    public String getFullName() {return fullName;}

}
