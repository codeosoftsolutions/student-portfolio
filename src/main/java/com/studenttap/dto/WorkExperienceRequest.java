

package com.studenttap.dto;
 
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
 
@Data
public class WorkExperienceRequest {
 
    @NotBlank(message = "Company name is required")
    private String companyName;
 
    @NotBlank(message = "Job title is required")
    private String jobTitle;
 
    private String jobLocation;      // e.g. "Hyderabad"
    private String startDate;        // e.g. "June 2022"
    private String endDate;          // e.g. "Present" or "Dec 2023"
    private Boolean isCurrent;       // true = currently working here
    private String description;      // responsibilities
    private Integer displayOrder;
}