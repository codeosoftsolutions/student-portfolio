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