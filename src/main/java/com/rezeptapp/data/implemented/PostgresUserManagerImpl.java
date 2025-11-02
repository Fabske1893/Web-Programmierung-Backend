package com.rezeptapp.data.implemented;

import com.rezeptapp.data.api.User;
import com.rezeptapp.data.api.UserManager;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

@Component
public class PostgresUserManagerImpl implements UserManager {

    private final DataSource dataSource;

    public PostgresUserManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean registerUser(User user) {
       
        String sql = "INSERT INTO users (username, email, password, token) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword()); 
            pstmt.setString(4, "OFF");

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token) {
    String email = null;
    String sql = "SELECT email FROM users WHERE token = ?";

    if (token == null || token.equals("OFF") || token.isEmpty()) {
        return null; 
    }

    try (Connection conn = dataSource.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, token);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                email = rs.getString("email");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return email; 
    }




    @Override
    public String loginUser(String email, String password) {
        
        String newToken = "TOKEN_" + System.currentTimeMillis();
        String sql = "UPDATE users SET token = ? WHERE email = ? AND password = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newToken);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            int affectedRows = pstmt.executeUpdate();
            return (affectedRows > 0) ? newToken : "OFF";
        } catch (SQLException e) {
            e.printStackTrace();
            return "OFF";
        }
    }

    @Override
    public boolean logoffUser(String email) {
       
        return true;
    }

    @Override
    public java.util.Map<String, Object> getUserByToken(String token) {
        String sql = "SELECT username, email FROM users WHERE token = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, token);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    java.util.Map<String, Object> user = new java.util.HashMap<>();
                    user.put("username", rs.getString("username"));
                    user.put("email", rs.getString("email"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}