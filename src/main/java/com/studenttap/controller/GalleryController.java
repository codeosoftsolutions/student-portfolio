


package com.studenttap.controller;
 
import com.studenttap.dto.ApiResponse;
import com.studenttap.service.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
 
@RestController
@CrossOrigin(origins = "*")
public class GalleryController {
 
    @Autowired
    private GalleryService galleryService;
 
    // 🔒 POST /api/student/gallery
    // Upload one photo to gallery
    @PostMapping("/api/student/gallery")
    public ResponseEntity<?> uploadPhoto(
            Authentication auth,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "caption",
                required = false,
                defaultValue = "") String caption) {
        try {
            var photo = galleryService.uploadPhoto(
                auth.getName(), file, caption);
 
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Photo uploaded to gallery!"
                        + photo.getPhotoPath()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // 🔒 GET /api/student/gallery
    // Get all gallery photos of logged-in student
    @GetMapping("/api/student/gallery")
    public ResponseEntity<?> getMyGallery(
            Authentication auth) {
        try {
            var photos = galleryService
                .getGallery(auth.getName());
            return ResponseEntity.ok(
                ApiResponse.success(
                    "Gallery fetched", photos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // 🔒 DELETE /api/student/gallery/{id}
    // Delete one photo from gallery
    @DeleteMapping("/api/student/gallery/{id}")
    public ResponseEntity<?> deletePhoto(
            Authentication auth,
            @PathVariable Long id) {
        try {
            galleryService.deletePhoto(auth.getName(), id);
            return ResponseEntity.ok(
                ApiResponse.success("Photo deleted!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
 
    // 🌐 GET /api/public/gallery/{username}
    // Public gallery - visible on portfolio page
  /*  @GetMapping("/api/public/gallery/{username}")
    public ResponseEntity<?> getPublicGallery(
            @PathVariable String username) {
        try {
            // Get student first then gallery
            return ResponseEntity.ok(
                ApiResponse.success("Gallery", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }*/
}
 
 