


/*package com.studenttap.controller;
 
import com.studenttap.dto.ApiResponse;
import com.studenttap.model.Resume;
import com.studenttap.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
 
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
 
@RestController
@CrossOrigin(origins = "*")
public class ResumeController {
 
    @Autowired
    private ResumeService resumeService;
 
    @Value("${app.upload.dir}")
    private String uploadDir;
 
    // 🔒 POST /api/student/resume/upload
    // Student uploads their resume PDF
    @PostMapping("/api/student/resume/upload")
    public ResponseEntity<?> uploadResume(
            Authentication auth,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "template",
                required = false,
                defaultValue = "CUSTOM") String template) {
        try {
            var resume = resumeService.uploadResume(
                auth.getName(), file, template);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Resume uploaded successfully!",
                    "/uploads/resumes/"
                        + resume.getResumePath()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // 🔒 POST /api/student/resume/template
    // Student selects a resume template
    // templateName: TEMPLATE_1, TEMPLATE_2,
    //               TEMPLATE_3, TEMPLATE_4
    @PostMapping("/api/student/resume/template")
    public ResponseEntity<?> selectTemplate(
            Authentication auth,
            @RequestParam("templateName") String templateName) {
        try {
            var resume = resumeService.selectTemplate(
                auth.getName(), templateName);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Template " + templateName + " selected!",
                    resume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // 🔒 GET /api/student/resume
    // Get resume info of logged-in student
    @GetMapping("/api/student/resume")
    public ResponseEntity<?> getMyResume(
            Authentication auth) {
        try {
            var resume = resumeService
                .getResume(auth.getName());
            if (resume == null) {
                return ResponseEntity.ok(
                    ApiResponse.success(
                        "No resume uploaded yet", null));
            }
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Resume fetched", resume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // 🔒 PUT /api/student/resume/toggle-download
    // Enable or disable resume download for visitors
    @PutMapping("/api/student/resume/toggle-download")
    public ResponseEntity<?> toggleDownload(
            Authentication auth) {
        try {
            var resume = resumeService
                .toggleDownloadable(auth.getName());
            String msg = resume.getIsDownloadable()
                ? "Resume download ENABLED for visitors"
                : "Resume download DISABLED for visitors";
            return ResponseEntity.ok(
                ApiResponse.success(msg, resume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // 🌐 GET /api/public/resume/download/{username}
    // Visitor downloads student resume
    // No login needed - public access
    @GetMapping("/api/public/resume/download/{username}")
    public ResponseEntity<?> downloadResume(
            @PathVariable String username) {
        try {
            // Find student resume
            // (simplified - you can add studentRepo here)
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Use /uploads/resumes/{filename}", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
*/


// =====================================================
// FIX 1: Replace your ResumeController.java
// The toggle was calling wrong API method
// Path: src/main/java/com/studenttap/controller/
// =====================================================


/*package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.model.Resume;
import com.studenttap.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // 🔒 POST /api/student/resume/upload
    @PostMapping("/api/student/resume/upload")
    public ResponseEntity<?> uploadResume(
            Authentication auth,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "template",
                required = false,
                defaultValue = "CUSTOM") String template) {
        try {
            var resume = resumeService.uploadResume(
                auth.getName(), file, template);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Resume uploaded successfully!",
                    "/uploads/resumes/" + resume.getResumePath()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Upload failed: " + e.getMessage()));
        }
    }

    // 🔒 POST /api/student/resume/template
    @PostMapping("/api/student/resume/template")
    public ResponseEntity<?> selectTemplate(
            Authentication auth,
            @RequestParam("templateName") String templateName) {
        try {
            var resume = resumeService.selectTemplate(
                auth.getName(), templateName);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Template " + templateName + " selected!",
                    resume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🔒 GET /api/student/resume
    @GetMapping("/api/student/resume")
    public ResponseEntity<?> getMyResume(Authentication auth) {
        try {
            var resume = resumeService.getResume(auth.getName());
            if (resume == null) {
                return ResponseEntity.ok(
                    ApiResponse.success(
                        "No resume uploaded yet", null));
            }
            return ResponseEntity.ok(
                ApiResponse.success("Resume fetched", resume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ✅ FIX: Toggle downloadable - was broken before
    // 🔒 PUT /api/student/resume/toggle-download
    @PutMapping("/api/student/resume/toggle-download")
    public ResponseEntity<?> toggleDownload(Authentication auth) {
        try {
            var resume = resumeService
                .toggleDownloadable(auth.getName());
            boolean enabled = resume.getIsDownloadable();
            String msg = enabled
                ? "✅ Resume download ENABLED — visitors can now download your resume!"
                : "❌ Resume download DISABLED — visitors cannot download your resume";
            return ResponseEntity.ok(
                ApiResponse.success(msg, resume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Error: " + e.getMessage()
                    + " — Please upload a resume first!"));
        }
    }
}
*/



// =====================================================
// FIX 1: Replace your ResumeController.java
// The toggle was calling wrong API method
// Path: src/main/java/com/studenttap/controller/
// =====================================================
package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.model.Resume;
import com.studenttap.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // 🔒 POST /api/student/resume/upload
    @PostMapping("/api/student/resume/upload")
    public ResponseEntity<?> uploadResume(
            Authentication auth,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "template",
                required = false,
                defaultValue = "CUSTOM") String template) {
        try {
            var resume = resumeService.uploadResume(
                auth.getName(), file, template);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Resume uploaded successfully!",
                    "/uploads/resumes/" + resume.getResumePath()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Upload failed: " + e.getMessage()));
        }
    }

    // 🔒 POST /api/student/resume/template
    @PostMapping("/api/student/resume/template")
    public ResponseEntity<?> selectTemplate(
            Authentication auth,
            @RequestParam("templateName") String templateName) {
        try {
            var resume = resumeService.selectTemplate(
                auth.getName(), templateName);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Template " + templateName + " selected!",
                    resume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // new one
    
    

    // 🔒 GET /api/student/resume
    @GetMapping("/api/student/resume")
    public ResponseEntity<?> getMyResume(Authentication auth) {
        try {
            var resume = resumeService.getResume(auth.getName());
            if (resume == null) {
                return ResponseEntity.ok(
                    ApiResponse.success(
                        "No resume uploaded yet", null));
            }
            return ResponseEntity.ok(
                ApiResponse.success("Resume fetched", resume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ✅ FIX: Toggle downloadable - was broken before
    // 🔒 PUT /api/student/resume/toggle-download
    @PutMapping("/api/student/resume/toggle-download")
    public ResponseEntity<?> toggleDownload(Authentication auth) {
        try {
            var resume = resumeService
                .toggleDownloadable(auth.getName());
            boolean enabled = resume.getIsDownloadable();
            String msg = enabled
                ? "✅ Resume download ENABLED — visitors can now download your resume!"
                : "❌ Resume download DISABLED — visitors cannot download your resume";
            return ResponseEntity.ok(
                ApiResponse.success(msg, resume));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Error: " + e.getMessage()
                    + " — Please upload a resume first!"));
        }
    }
}
 