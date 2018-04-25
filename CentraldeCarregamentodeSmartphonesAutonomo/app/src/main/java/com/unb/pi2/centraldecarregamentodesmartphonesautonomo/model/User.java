package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model;

public class User {

    private String name;
    private String email;
    private int cpf;
    private int password;

    public User(String name, String email, int cpf, int password){
        setName(name);
        setEmail(email);
        setCpf(cpf);
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

    public int getCpf() {
        return cpf;
    }

    public void setCpf(int cpf) {
        this.cpf = cpf;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

}
