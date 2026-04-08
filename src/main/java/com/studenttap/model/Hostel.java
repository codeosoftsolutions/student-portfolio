
package com.studenttap.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "hostels")
@Data
public class Hostel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owner reference
    @Column(nullable = false)
    private Long ownerId;

    // ===== BASIC INFO =====
    @Column(nullable = false)
    private String hostelName;

    private String ownerName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String googleMapsLink;

    // ===== DETAILS =====
    @Column(columnDefinition = "TEXT")
    private String description;

    // Gender: BOYS, GIRLS, BOTH
    private String genderType;

    // ===== ROOM PRICES =====
    private String singleRoomPrice;
    private String doubleSharePrice;
    private String tripleSharePrice;
    private String fourSharePrice;

    // ===== AMENITIES =====
    // Stored as comma separated: "WiFi,Food,AC,Laundry"
    @Column(columnDefinition = "TEXT")
    private String amenities;

    // ===== RULES =====
    @Column(columnDefinition = "TEXT")
    private String rules;

    private String timings;

    // ===== PHOTOS =====
    // Stored as comma separated Cloudinary URLs
    @Column(columnDefinition = "TEXT")
    private String photos;

    // ===== VIDEOS =====
    @Column(columnDefinition = "TEXT")
    private String videos;

    // ===== STATUS =====
    private Boolean isAvailable = true;
    private Boolean isActive = true;
    private Integer totalRooms;
    private Integer availableRooms;

    // ===== TIMESTAMPS =====
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}