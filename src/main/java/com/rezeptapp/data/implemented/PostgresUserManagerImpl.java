package com.rezeptapp.data.implemented;

import com.rezeptapp.data.api.User;
import com.rezeptapp.data.api.UserManager;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}