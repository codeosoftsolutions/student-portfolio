


package com.studenttap.repository;
 
import com.studenttap.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
 
@Repository
public interface ContactRepository
        extends JpaRepository<ContactMessage, Long> {
 
    // Get all messages for a student
    // newest first
    List<ContactMessage> findByStudentIdOrderBySentAtDesc(
        Long studentId);
 
    // Count unread messages
    long countByStudentIdAndIsRead(
        Long studentId, Boolean isRead);
}