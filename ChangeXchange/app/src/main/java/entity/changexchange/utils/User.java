package entity.changexchange.utils;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String nickname;
    private Currency currency;
    private String contact;
    private double rating;

    public User(String name, String nickname, Currency currency, String contact, double rating) {
        this.name = name;
        this.nickname = nickname;
        this.currency = currency;
        this.contact = contact;
        this.rating = rating;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
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

    public void changeCurrency(Currency newCurrency) {
        currency = newCurrency;
    }

    public void changeContact(String newContact) {
        contact = newContact;
    }
}