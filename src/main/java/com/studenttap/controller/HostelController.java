package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.model.BusinessUser;
import com.studenttap.model.Hostel;
import com.studenttap.repository.BusinessUserRepository;
import com.studenttap.repository.HostelRepository;
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
public class HostelController {

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private BusinessUserRepository businessUserRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ===================================================
    // 🌐 PUBLIC - GET /api/public/hostels
    // Students see all hostels
    // ===================================================
    @GetMapping("/api/public/hostels")
    public ResponseEntity<?> getAllHostels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String gender) {
        try {
            List<Hostel> hostels;
            if (city != null && !city.isEmpty()) {
                hostels = hostelRepository
                    .findByCityAndIsActiveTrue(city);
            } else {
                hostels = hostelRepository
                    .findByIsActiveTrue();
            }
            return ResponseEntity.ok(
                ApiResponse.success("Hostels", hostels));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🌐 PUBLIC - GET /api/public/hostels/{id}
    // ===================================================
    @GetMapping("/api/public/hostels/{id}")
    public ResponseEntity<?> getHostelById(
            @PathVariable Long id) {
        try {
            Hostel hostel = hostelRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Hostel not found"));
            return ResponseEntity.ok(
                ApiResponse.success("Hostel", hostel));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 GET /api/hostel/my
    // Hostel owner sees their own hostel
    // ===================================================
    @GetMapping("/api/hostel/my")
    public ResponseEntity<?> getMyHostel(
            Authentication auth) {
        try {
            BusinessUser owner = getOwner(auth.getName());
            Hostel hostel = hostelRepository
                .findByOwnerId(owner.getId())
                .orElse(null);
            return ResponseEntity.ok(
                ApiResponse.success("My Hostel", hostel));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 POST /api/hostel/save
    // Save or update hostel details
    // ===================================================
    @PostMapping("/api/hostel/save")
    public ResponseEntity<?> saveHostel(
            Authentication auth,
            @RequestBody Map<String, Object> request) {
        try {
            BusinessUser owner = getOwner(auth.getName());

            // Get or create hostel
            Hostel hostel = hostelRepository
                .findByOwnerId(owner.getId())
                .orElse(new Hostel());

            hostel.setOwnerId(owner.getId());
            hostel.setOwnerName(owner.getFullName());
            hostel.setEmail(owner.getEmail());

            // Set fields from request
            if (request.get("hostelName") != null)
                hostel.setHostelName(
                    request.get("hostelName").toString());
            if (request.get("phone") != null)
                hostel.setPhone(
                    request.get("phone").toString());
            if (request.get("address") != null)
                hostel.setAddress(
                    request.get("address").toString());
            if (request.get("city") != null)
                hostel.setCity(
                    request.get("city").toString());
            if (request.get("googleMapsLink") != null)
                hostel.setGoogleMapsLink(
                    request.get("googleMapsLink").toString());
            if (request.get("description") != null)
                hostel.setDescription(
                    request.get("description").toString());
            if (request.get("genderType") != null)
                hostel.setGenderType(
                    request.get("genderType").toString());
            if (request.get("singleRoomPrice") != null)
                hostel.setSingleRoomPrice(
                    request.get("singleRoomPrice").toString());
            if (request.get("doubleSharePrice") != null)
                hostel.setDoubleSharePrice(
                    request.get("doubleSharePrice").toString());
            if (request.get("tripleSharePrice") != null)
                hostel.setTripleSharePrice(
                    request.get("tripleSharePrice").toString());
            if (request.get("fourSharePrice") != null)
                hostel.setFourSharePrice(
                    request.get("fourSharePrice").toString());
            if (request.get("amenities") != null)
                hostel.setAmenities(
                    request.get("amenities").toString());
            if (request.get("rules") != null)
                hostel.setRules(
                    request.get("rules").toString());
            if (request.get("timings") != null)
                hostel.setTimings(
                    request.get("timings").toString());
            if (request.get("totalRooms") != null)
                hostel.setTotalRooms(Integer.parseInt(
                    request.get("totalRooms").toString()));
            if (request.get("availableRooms") != null)
                hostel.setAvailableRooms(Integer.parseInt(
                    request.get("availableRooms").toString()));
            if (request.get("isAvailable") != null)
                hostel.setIsAvailable(Boolean.parseBoolean(
                    request.get("isAvailable").toString()));

            hostel.setUpdatedAt(LocalDateTime.now());
            hostelRepository.save(hostel);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Hostel details saved! ✅", hostel));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Save failed: " + e.getMessage()));
        }
    }

    // ===================================================
    // 🔒 POST /api/hostel/upload-photo
    // Upload hostel photo to Cloudinary
    // ===================================================
    @PostMapping("/api/hostel/upload-photo")
    public ResponseEntity<?> uploadPhoto(
            Authentication auth,
            @RequestParam("file") MultipartFile file) {
        try {
            BusinessUser owner = getOwner(auth.getName());

            // Upload to Cloudinary
            String photoUrl = cloudinaryService
                .uploadPhoto(file, "hostels");

            // Add to hostel photos list
            Hostel hostel = hostelRepository
                .findByOwnerId(owner.getId())
                .orElseThrow(() ->
                    new RuntimeException(
                        "Please save hostel details first!"));

            String existing = hostel.getPhotos();
            if (existing == null || existing.isEmpty()) {
                hostel.setPhotos(photoUrl);
            } else {
                // Max 10 photos
                List<String> photos = new ArrayList<>(
                    Arrays.asList(existing.split(",")));
                if (photos.size() >= 10) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(
                            "Maximum 10 photos allowed!"));
                }
                photos.add(photoUrl);
                hostel.setPhotos(String.join(",", photos));
            }

            hostelRepository.save(hostel);

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
    // 🔒 DELETE /api/hostel/photo
    // Delete a hostel photo
    // ===================================================
    @DeleteMapping("/api/hostel/photo")
    public ResponseEntity<?> deletePhoto(
            Authentication auth,
            @RequestBody Map<String, String> request) {
        try {
            BusinessUser owner = getOwner(auth.getName());
            String photoUrl = request.get("photoUrl");

            Hostel hostel = hostelRepository
                .findByOwnerId(owner.getId())
                .orElseThrow(() ->
                    new RuntimeException("Hostel not found"));

            String existing = hostel.getPhotos();
            if (existing != null) {
                List<String> photos = new ArrayList<>(
                    Arrays.asList(existing.split(",")));
                photos.remove(photoUrl);
                hostel.setPhotos(String.join(",", photos));
                hostelRepository.save(hostel);

                // Delete from Cloudinary
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

    // Helper — get business user by email
    private BusinessUser getOwner(String email) {
        return businessUserRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Owner not found"));
    }
}