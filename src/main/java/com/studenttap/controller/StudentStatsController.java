package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.model.Student;
import com.studenttap.repository.CertificationRepository;
import com.studenttap.repository.ContactMessageRepository;
import com.studenttap.repository.EducationRepository;
import com.studenttap.repository.GalleryRepository;
import com.studenttap.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentStatsController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private GalleryRepository galleryRepository;

    // ===================================================
    // GET /api/student/stats
    // Returns counts for student home dashboard
    // ===================================================
    @GetMapping("/stats")
    public ResponseEntity<?> getStudentStats(
            Authentication auth) {
        try {
            Student student = studentRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException(
                        "Student not found"));

            Long studentId = student.getId();

            Map<String, Object> stats = new HashMap<>();

            // ✅ Education count
            stats.put("educationCount",
                educationRepository
                    .countByStudentId(studentId));

            // ✅ Certification count
            stats.put("certCount",
                certificationRepository
                    .countByStudentId(studentId));

            // ✅ Message count
            stats.put("messageCount",
                contactMessageRepository
                    .countByStudentId(studentId));

            // ✅ Unread message count
            stats.put("unreadCount",
                contactMessageRepository
                    .countByStudentIdAndIsRead(
                        studentId, false));

            // ✅ Gallery count
            stats.put("galleryCount",
                galleryRepository
                    .countByStudentId(studentId));

            // ✅ Student info
            stats.put("username", student.getUsername());
            stats.put("fullName", student.getFullName());
            stats.put("profilePhoto",
                student.getProfilePhoto());

            return ResponseEntity.ok(
                ApiResponse.success("Stats", stats));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}