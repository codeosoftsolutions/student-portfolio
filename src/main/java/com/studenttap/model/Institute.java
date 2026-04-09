package com.studenttap.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "institutes")
@Data
public class Institute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owner reference
    @Column(nullable = false)
    private Long ownerId;

    // ===== BASIC INFO =====
    @Column(nullable = false)
    private String instituteName;

    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String googleMapsLink;
    private String website;

    // ===== DETAILS =====
    @Column(columnDefinition = "TEXT")
    private String description;

    // Type: COACHING, TRAINING, COLLEGE, OTHER
    private String instituteType;

    // ===== COURSES =====
    // Stored as JSON string
    @Column(columnDefinition = "TEXT")
    private String courses;

    // ===== FEES =====
    private String feeRange;

    // ===== BATCH INFO =====
    @Column(columnDefinition = "TEXT")
    private String batchTimings;

    // ===== FACILITIES =====
    @Column(columnDefinition = "TEXT")
    private String facilities;

    // ===== PHOTOS =====
    @Column(columnDefinition = "TEXT")
    private String photos;

    // ===== ACHIEVEMENTS =====
    @Column(columnDefinition = "TEXT")
    private String achievements;

    // ===== FACULTY =====
    @Column(columnDefinition = "TEXT")
    private String faculty;

    // ===== STATUS =====
    private Boolean isActive = true;
    private Integer availableSeats;

    // ===== TIMESTAMPS =====
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}