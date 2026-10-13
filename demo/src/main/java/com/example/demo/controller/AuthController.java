package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    // Hardcoded admin credentials for now (in production, use proper user management)
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin@123";

    @PostMapping("/admin-login")
    public ResponseEntity<Map<String, Object>> adminLogin(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();

        // Validate credentials
        if (!ADMIN_USERNAME.equals(loginRequest.getUsername()) || 
            !ADMIN_PASSWORD.equals(loginRequest.getPassword())) {
            response.put("success", false);
            response.put("message", "Invalid admin credentials");
            return ResponseEntity.status(401).body(response);
        }

        // Return success response
        response.put("success", true);
        response.put("message", "Admin login successful");
        response.put("username", ADMIN_USERNAME);
        response.put("role", "ADMIN");
        response.put("token", "admin-token-" + System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> userLogin(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();

        // For demo purposes, accept any email/password combination
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("email", loginRequest.getUsername());
        response.put("role", "USER");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> userSignup(@RequestBody SignupRequest signupRequest) {
        Map<String, Object> response = new HashMap<>();

        // For demo purposes, accept any signup
        response.put("success", true);
        response.put("message", "Signup successful");
        response.put("name", signupRequest.getName());
        response.put("email", signupRequest.getEmail());
        response.put("role", "USER");

        return ResponseEntity.ok(response);
    }

    // Request DTOs
    public static class LoginRequest {
        private String username;
        private String password;
        private String email;

        public String getUsername() {
            return username != null ? username : email;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class SignupRequest {
        private String name;
        private String email;
        private String password;

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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
