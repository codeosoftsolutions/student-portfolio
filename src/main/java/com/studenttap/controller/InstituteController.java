
/*
package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.model.BusinessUser;
import com.studenttap.model.Institute;
import com.studenttap.repository.BusinessUserRepository;
import com.studenttap.repository.InstituteRepository;
import com.studenttap.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class InstituteController {// nkjbfgfbdub ghvuyfsa fuysa  ug

    @Autowired
    private InstituteRepository instituteRepository;

    @Autowired
    private BusinessUserRepository businessUserRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ===================================================
    // 🌐 PUBLIC - GET /api/public/institutes
    // Students see all institutes
    // ===================================================
    @GetMapping("/api/public/institutes")
    public ResponseEntity<?> getAllInstitutes(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type) {
        try {
            List<Institute> list;
            if (city != null && !city.isEmpty()) {
                list = instituteRepository
                    .findByCityAndIsActiveTrue(city);
            } else {
                list = instituteRepository
                    .findByIsActiveTrue();
            }
            return ResponseEntity.ok(
                ApiResponse.success("Institutes", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🌐 PUBLIC - GET /api/public/institutes/{id}
    // ===================================================
    @GetMapping("/api/public/institutes/{id}")
    public ResponseEntity<?> getInstituteById(
            @PathVariable Long id) {
        try {
            Institute institute = instituteRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Institute not found"));
            return ResponseEntity.ok(
                ApiResponse.success("Institute", institute));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 GET /api/institute/my
    // ===================================================
    @GetMapping("/api/institute/my")
    public ResponseEntity<?> getMyInstitute(
            Authentication auth) {
        try {
            BusinessUser owner = getOwner(auth.getName());
            Institute institute = instituteRepository
                .findByOwnerId(owner.getId())
                .orElse(null);
            return ResponseEntity.ok(
                ApiResponse.success("My Institute",
                    institute));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 POST /api/institute/save
    // ===================================================
    @PostMapping("/api/institute/save")
    public ResponseEntity<?> saveInstitute(
            Authentication auth,
            @RequestBody Map<String, Object> request) {
        try {
            BusinessUser owner = getOwner(auth.getName());

            Institute institute = instituteRepository
                .findByOwnerId(owner.getId())
                .orElse(new Institute());

            institute.setOwnerId(owner.getId());
            institute.setContactPerson(owner.getFullName());
            institute.setEmail(owner.getEmail());

            if (request.get("instituteName") != null)
                institute.setInstituteName(
                    request.get("instituteName").toString());
            if (request.get("phone") != null)
                institute.setPhone(
                    request.get("phone").toString());
            if (request.get("address") != null)
                institute.setAddress(
                    request.get("address").toString());
            if (request.get("city") != null)
                institute.setCity(
                    request.get("city").toString());
            if (request.get("googleMapsLink") != null)
                institute.setGoogleMapsLink(
                    request.get("googleMapsLink").toString());
            if (request.get("website") != null)
                institute.setWebsite(
                    request.get("website").toString());
            if (request.get("description") != null)
                institute.setDescription(
                    request.get("description").toString());
            if (request.get("instituteType") != null)
                institute.setInstituteType(
                    request.get("instituteType").toString());
            if (request.get("courses") != null)
                institute.setCourses(
                    request.get("courses").toString());
            if (request.get("feeRange") != null)
                institute.setFeeRange(
                    request.get("feeRange").toString());
            if (request.get("batchTimings") != null)
                institute.setBatchTimings(
                    request.get("batchTimings").toString());
            if (request.get("facilities") != null)
                institute.setFacilities(
                    request.get("facilities").toString());
            if (request.get("achievements") != null)
                institute.setAchievements(
                    request.get("achievements").toString());
            if (request.get("faculty") != null)
                institute.setFaculty(
                    request.get("faculty").toString());
            if (request.get("availableSeats") != null)
                institute.setAvailableSeats(
                    Integer.parseInt(request.get(
                        "availableSeats").toString()));

            institute.setUpdatedAt(LocalDateTime.now());
            instituteRepository.save(institute);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Institute details saved! ✅",
                    institute));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Save failed: " + e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 POST /api/institute/upload-photo
    // ===================================================
    @PostMapping("/api/institute/upload-photo")
    public ResponseEntity<?> uploadPhoto(
            Authentication auth,
            @RequestParam("file") MultipartFile file) {
        try {
            BusinessUser owner = getOwner(auth.getName());
            String photoUrl = cloudinaryService
                .uploadPhoto(file, "institutes");

            Institute institute = instituteRepository
                .findByOwnerId(owner.getId())
                .orElseThrow(() ->
                    new RuntimeException(
                        "Please save institute details first!"));

            String existing = institute.getPhotos();
            if (existing == null || existing.isEmpty()) {
                institute.setPhotos(photoUrl);
            } else {
                List<String> photos = new ArrayList<>(
                    Arrays.asList(existing.split(",")));
                if (photos.size() >= 10) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(
                            "Maximum 10 photos allowed!"));
                }
                photos.add(photoUrl);
                institute.setPhotos(
                    String.join(",", photos));
            }
            instituteRepository.save(institute);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Photo uploaded! ✅", photoUrl));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Upload failed: " + e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 DELETE /api/institute/photo
    // ===================================================
    @DeleteMapping("/api/institute/photo")
    public ResponseEntity<?> deletePhoto(
            Authentication auth,
            @RequestBody Map<String, String> request) {
        try {
            BusinessUser owner = getOwner(auth.getName());
            String photoUrl = request.get("photoUrl");

            Institute institute = instituteRepository
                .findByOwnerId(owner.getId())
                .orElseThrow(() ->
                    new RuntimeException(
                        "Institute not found"));

            String existing = institute.getPhotos();
            if (existing != null) {
                List<String> photos = new ArrayList<>(
                    Arrays.asList(existing.split(",")));
                photos.remove(photoUrl);
                institute.setPhotos(
                    String.join(",", photos));
                instituteRepository.save(institute);
                cloudinaryService.deletePhoto(
                    cloudinaryService
                        .getPublicIdFromUrl(photoUrl));
            }

            return ResponseEntity.ok(
                ApiResponse.success("Photo deleted! ✅"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    private BusinessUser getOwner(String email) {
        return businessUserRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Owner not found"));
    }
}


*/



