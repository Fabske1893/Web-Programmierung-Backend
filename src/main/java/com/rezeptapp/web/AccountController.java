package com.rezeptapp.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "*") 
public class AccountController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public Map<String, Object> getAccount(@RequestParam String token) {
        String sql = "SELECT username, email FROM users WHERE token = ?";
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(sql, token);
            result.put("username", user.get("username"));
            result.put("email", user.get("email"));
        } catch (Exception e) {
            result.put("error", "User not found");
        }
        return result;
    }
}
