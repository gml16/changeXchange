package entity.changexchange.utils;

public enum ExpiryTime {

    H("1 hour"), H2("2 hours"), H3("3 hours"),
    D("1 day"), D2("2 days"), D3("3 days"),
    W("1 week"), W2("2 weeks"), W3("3 weeks"),
    M("1 month");

    private String time;

    ExpiryTime(String c) {
        time = c;
    }

    @Override
    public String toString() {
        return time;
    }
}
