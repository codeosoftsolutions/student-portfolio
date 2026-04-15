



package com.studenttap.model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "selected_template")
    private String selectedTemplate;
    

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", length = 150)
    private String fullName;
    
    
    
    

    @Column(length = 150)
    private String designation;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "cover_photo")
    private String coverPhoto;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 20)
    private String phone;

    @Column(name = "alternate_phone", length = 20)
    private String alternatePhone;

    @Column(name = "email_public", length = 150)
    private String emailPublic;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "google_maps_link", length = 500)
    private String googleMapsLink;

    @Column(name = "linkedin_url", length = 300)
    private String linkedinUrl;

    @Column(name = "github_url", length = 300)
    private String githubUrl;
    
    

    @Column(name = "website_url", length = 300)
    private String websiteUrl;

    @Column(name = "is_fresher")
    private Boolean isFresher = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}