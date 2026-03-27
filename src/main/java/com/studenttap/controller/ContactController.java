package com.studenttap.controller;
 
import com.studenttap.dto.ApiResponse;
import com.studenttap.dto.ContactFormRequest;
import com.studenttap.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
 
@RestController
@CrossOrigin(origins = "*")
public class ContactController {
 
    @Autowired
    private ContactService contactService;
 
    // ===================================================
    // 🌐 PUBLIC API - No login needed
    // POST /api/public/contact/{username}
    // Visitor sends message to student from portfolio page
    // ===================================================
    @PostMapping("/api/public/contact/{username}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String username,
            @Valid @RequestBody ContactFormRequest request) {
        try {
            contactService.sendMessage(username, request);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Message sent successfully! "
                    + "The student will contact you soon."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // ===================================================
    // 🔒 PROTECTED - Login required
    // GET /api/student/messages
    // Student reads all messages in dashboard
    // ===================================================
    @GetMapping("/api/student/messages")
    public ResponseEntity<?> getMyMessages(
            Authentication auth) {
        try {
            var messages = contactService
                .getMyMessages(auth.getName());
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Messages fetched", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // ===================================================
    // 🔒 PROTECTED - Login required
    // GET /api/student/messages/unread-count
    // Get count of unread messages (for badge in dashboard)
    // ===================================================
    @GetMapping("/api/student/messages/unread-count")
    public ResponseEntity<?> getUnreadCount(
            Authentication auth) {
        try {
            long count = contactService
                .getUnreadCount(auth.getName());
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Unread count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // ===================================================
    // 🔒 PROTECTED - Login required
    // PUT /api/student/messages/{id}/read
    // Mark a message as read
    // ===================================================
    @PutMapping("/api/student/messages/{id}/read")
    public ResponseEntity<?> markAsRead(
            Authentication auth,
            @PathVariable Long id) {
        try {
            contactService.markAsRead(auth.getName(), id);
            return ResponseEntity.ok(
                ApiResponse.success("Message marked as read!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // ===================================================
    // 🔒 PROTECTED - Login required
    // DELETE /api/student/messages/{id}
    // Delete a message
    // ===================================================
    @DeleteMapping("/api/student/messages/{id}")
    public ResponseEntity<?> deleteMessage(
            Authentication auth,
            @PathVariable Long id) {
        try {
            contactService.deleteMessage(auth.getName(), id);
            return ResponseEntity.ok(
                ApiResponse.success("Message deleted!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
 