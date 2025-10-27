package com.rezeptapp.data.api;

public interface UserManager {
    String loginUser(String email, String password);
    boolean logoffUser(String email);
    boolean registerUser(User username);
    
}