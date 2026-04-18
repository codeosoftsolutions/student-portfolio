
package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;

import com.studenttap.model.BusinessUser;
import com.studenttap.model.Student;
import com.studenttap.repository.BusinessUserRepository;
import com.studenttap.repository.ResumeRepository;
import com.studenttap.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/company")
@CrossOrigin(origins = "*")
public class CompanyController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BusinessUserRepository businessUserRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    // ===================================================
    // GET /api/company/students
    // Company sees ALL registered students
    // ===================================================
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents(
            Authentication auth) {
        try {
            // Verify caller is a company
            BusinessUser company = businessUserRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException(
                        "Company not found"));

            if (company.getUserType() !=
                    BusinessUser.UserType.COMPANY) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error(
                        "Access denied!"));
            }

            // Get ALL students
            List<Student> students =
                studentRepository.findAll();

            // Build response with needed fields only
            List<Map<String, Object>> result =
                new ArrayList<>();

            for (Student s : students) {
                Map<String, Object> studentData =
                    new HashMap<>();

                studentData.put("id", s.getId());
                studentData.put("fullName", s.getFullName());
                studentData.put("username", s.getUsername());
                studentData.put("email", s.getEmail());
                studentData.put("phone", s.getPhone());
               // studentData.put("skills", s.getSkills());
                studentData.put("designation",
                    s.getDesignation());
                studentData.put("isFresher", s.getIsFresher());
                studentData.put("profilePhoto",
                    s.getProfilePhoto());
                studentData.put("address", s.getAddress());
                studentData.put("bio", s.getBio());

                // Check if resume exists
                boolean hasResume = resumeRepository
                    .findByStudentId(s.getId())
                    .map(r -> r.getIsDownloadable() != null
                        && r.getIsDownloadable())
                    .orElse(false);

                studentData.put("hasResume", hasResume);

                // Get resume template if exists
                String template = resumeRepository
                    .findByStudentId(s.getId())
                    .map(r -> r.getTemplateUsed())
                    .orElse("TEMPLATE_1");

                studentData.put("resumeTemplate", template);

                result.add(studentData);
            }

            return ResponseEntity.ok(
                ApiResponse.success("Students", result));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // GET /api/company/profile
    // Company gets own profile
    // ===================================================
    @GetMapping("/profile")
    public ResponseEntity<?> getCompanyProfile(
            Authentication auth) {
        try {
            BusinessUser company = businessUserRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException(
                        "Company not found"));

            return ResponseEntity.ok(
                ApiResponse.success("Profile", company));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}