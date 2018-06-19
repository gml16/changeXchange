package entity.changexchange.utils;

import java.io.Serializable;

public class User implements Serializable {

    private String nickname;
    private Currency currency;
    private String contact;
    private double rating;
    private int num_ratings;
    private String login;
    private String token;

    public User(String nickname, Currency currency, String contact, double rating, int num_ratings, String login, String token) {
        this.nickname = nickname;
        this.currency = currency;
        this.contact = contact;
        this.rating = rating;
        this.num_ratings = num_ratings;
        this.login = login;
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getContact() {
        return contact;
    }

    public double getRating() {
        return rating;
    }

    public int getNumRating() {
        return num_ratings;
    }

    public String getLogin() {
        return login;
    }

    public String getToken() {
        return token;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeCurrency(Currency newCurrency) {
        currency = newCurrency;
    }

    public void changeContact(String newContact) {
        contact = newContact;
    }

    public void addRating(double new_rating) {
        rating = ((rating * num_ratings) + new_rating) / (num_ratings + 1);
        num_ratings++;
    }
}