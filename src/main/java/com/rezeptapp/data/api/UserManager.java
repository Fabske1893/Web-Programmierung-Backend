package com.rezeptapp.data.api;

import java.util.Map;

public interface UserManager {
    String loginUser(String email, String password);
    boolean logoffUser(String email);
    boolean registerUser(User username);
    String getEmailFromToken(String token);
    Map<String, Object> getUserByToken(String token);
    
}