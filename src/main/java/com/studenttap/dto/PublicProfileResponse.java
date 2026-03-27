package com.studenttap.dto;
 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicProfileResponse {
 
    // Basic
    private String username;
    private String fullName;
    private String designation;
    private String bio;
    private String profilePhoto;     // URL to access photo
    private String coverPhoto;       // URL to access cover
 
    // Contact - shown as clickable buttons
    private String phone;
    private String alternatePhone;
    private String emailPublic;
    private String address;
    private String googleMapsLink;   // opens Google Maps when clicked
 
    // Social
    private String linkedinUrl;
    private String githubUrl;
    private String websiteUrl;
 
    // Type
    private Boolean isFresher;
 
    // Personal Details
    private String dateOfBirth;
    private String gender;
    private String nationality;
    private String languagesKnown;
    private String hobbies;
    private String skills;
    private String totalExperience;
    private String careerObjective;
}
 