


package com.studenttap.dto;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
 
    private boolean success;
    private String message;
    private Object data;        // any extra data to send back
 
    // Success with data
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }
 
    // Success without data
    public static ApiResponse success(String message) {
        return new ApiResponse(true, message, null);
    }
 
    // Failure
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }
}