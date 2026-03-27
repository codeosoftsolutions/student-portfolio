

package com.studenttap.dto;
 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
 
@Data
public class ContactFormRequest {
 
    @NotBlank(message = "Your name is required")
    private String senderName;
 
    @NotBlank(message = "Your email is required")
    @Email(message = "Please enter valid email")
    private String senderEmail;
 
    private String senderPhone;    // optional
    private String subject;        // optional
 
    @NotBlank(message = "Message cannot be empty")
    private String message;
}