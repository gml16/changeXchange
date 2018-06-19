package entity.changexchange.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Offer implements Serializable {

    private final String poster_nickname;
    private final Currency buying;
    private final Currency selling;
    private final float amount;
    private final Airport airport;
    private final String note;
    private final String interests;


    public Offer(String poster_nickname, Currency buying, Currency selling, float amount, Airport airport, String note, String interests) {
        this.poster_nickname = poster_nickname;
        this.buying = buying;
        this.selling = selling;
        this.amount = amount;
        this.airport = airport;
        this.note = note;
        this.interests = interests;
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

    public float getAmount() {
        return amount;
    }

    public Airport getLocation() {
        return airport;
    }

    public String getNote() {
        return note;
    }

    public List<String> getInterests() {
        return Arrays.asList(interests.split(","));
    }
}
