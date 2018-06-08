package entity.changexchange.utils;

import java.io.Serializable;

public class User implements Serializable{

    private String name;
    private String nickname;
    private Currency preferedCurrency;
    private String preferedContactDetails;


    public User(String name, String nickname, Currency preferedCurrency, String preferedContactDetails) {
        this.name = name;
        this.nickname = nickname;
        this.preferedCurrency = preferedCurrency;
        this.preferedContactDetails = preferedContactDetails;
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

    public Currency getPreferedCurrency() {
        return preferedCurrency;
    }

    public String getPreferedContactDetails() {
        return preferedContactDetails;
    }

}