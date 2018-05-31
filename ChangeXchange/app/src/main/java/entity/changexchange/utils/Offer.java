package entity.changexchange.utils;

public class Offer {

    private final String poster_nickname;
    private final Currency buying;
    private final Currency selling;
    private final long amount;


    public Offer(String poster_nickname, Currency buying, Currency selling, long amount) {
        this.poster_nickname = poster_nickname;
        this.buying = buying;
        this.selling = selling;
        this.amount = amount;
    }

    public String getPoster_nickname() {
        return poster_nickname;
    }

    public Currency getBuying() {
        return buying;
    }

    public Currency getSelling() {
        return selling;
    }

    public long getAmount() {
        return amount;
    }
}
