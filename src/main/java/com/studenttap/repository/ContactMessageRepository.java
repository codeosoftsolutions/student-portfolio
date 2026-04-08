package com.studenttap.repository;

import com.studenttap.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository
        extends JpaRepository<ContactMessage, Long> {

    // Get all messages for a student
    List<ContactMessage> findByStudentId(Long studentId);

    // ✅ Count messages for student stats
    long countByStudentId(Long studentId);

    // Get unread messages
    List<ContactMessage> findByStudentIdAndIsRead(
        Long studentId, Boolean isRead);

    // Count unread messages
    long countByStudentIdAndIsRead(
        Long studentId, Boolean isRead);
}