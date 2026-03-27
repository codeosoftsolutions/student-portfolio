

package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.dto.CertificationRequest;
import com.studenttap.dto.EducationRequest;
import com.studenttap.dto.WorkExperienceRequest;
import com.studenttap.service.EducationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
public class EducationController {

    @Autowired
    private EducationService educationService;

    // =================================================
    // ========== EDUCATION APIs =======================
    // =================================================

    // 🔒 POST /api/student/education
    // Add or update one education record
    @PostMapping("/api/student/education")
    public ResponseEntity<?> saveEducation(
            Authentication auth,
            @Valid @RequestBody EducationRequest request) {
        try {
            var result = educationService
                .saveEducation(auth.getName(), request);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Education saved successfully!", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🔒 GET /api/student/education
    // Get all education records of logged-in student
    @GetMapping("/api/student/education")
    public ResponseEntity<?> getMyEducation(
            Authentication auth) {
        try {
            var list = educationService
                .getAllEducation(auth.getName());
            return ResponseEntity.ok(
                ApiResponse.success("Education fetched", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🔒 DELETE /api/student/education/{id}
    // Delete one education record
    @DeleteMapping("/api/student/education/{id}")
    public ResponseEntity<?> deleteEducation(
            Authentication auth,
            @PathVariable Long id) {
        try {
            educationService.deleteEducation(auth.getName(), id);
            return ResponseEntity.ok(
                ApiResponse.success("Education deleted!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // =================================================
    // ========== CERTIFICATION APIs ===================
    // =================================================

    // 🔒 POST /api/student/certifications
    // Add a new certification course
    @PostMapping("/api/student/certifications")
    public ResponseEntity<?> addCertification(
            Authentication auth,
            @Valid @RequestBody CertificationRequest request) {
        try {
            var result = educationService
                .addCertification(auth.getName(), request);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Certification added!", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🔒 POST /api/student/certifications/{id}/image
    // Upload certificate image/photo
    @PostMapping("/api/student/certifications/{id}/image")
    public ResponseEntity<?> uploadCertImage(
            Authentication auth,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            var result = educationService
                .uploadCertificateImage(
                    auth.getName(), id, file);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Certificate image uploaded!", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🔒 GET /api/student/certifications
    // Get all certifications of logged-in student
    @GetMapping("/api/student/certifications")
    public ResponseEntity<?> getMyCertifications(
            Authentication auth) {
        try {
            var list = educationService
                .getAllCertifications(auth.getName());
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Certifications fetched", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🔒 DELETE /api/student/certifications/{id}
    // Delete a certification
    @DeleteMapping("/api/student/certifications/{id}")
    public ResponseEntity<?> deleteCertification(
            Authentication auth,
            @PathVariable Long id) {
        try {
            educationService
                .deleteCertification(auth.getName(), id);
            return ResponseEntity.ok(
                ApiResponse.success("Certification deleted!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // =================================================
    // ========== WORK EXPERIENCE APIs =================
    // (For experienced candidates only)
    // =================================================

    // 🔒 POST /api/student/experience
    // Add a new work experience
    @PostMapping("/api/student/experience")
    public ResponseEntity<?> addExperience(
            Authentication auth,
            @Valid @RequestBody WorkExperienceRequest request) {
        try {
            var result = educationService
                .addWorkExperience(auth.getName(), request);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Work experience added!", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🔒 GET /api/student/experience
    // Get all work experience of logged-in student
    @GetMapping("/api/student/experience")
    public ResponseEntity<?> getMyExperience(
            Authentication auth) {
        try {
            var list = educationService
                .getAllWorkExperience(auth.getName());
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Experience fetched", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🔒 DELETE /api/student/experience/{id}
    // Delete a work experience record
    @DeleteMapping("/api/student/experience/{id}")
    public ResponseEntity<?> deleteExperience(
            Authentication auth,
            @PathVariable Long id) {
        try {
            educationService
                .deleteWorkExperience(auth.getName(), id);
            return ResponseEntity.ok(
                ApiResponse.success("Experience deleted!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}