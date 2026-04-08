

package com.studenttap.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "business_users")
@Data
public class BusinessUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== USER TYPE =====
    // HOSTEL, INSTITUTE, COMPANY
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    public enum UserType {
        HOSTEL, INSTITUTE, COMPANY
    }

    // ===== BASIC INFO =====
    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;
    private String city;
    private String industry; // for companies

    // ===== STATUS =====
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isApproved = true;

    // ===== TIMESTAMPS =====
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastLoginAt;
}