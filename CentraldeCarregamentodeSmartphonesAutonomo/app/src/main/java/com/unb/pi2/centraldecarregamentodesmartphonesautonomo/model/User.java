package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model;

public class User {

    private String name;
    private String email;
    private int password;

    public User(String name, String email, int password){
        setName(name);
        setEmail(email);
        setPassword(password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

}
