

/*
package com.studenttap.controller;


import com.studenttap.dto.ApiResponse;
import com.studenttap.service.EducationService;
import com.studenttap.service.GalleryService;
import com.studenttap.service.ProfileService;
import com.studenttap.repository.ResumeRepository;
import com.studenttap.repository.StudentRepository;
import com.studenttap.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private EducationService educationService;

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ResumeRepository resumeRepository;

    // ===================================================
    // GET /api/public/profile/{username}
    // Already in ProfileController - this is a duplicate
    // for clarity - returns full public profile
    // ===================================================

    // ===================================================
    // GET /api/public/education/{username}
    // Public education records - no login needed
    // Called by portfolio page
    // ===================================================
    @GetMapping("/education/{username}")
    public ResponseEntity<?> getPublicEducation(
            @PathVariable String username) {
        try {
            Student student = studentRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));

            var list = educationService
                .getEducationByStudentId(student.getId());

            return ResponseEntity.ok(
                ApiResponse.success("Education", list));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // GET /api/public/certifications/{username}
    // Public certifications - no login needed
    // ===================================================
    @GetMapping("/certifications/{username}")
    public ResponseEntity<?> getPublicCertifications(
            @PathVariable String username) {
        try {
            Student student = studentRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));

            var list = educationService
                .getCertificationsByStudentId(student.getId());

            return ResponseEntity.ok(
                ApiResponse.success("Certifications", list));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // GET /api/public/experience/{username}
    // Public work experience - no login needed
    // ===================================================
    @GetMapping("/experience/{username}")
    public ResponseEntity<?> getPublicExperience(
            @PathVariable String username) {
        try {
            Student student = studentRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));

            var list = educationService
                .getWorkExperienceByStudentId(student.getId());

            return ResponseEntity.ok(
                ApiResponse.success("Experience", list));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    
 // ===================================================
 // ADD THIS METHOD to your existing PublicController.java
 // inside the class — after the getPublicGallery method
 // ===================================================

     // GET /api/public/resume/{username}
     // Visitor checks if resume is downloadable
     @GetMapping("/resume/{username}")
     public ResponseEntity<?> getPublicResume(
             @PathVariable String username) {
         try {
             Student student = studentRepository
                 .findByUsername(username)
                 .orElseThrow(() ->
                     new RuntimeException("Student not found"));

             // You need to @Autowired ResumeRepository
             // Add this at the top of PublicController:
             // @Autowired
             // private ResumeRepository resumeRepository;

             var resume = resumeRepository
                 .findByStudentId(student.getId())
                 .orElse(null);

             if (resume == null || !resume.getIsDownloadable()) {
                 return ResponseEntity.ok(
                     ApiResponse.success("No resume", null));
             }

             return ResponseEntity.ok(
                 ApiResponse.success("Resume", resume));
         } catch (Exception e) {
             return ResponseEntity.status(404)
                 .body(ApiResponse.error(e.getMessage()));
         }
     }
    

    // ===================================================
    // GET /api/public/gallery/{username}
    // Public gallery photos - no login needed
    // ===================================================
    
    
    
    
    
}

*/

package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;


import com.studenttap.model.Student;
import com.studenttap.repository.ResumeRepository;
import com.studenttap.repository.StudentRepository;
import com.studenttap.service.EducationService;
import com.studenttap.service.GalleryService;
import com.studenttap.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.studenttap.model.BusinessUser;
import com.studenttap.repository.BusinessUserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicController {

    @Autowired
    private ProfileService profileService;
    
    @Autowired
 private BusinessUserRepository businessUserRepository;

    @Autowired
    private EducationService educationService;

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private StudentRepository studentRepository;

    // ✅ ADD THIS - was missing before
    @Autowired
    private ResumeRepository resumeRepository;

    // GET /api/public/education/{username}
    @GetMapping("/education/{username}")
    public ResponseEntity<?> getPublicEducation(
            @PathVariable String username) {
        try {
            Student student = studentRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));
            var list = educationService
                .getEducationByStudentId(student.getId());
            return ResponseEntity.ok(
                ApiResponse.success("Education", list));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/public/certifications/{username}
    @GetMapping("/certifications/{username}")
    public ResponseEntity<?> getPublicCertifications(
            @PathVariable String username) {
        try {
            Student student = studentRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));
            var list = educationService
                .getCertificationsByStudentId(student.getId());
            return ResponseEntity.ok(
                ApiResponse.success("Certifications", list));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/public/experience/{username}
    @GetMapping("/experience/{username}")
    public ResponseEntity<?> getPublicExperience(
            @PathVariable String username) {
        try {
            Student student = studentRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));
            var list = educationService
                .getWorkExperienceByStudentId(student.getId());
            return ResponseEntity.ok(
                ApiResponse.success("Experience", list));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/public/gallery/{username}
    @GetMapping("/gallery/{username}")
    public ResponseEntity<?> getPublicGallery(
            @PathVariable String username) {
        try {
            Student student = studentRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));
            var list = galleryService
                .getGalleryByStudentId(student.getId());
            return ResponseEntity.ok(
                ApiResponse.success("Gallery", list));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    
    
    // ✅ NEW - GET /api/public/resume/{username}
   
    // Visitor checks if resume is available to download
    
    /*
    @GetMapping("/resume/{username}")
    public ResponseEntity<?> getPublicResume(
            @PathVariable String username) {
        try {
            Student student = studentRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));

            var resume = resumeRepository
                .findByStudentId(student.getId())
                .orElse(null);

            // Only return if student enabled download
            if (resume == null
                    || !resume.getIsDownloadable()
                    || resume.getResumePath() == null) {
                return ResponseEntity.ok(
                    ApiResponse.success(
                        "Resume not available", null));
            }

            return ResponseEntity.ok(
                ApiResponse.success("Resume", resume));

        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    */
    
    
    
    
 // ===================================================
 // 🌐 PUBLIC - GET /api/public/companies
 // Students see all registered companies
 // ===================================================
 @GetMapping("/api/public/companies")
 public ResponseEntity<?> getPublicCompanies() {
     try {
         List<BusinessUser> companies =
             businessUserRepository.findByUserType(
                 BusinessUser.UserType.COMPANY);
  
         // Return only safe public info
         // (no passwords etc.)
         List<Map<String, Object>> result =
             new ArrayList<>();
  
         for (BusinessUser c : companies) {
             if (!c.getIsActive()) continue;
             Map<String, Object> data = new HashMap<>();
             data.put("id", c.getId());
             data.put("businessName", c.getBusinessName());
             data.put("fullName", c.getFullName());
             data.put("email", c.getEmail());
             data.put("phone", c.getPhone());
             data.put("city", c.getCity());
             data.put("industry", c.getIndustry());
             result.add(data);
         }
  
         return ResponseEntity.ok(
             ApiResponse.success("Companies", result));
     } catch (Exception e) {
         return ResponseEntity.badRequest()
             .body(ApiResponse.error(e.getMessage()));
     }
 }
    
    
    
    
    @GetMapping("/resume/{username}")
    public ResponseEntity<?> getPublicResume(
            @PathVariable String username) {
        try {
            Student student = studentRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));

            var resume = resumeRepository
                .findByStudentId(student.getId())
                .orElse(null);

            // ✅ FIX: Only check isDownloadable
            // Don't require resumePath!
            if (resume == null
                    || !Boolean.TRUE.equals(
                        resume.getIsDownloadable())) {
                return ResponseEntity.ok(
                    ApiResponse.success("No resume", null));
            }

            return ResponseEntity.ok(
                ApiResponse.success("Resume", resume));

        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}