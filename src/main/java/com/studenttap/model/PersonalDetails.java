



package com.studenttap.model;
 
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
 
@Entity
@Table(name = "personal_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDetails {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;
 
    @Column(name = "date_of_birth")
    private String dateOfBirth;
 
    @Column(length = 20)
    private String gender;
 
    @Column(length = 100)
    private String nationality = "Indian";
 
    @Column(name = "languages_known", length = 300)
    private String languagesKnown;
 
    @Column(columnDefinition = "TEXT")
    private String hobbies;
 
    @Column(columnDefinition = "TEXT")
    private String skills;
 
    @Column(name = "total_experience", length = 50)
    private String totalExperience;
    
    @Column(name = "career_objective", columnDefinition = "TEXT")
    private String careerObjective;
}
    
    
    
    
    