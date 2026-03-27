

package com.studenttap.model;
 
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
 
@Entity
@Table(name = "certifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certification {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
 
    @Column(name = "course_name", nullable = false)
    private String courseName;
 
    @Column(name = "issuing_org")
    private String issuingOrg;
 
    @Column(name = "issue_date", length = 20)
    private String issueDate;
 
    @Column(name = "credential_url", length = 500)
    private String credentialUrl;
 
    @Column(name = "certificate_img")
    private String certificateImg;
}