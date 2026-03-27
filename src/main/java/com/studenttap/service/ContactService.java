


package com.studenttap.service;
 
import com.studenttap.dto.ApiResponse;
import com.studenttap.dto.ContactFormRequest;
import com.studenttap.model.ContactMessage;
import com.studenttap.model.Student;
import com.studenttap.repository.ContactRepository;
import com.studenttap.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
 
@Service
public class ContactService {
 
    @Autowired
    private ContactRepository contactRepository;
 
    @Autowired
    private StudentRepository studentRepository;
 
    // ===================================================
    // SEND message to a student (public - no login needed)
    // Called when visitor fills contact form on portfolio
    // ===================================================
    public ContactMessage sendMessage(
            String username,
            ContactFormRequest request) {
 
        // Find the student by username
        Student student = studentRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new RuntimeException(
                    "Student profile not found!"));
 
        // Save message to database
        ContactMessage message = new ContactMessage();
        message.setStudent(student);
        message.setSenderName(request.getSenderName());
        message.setSenderEmail(request.getSenderEmail());
        message.setSenderPhone(request.getSenderPhone());
        message.setSubject(request.getSubject());
        message.setMessage(request.getMessage());
        message.setIsRead(false);
 
        return contactRepository.save(message);
    }
 
    // ===================================================
    // GET all messages for logged-in student
    // Student sees messages in dashboard
    // ===================================================
    public List<ContactMessage> getMyMessages(
            String email) {
 
        Student student = studentRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Student not found!"));
 
        return contactRepository
            .findByStudentIdOrderBySentAtDesc(
                student.getId());
    }
 
    // ===================================================
    // MARK message as read
    // ===================================================
    public ContactMessage markAsRead(
            String email, Long messageId) {
 
        Student student = studentRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Student not found!"));
 
        ContactMessage message = contactRepository
            .findById(messageId)
            .orElseThrow(() ->
                new RuntimeException("Message not found!"));
 
        // Security - only own messages
        if (!message.getStudent().getId()
                .equals(student.getId())) {
            throw new RuntimeException(
                "Access denied!");
        }
 
        message.setIsRead(true);
        return contactRepository.save(message);
    }
 
    // ===================================================
    // GET unread message count
    // Shown as badge in dashboard
    // ===================================================
    public long getUnreadCount(String email) {
 
        Student student = studentRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Student not found!"));
 
        return contactRepository
            .countByStudentIdAndIsRead(
                student.getId(), false);
    }
 
    // ===================================================
    // DELETE a message
    // ===================================================
    public void deleteMessage(
            String email, Long messageId) {
 
        Student student = studentRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Student not found!"));
 
        ContactMessage message = contactRepository
            .findById(messageId)
            .orElseThrow(() ->
                new RuntimeException("Message not found!"));
 
        // Security - only own messages
        if (!message.getStudent().getId()
                .equals(student.getId())) {
            throw new RuntimeException("Access denied!");
        }
 
        contactRepository.deleteById(messageId);
    }
}
 