


package com.studenttap.dto;
 
import lombok.Data;
 
@Data
public class PersonalDetailsRequest {
 
    private String dateOfBirth;       // e.g. "15-08-2002"
    private String gender;            // "Male" / "Female" / "Other"
    private String nationality;       // default "Indian"
    private String languagesKnown;    // e.g. "Telugu, Hindi, English"
    private String hobbies;
    private String skills;            // e.g. "Java, Spring Boot, MySQL"
    private String totalExperience;   // for experienced: "2 years 3 months"
    private String careerObjective;
}
 
 

