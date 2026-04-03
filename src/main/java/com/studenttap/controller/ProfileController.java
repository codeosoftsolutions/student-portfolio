

/*
package com.studenttap.controller;


import com.studenttap.dto.ApiResponse;
import com.studenttap.dto.PersonalDetailsRequest;
import com.studenttap.dto.ProfileRequest;
import com.studenttap.dto.PublicProfileResponse;
import com.studenttap.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // ===================================================
    // 🌐 PUBLIC API - No login needed
    // GET /api/public/profile/{username}
    // Called when NFC card is tapped — opens portfolio
    // ===================================================
    @GetMapping("/api/public/profile/{username}")
    public ResponseEntity<?> getPublicProfile(
            @PathVariable String username) {
        try {
            PublicProfileResponse profile =
                profileService.getPublicProfile(username);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 PROTECTED API - Login required
    // GET /api/student/profile
    // Student views their own profile in dashboard
    // ===================================================
    @GetMapping("/api/student/profile")
    public ResponseEntity<?> getMyProfile(
            Authentication authentication) {
        try {
            String email = authentication.getName();
            return ResponseEntity.ok(
                profileService.getStudentByEmail(email)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 PROTECTED API - Login required
    // PUT /api/student/profile
    // Student updates their profile info
    // ===================================================
    @PutMapping("/api/student/profile")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody ProfileRequest request) {
        try {
            String email = authentication.getName();
            profileService.updateProfile(email, request);
            return ResponseEntity.ok(
                ApiResponse.success("Profile updated successfully!")
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 PROTECTED API - Login required
    // POST /api/student/profile/photo
    // Student uploads their profile photo
    // ===================================================
    @PostMapping("/api/student/profile/photo")
    public ResponseEntity<?> uploadProfilePhoto(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null ||
                !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Only image files allowed!"));
            }

            String email = authentication.getName();
            String fileName = profileService
                .uploadProfilePhoto(email, file);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Profile photo uploaded!",
                    "/uploads/profiles/" + fileName)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Upload failed: " + e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 PROTECTED API - Login required
    // POST /api/student/profile/cover
    // Student uploads their cover/banner photo
    // ===================================================
    @PostMapping("/api/student/profile/cover")
    public ResponseEntity<?> uploadCoverPhoto(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null ||
                !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Only image files allowed!"));
            }

            String email = authentication.getName();
            String fileName = profileService
                .uploadCoverPhoto(email, file);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Cover photo uploaded!",
                    "/uploads/covers/" + fileName)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Upload failed: " + e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 PROTECTED API - Login required
    // POST /api/student/personal-details
    // Student saves personal details
    // ===================================================
    @PostMapping("/api/student/personal-details")
    public ResponseEntity<?> savePersonalDetails(
            Authentication authentication,
            @RequestBody PersonalDetailsRequest request) {
        try {
            String email = authentication.getName();
            profileService.savePersonalDetails(email, request);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Personal details saved successfully!")
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 PROTECTED API - Login required
    // GET /api/student/personal-details
    // Student views their personal details
    // ===================================================
    @GetMapping("/api/student/personal-details")
    public ResponseEntity<?> getPersonalDetails(
            Authentication authentication) {
        try {
            String email = authentication.getName();
            var student = profileService.getStudentByEmail(email);
            // Return personal details - handled in service
            return ResponseEntity.ok(
                ApiResponse.success("Personal details fetched", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
*/






