


package com.studenttap.dto;
 
import lombok.Data;
 
@Data
public class ProfileRequest {
 
    // Basic Info
    private String fullName;
    private String designation;      // e.g. "Java Developer | Fresher"
    private String bio;              // about me / description
 
    // Contact Info
    private String phone;
    private String alternatePhone;
    private String emailPublic;      // public email shown on portfolio
    private String address;
    private String googleMapsLink;   // paste Google Maps link here
 
    // Social Links
    private String linkedinUrl;
    private String githubUrl;
    private String websiteUrl;
 
    // Fresher or Experienced
    private Boolean isFresher;
}