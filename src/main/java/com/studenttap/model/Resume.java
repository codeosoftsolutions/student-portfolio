package com.studenttap.model;
 
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;
 
    @Column(name = "resume_path")
    private String resumePath;
 
    // Values: "TEMPLATE_1", "TEMPLATE_2", "TEMPLATE_3", "TEMPLATE_4", "CUSTOM"
    @Column(name = "template_used", length = 50)
    private String templateUsed;
 
    @Column(name = "is_downloadable")
    private Boolean isDownloadable = true;
 
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
 
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}