package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.model.BusinessUser;
import com.studenttap.model.Institute;
import com.studenttap.repository.BusinessUserRepository;
import com.studenttap.repository.InstituteRepository;
import com.studenttap.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class InstituteController {

    @Autowired
    private InstituteRepository instituteRepository;

    @Autowired
    private BusinessUserRepository businessUserRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ===================================================
    // 🌐 PUBLIC - GET /api/public/institutes
    // ===================================================
    @GetMapping("/api/public/institutes")
    public ResponseEntity<?> getAllInstitutes(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type) {
        try {
            List<Institute> list;
            if (city != null && !city.isEmpty()) {
                list = instituteRepository
                    .findByCityAndIsActiveTrue(city);
            } else {
                list = instituteRepository
                    .findByIsActiveTrue();
            }
            return ResponseEntity.ok(
                ApiResponse.success("Institutes", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🌐 PUBLIC - GET /api/public/institutes/{id}
    // ===================================================
    @GetMapping("/api/public/institutes/{id}")
    public ResponseEntity<?> getInstituteById(
            @PathVariable Long id) {
        try {
            Institute institute = instituteRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Institute not found"));
            return ResponseEntity.ok(
                ApiResponse.success("Institute",
                    institute));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 GET /api/institute/my
    // ===================================================
    @GetMapping("/api/institute/my")
    public ResponseEntity<?> getMyInstitute(
            Authentication auth) {
        try {
            // ✅ Debug log
            System.out.println(">>> Institute GET - auth: "
                + (auth != null ? auth.getName() : "NULL"));

            if (auth == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(
                        "Not authenticated!"));
            }

            BusinessUser owner = getOwner(auth.getName());
            Institute institute = instituteRepository
                .findByOwnerId(owner.getId())
                .orElse(null);
            return ResponseEntity.ok(
                ApiResponse.success("My Institute",
                    institute));
        } catch (Exception e) {
            System.out.println(">>> Institute GET error: "
                + e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 POST /api/institute/save
    // ===================================================
    @PostMapping("/api/institute/save")
    public ResponseEntity<?> saveInstitute(
            Authentication auth,
            @RequestBody Map<String, Object> request) {
        try {
            // ✅ Debug log
            System.out.println(">>> Institute SAVE - auth: "
                + (auth != null ? auth.getName() : "NULL"));
            System.out.println(">>> Request: " + request);

            if (auth == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(
                        "Not authenticated! Please login again."));
            }

            BusinessUser owner = getOwner(auth.getName());
            System.out.println(">>> Owner found: "
                + owner.getEmail()
                + " type: " + owner.getUserType());

            // Verify this is an INSTITUTE user
            if (owner.getUserType() !=
                    BusinessUser.UserType.INSTITUTE) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error(
                        "Only institute owners can save!"));
            }

            Institute institute = instituteRepository
                .findByOwnerId(owner.getId())
                .orElse(new Institute());

            institute.setOwnerId(owner.getId());
            institute.setContactPerson(owner.getFullName());
            institute.setEmail(owner.getEmail());

            // Set fields safely
            setIfNotNull(institute, request);

            institute.setUpdatedAt(LocalDateTime.now());
            if (institute.getCreatedAt() == null) {
                institute.setCreatedAt(LocalDateTime.now());
            }
            if (institute.getIsActive() == null) {
                institute.setIsActive(true);
            }

            instituteRepository.save(institute);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Institute details saved! ✅",
                    institute));

        } catch (Exception e) {
            System.out.println(">>> Institute SAVE error: "
                + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Save failed: " + e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 POST /api/institute/upload-photo
    // ===================================================
    @PostMapping("/api/institute/upload-photo")
    public ResponseEntity<?> uploadPhoto(
            Authentication auth,
            @RequestParam("file") MultipartFile file) {
        try {
            System.out.println(
                ">>> Institute PHOTO - auth: "
                + (auth != null ? auth.getName() : "NULL"));

            if (auth == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(
                        "Not authenticated!"));
            }

            BusinessUser owner = getOwner(auth.getName());

            // ✅ Upload to Cloudinary
            String photoUrl = cloudinaryService
                .uploadPhoto(file, "institutes");

            // Get or create institute first
            Institute institute = instituteRepository
                .findByOwnerId(owner.getId())
                .orElseGet(() -> {
                    // Auto create institute record
                    Institute newInst = new Institute();
                    newInst.setOwnerId(owner.getId());
                    newInst.setInstituteName(
                        owner.getBusinessName());
                    newInst.setEmail(owner.getEmail());
                    newInst.setContactPerson(
                        owner.getFullName());
                    newInst.setIsActive(true);
                    newInst.setCreatedAt(
                        LocalDateTime.now());
                    newInst.setUpdatedAt(
                        LocalDateTime.now());
                    return instituteRepository
                        .save(newInst);
                });

            String existing = institute.getPhotos();
            if (existing == null || existing.isEmpty()) {
                institute.setPhotos(photoUrl);
            } else {
                List<String> photos = new ArrayList<>(
                    Arrays.asList(existing.split(",")));
                if (photos.size() >= 10) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(
                            "Maximum 10 photos allowed!"));
                }
                photos.add(photoUrl);
                institute.setPhotos(
                    String.join(",", photos));
            }
            instituteRepository.save(institute);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Photo uploaded! ✅", photoUrl));

        } catch (Exception e) {
            System.out.println(
                ">>> Institute PHOTO error: "
                + e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Upload failed: " + e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 DELETE /api/institute/photo
    // ===================================================
    @DeleteMapping("/api/institute/photo")
    public ResponseEntity<?> deletePhoto(
            Authentication auth,
            @RequestBody Map<String, String> request) {
        try {
            if (auth == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(
                        "Not authenticated!"));
            }

            BusinessUser owner = getOwner(auth.getName());
            String photoUrl = request.get("photoUrl");

            Institute institute = instituteRepository
                .findByOwnerId(owner.getId())
                .orElseThrow(() ->
                    new RuntimeException(
                        "Institute not found"));

            String existing = institute.getPhotos();
            if (existing != null) {
                List<String> photos = new ArrayList<>(
                    Arrays.asList(existing.split(",")));
                photos.remove(photoUrl);
                institute.setPhotos(
                    String.join(",", photos));
                instituteRepository.save(institute);
                cloudinaryService.deletePhoto(
                    cloudinaryService
                        .getPublicIdFromUrl(photoUrl));
            }

            return ResponseEntity.ok(
                ApiResponse.success("Photo deleted! ✅"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // Helper - set institute fields from request
    // ===================================================
    private void setIfNotNull(Institute inst,
            Map<String, Object> req) {
        if (req.get("instituteName") != null)
            inst.setInstituteName(
                req.get("instituteName").toString());
        if (req.get("phone") != null)
            inst.setPhone(req.get("phone").toString());
        if (req.get("address") != null)
            inst.setAddress(req.get("address").toString());
        if (req.get("city") != null)
            inst.setCity(req.get("city").toString());
        if (req.get("googleMapsLink") != null)
            inst.setGoogleMapsLink(
                req.get("googleMapsLink").toString());
        if (req.get("website") != null)
            inst.setWebsite(req.get("website").toString());
        if (req.get("description") != null)
            inst.setDescription(
                req.get("description").toString());
        if (req.get("instituteType") != null)
            inst.setInstituteType(
                req.get("instituteType").toString());
        if (req.get("courses") != null)
            inst.setCourses(req.get("courses").toString());
        if (req.get("feeRange") != null)
            inst.setFeeRange(
                req.get("feeRange").toString());
        if (req.get("batchTimings") != null)
            inst.setBatchTimings(
                req.get("batchTimings").toString());
        if (req.get("facilities") != null)
            inst.setFacilities(
                req.get("facilities").toString());
        if (req.get("achievements") != null)
            inst.setAchievements(
                req.get("achievements").toString());
        if (req.get("faculty") != null)
            inst.setFaculty(req.get("faculty").toString());
        if (req.get("availableSeats") != null) {
            try {
                inst.setAvailableSeats(Integer.parseInt(
                    req.get("availableSeats").toString()));
            } catch (Exception ignored) {}
        }
    }

    // ===================================================
    // Helper - get business user by email
    // ===================================================
    private BusinessUser getOwner(String email) {
        System.out.println(
            ">>> Looking for owner: " + email);

        if (email == null || email.isEmpty()) {
            throw new RuntimeException(
                "Email is null - not authenticated!");
        }

        return businessUserRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException(
                    "Owner not found for: " + email
                    + ". Please login again."));
    }
}





