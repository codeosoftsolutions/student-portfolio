

package com.studenttap.model;
 
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
 
@Entity
@Table(name = "work_experience")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperience {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
 
    @Column(name = "company_name", nullable = false)
    private String companyName;
 
    @Column(name = "job_title", nullable = false)
    private String jobTitle;
 
    @Column(name = "job_location", length = 150)
    private String jobLocation;
 
    @Column(name = "start_date", length = 20)
    private String startDate;
 
    @Column(name = "end_date", length = 20)
    private String endDate;
    
    @Column(name = "is_current")
    private Boolean isCurrent = false;
 
    @Column(columnDefinition = "TEXT")
    private String description;
 
    @Column(name = "display_order")
    private Integer displayOrder=0;
    
    
    
    
}
    
    