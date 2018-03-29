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

    private void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public int getCpf() {
        return cpf;
    }

    private void setCpf(int cpf) {
        this.cpf = cpf;
    }

    public int getPassword() {
        return password;
    }

    private void setPassword(int password) {
        this.password = password;
    }

}
