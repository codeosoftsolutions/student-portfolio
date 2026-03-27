package com.studenttap.dto;
 
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
 
@Data
public class EducationRequest {
 
    // Values: "10TH", "INTERMEDIATE", "DEGREE", "PG", "DIPLOMA"
    @NotBlank(message = "Education type is required")
    private String educationType;
 
    @NotBlank(message = "Institution name is required")
    private String institutionName;
 
    private String boardUniversity;  // e.g. "CBSE", "Osmania University"
    private String fieldOfStudy;     // e.g. "Computer Science", "MPC"
    private String percentageCgpa;   // e.g. "85%" or "7.8 CGPA"
    private String yearOfPassing;    // e.g. "2022"
    private Integer displayOrder;    // controls order shown on page
}