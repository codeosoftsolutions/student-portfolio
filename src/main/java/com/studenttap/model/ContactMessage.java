package com.studenttap.model;
 
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "contact_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
 
    @Column(name = "sender_name", nullable = false, length = 150)
    private String senderName;
 
    @Column(name = "sender_email", nullable = false, length = 150)
    private String senderEmail;
 
    @Column(name = "sender_phone", length = 20)
    private String senderPhone;
 
    @Column(length = 255)
    private String subject;
 
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    @Column(name = "is_read")
    private Boolean isRead = false;
 
    @Column(name = "sent_at")
    private LocalDateTime sentAt = LocalDateTime.now();
}