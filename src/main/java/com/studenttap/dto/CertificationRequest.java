

package com.studenttap.dto;
 
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
 
@Data
public class CertificationRequest {
 
    @NotBlank(message = "Course name is required")
    private String courseName;       // e.g. "Java SE 11 Developer"
 
    private String issuingOrg;       // e.g. "Oracle", "Udemy"
    private String issueDate;        // e.g. "March 2024"
    private String credentialUrl;    // link to certificate online
}