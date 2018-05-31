package entity.changexchange.utils;

public enum Airport {

    LTN("London Luton"), LHR("London Heathrow"), LGW("London Gatwick"),
    STD("London Stanstead");

    private String currency;

    Airport(String c) {
        currency = c;
    }

    @Override
    public String toString() {
        return currency;
    }

}
