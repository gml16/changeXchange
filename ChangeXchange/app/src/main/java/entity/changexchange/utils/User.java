package entity.changexchange.utils;

import java.io.Serializable;

public class User implements Serializable{

    private String name;
    private String nickname;
    private Currency preferredCurrency;
    private String preferredContactDetails;


    public User(String name, String nickname, Currency preferredCurrency, String preferredContactDetails) {
        this.name = name;
        this.nickname = nickname;
        this.preferredCurrency = preferredCurrency;
        this.preferredContactDetails = preferredContactDetails;
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

    public Currency getPreferredCurrency() {
        return preferredCurrency;
    }

    public String getPreferredContactDetails() {
        return preferredContactDetails;
    }

}