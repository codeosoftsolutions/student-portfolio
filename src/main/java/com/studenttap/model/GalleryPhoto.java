



package com.studenttap.model;
 
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "gallery_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryPhoto {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
 
    @Column(name = "photo_path", nullable = false)
    private String photoPath;
 
    @Column(length = 255)
    private String caption;
 
    @Column(name = "display_order")
    private Integer displayOrder = 0;
 
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();
}