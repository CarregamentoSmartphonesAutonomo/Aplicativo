package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model;

public class UserDAO  {

    private static UserDAO userDAOSIngleton;

    private User user;

    //Singleton pattern

    public static UserDAO getInstance(){
        if(userDAOSIngleton == null){
            userDAOSIngleton = new UserDAO();
        }
        return userDAOSIngleton;
    }

    //Getters and Setters

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }
}
