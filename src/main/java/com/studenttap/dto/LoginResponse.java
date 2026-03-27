



package com.studenttap.dto;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
 
    private String token;           // JWT token
    private String tokenType = "Bearer";
    private String username;
    private String fullName;
    private String email;
    private Boolean isFresher;
    private String message;
 
    public LoginResponse(String token, String username,
                         String fullName, String email,
                         Boolean isFresher) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.isFresher = isFresher;
        this.message = "Login successful";
    }
}