package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model;

import java.util.Observer;

public class CreditCard {
    private String cardNumber;
    private String name;
    private String month;
    private String year;
    private String cvv;
    private int parcels;
    private String error;
    private String token;

    /*public CreditCard(Observer observer){
        addObserver( observer );
    }*/

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public int getParcels() {
        return parcels;
    }

    public void setParcels(int parcels) {
        this.parcels = parcels;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