package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.dto.PersonalDetailsRequest;
import com.studenttap.dto.ProfileRequest;
import com.studenttap.dto.PublicProfileResponse;
import com.studenttap.model.Student;
import com.studenttap.repository.StudentRepository;
import com.studenttap.service.CloudinaryService;
import com.studenttap.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ===================================================
    // 🌐 PUBLIC API - No login needed
    // GET /api/public/profile/{username}
    // Called when NFC card is tapped — opens portfolio
    // ===================================================
    @GetMapping("/api/public/profile/{username}")
    public ResponseEntity<?> getPublicProfile(
            @PathVariable String username) {
        try {
            PublicProfileResponse profile =
                profileService.getPublicProfile(username);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 PROTECTED API - Login required
    // GET /api/student/profile
    // ===================================================
    @GetMapping("/api/student/profile")
    public ResponseEntity<?> getMyProfile(
            Authentication authentication) {
        try {
            String email = authentication.getName();
            return ResponseEntity.ok(
                profileService.getStudentByEmail(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 PROTECTED API - Login required
    // PUT /api/student/profile
    // ===================================================
   
    /*
    @PutMapping("/api/student/profile")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody ProfileRequest request) {
        try {
            String email = authentication.getName();
            profileService.updateProfile(email, request);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Profile updated successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    */
    
    @PutMapping("/api/student/profile")
    public ResponseEntity<?> updateProfile(
            Authentication auth,
            @RequestBody ProfileRequest request) {
        try {
            profileService.updateProfile(
                auth.getName(), request);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Profile updated successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    
    
    
    /*

    // ===================================================
    // 🔒 POST /api/student/profile/photo
    // ✅ Upload to Cloudinary - permanent storage!
    // ===================================================
    @PostMapping("/api/student/profile/photo")
    public ResponseEntity<?> uploadProfilePhoto(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null ||
                    !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Only image files allowed!"));
            }

            String email = authentication.getName();

            // ✅ Upload to Cloudinary
            String photoUrl = cloudinaryService
                .uploadPhoto(file, "profiles");

            // Save Cloudinary URL to database
            Student student = studentRepository
                .findByEmail(email)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Student not found"));
            student.setProfilePhoto(photoUrl);
            studentRepository.save(student);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Profile photo uploaded! ✅",
                    photoUrl));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Upload failed: " + e.getMessage()));
        }
    }
    
    */
    
    @PostMapping("/api/student/profile/photo")
    public ResponseEntity<?> uploadProfilePhoto(
            Authentication auth,
            @RequestParam("file") MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null ||
                    !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Only image files allowed!"));
            }
 
            // ✅ Upload to Cloudinary
            String photoUrl = cloudinaryService
                .uploadPhoto(file, "profiles");
 
            // Save Cloudinary URL to database
            Student student = studentRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));
            student.setProfilePhoto(photoUrl);
            studentRepository.save(student);
 
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Profile photo uploaded! ✅", photoUrl));
 
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Upload failed: " + e.getMessage()));
        }
    }
    
    /*

    // ===================================================
    // 🔒 POST /api/student/profile/cover
    // ✅ Upload to Cloudinary - permanent storage!
    // ===================================================
    @PostMapping("/api/student/profile/cover")
    public ResponseEntity<?> uploadCoverPhoto(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null ||
                    !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Only image files allowed!"));
            }

            String email = authentication.getName();

            // ✅ Upload to Cloudinary
            String coverUrl = cloudinaryService
                .uploadPhoto(file, "covers");

            // Save Cloudinary URL to database
            Student student = studentRepository
                .findByEmail(email)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Student not found"));
            student.setCoverPhoto(coverUrl);
            studentRepository.save(student);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Cover photo uploaded! ✅",
                    coverUrl));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Upload failed: " + e.getMessage()));
        }
    }
    
    */
    
    
    @PostMapping("/api/student/profile/cover")
    public ResponseEntity<?> uploadCoverPhoto(
            Authentication auth,
            @RequestParam("file") MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null ||
                    !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Only image files allowed!"));
            }
 
            // ✅ Upload to Cloudinary
            String coverUrl = cloudinaryService
                .uploadPhoto(file, "covers");
 
            // Save Cloudinary URL to database
            Student student = studentRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));
            student.setCoverPhoto(coverUrl);
            studentRepository.save(student);
 
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Cover photo uploaded! ✅", coverUrl));
 
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Upload failed: " + e.getMessage()));
        }
    }
    
    

    // ===================================================
    // 🔒 POST /api/student/personal-details
    // ===================================================
    @PostMapping("/api/student/personal-details")
    public ResponseEntity<?> savePersonalDetails(
            Authentication authentication,
            @RequestBody PersonalDetailsRequest request) {
        try {
            String email = authentication.getName();
            profileService.savePersonalDetails(
                email, request);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Personal details saved! ✅"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    

    // ===================================================
    // 🔒 GET /api/student/personal-details
    // ===================================================
  /*  @GetMapping("/api/student/personal-details")
    public ResponseEntity<?> getPersonalDetails(
            Authentication authentication) {
        try {
            String email = authentication.getName();
            var details = profileService
                .getPersonalDetails(email);
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Personal details", details));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }*/
    
    @GetMapping("/api/student/personal-details")
    public ResponseEntity<?> getPersonalDetails(
            Authentication authentication) {
        try {
            String email = authentication.getName();
            var student = profileService.getStudentByEmail(email);
            // Return personal details - handled in service
            return ResponseEntity.ok(
                ApiResponse.success("Personal details fetched", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
}


