package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model;

public class User {

    private String name;
    private String email;
    private String cpf;
    private int password;
    private String cabin;
    private long chargeTime;

    public User(String name, String email, String cpf, int password){
        setName(name);
        setEmail(email);
        setCpf(cpf);
        setPassword(password);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    private void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getPassword() {
        return password;
    }

    private void setPassword(int password) {
        this.password = password;
    }

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public long getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(long chargeTime) {
        this.chargeTime = chargeTime;
    }

}
