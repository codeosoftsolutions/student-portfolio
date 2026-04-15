

package com.studenttap.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "advertisements")
@Data
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // Cloudinary URL for ad image
    private String imageUrl;

    // Click URL
    private String linkUrl;

    // Target: ALL, STUDENT, HOSTEL, INSTITUTE, COMPANY
    private String targetAudience = "ALL";

    private Boolean isActive = true;

    // Display order
    private Integer displayOrder = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
}