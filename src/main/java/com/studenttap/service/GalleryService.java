


/*
package com.studenttap.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.studenttap.model.GalleryPhoto;
import com.studenttap.model.Student;
import com.studenttap.repository.GalleryRepository;
import com.studenttap.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private Cloudinary cloudinary;

    private static final int MAX_PHOTOS = 20;

    // ===================================================
    // UPLOAD PHOTO (Cloudinary version ✅)
    // ===================================================
    public GalleryPhoto uploadPhoto(
            String email,
            MultipartFile file,
            String caption) throws IOException {

        Student student = getStudent(email);

        long count = galleryRepository
                .countByStudentId(student.getId());

        if (count >= MAX_PHOTOS) {
            throw new RuntimeException(
                    "Maximum " + MAX_PHOTOS + " photos allowed!");
        }

        // Validate image
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files allowed!");
        }

        // ✅ Upload to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.emptyMap()
        );

        String imageUrl = uploadResult.get("secure_url").toString();

        // Save to DB
        GalleryPhoto photo = new GalleryPhoto();
        photo.setStudent(student);
        photo.setPhotoPath(imageUrl); // ✅ FULL URL
        photo.setCaption(caption);
        photo.setDisplayOrder((int) count + 1);

        return galleryRepository.save(photo);
    }

    // ===================================================
    public List<GalleryPhoto> getGallery(String email) {
        Student student = getStudent(email);
        return galleryRepository
                .findByStudentIdOrderByDisplayOrderAsc(student.getId());
    }

    public List<GalleryPhoto> getGalleryByStudentId(Long studentId) {
        return galleryRepository
                .findByStudentIdOrderByDisplayOrderAsc(studentId);
    }

    // ===================================================
    @Transactional
    public void deletePhoto(String email, Long photoId) {

        Student student = getStudent(email);

        GalleryPhoto photo = galleryRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found!"));

        if (!photo.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Unauthorized!");
        }

        // ❌ No local file delete needed anymore
        galleryRepository.deleteById(photoId);
    }

    private Student getStudent(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found!"));
    }
}*/


package com.studenttap.service;
 
import com.studenttap.model.GalleryPhoto;

import com.studenttap.model.Student;
import com.studenttap.repository.GalleryRepository;
import com.studenttap.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
 
@Service
public class GalleryService {
 
    @Autowired
    private GalleryRepository galleryRepository;
 
    @Autowired
    private StudentRepository studentRepository;
 
    @Value("${app.upload.dir}")
    private String uploadDir;
 
    // Max photos allowed per student
    private static final int MAX_PHOTOS = 20;
 
    // ===================================================
    // UPLOAD one photo to gallery
    // ===================================================
    public GalleryPhoto uploadPhoto(
            String email,
            MultipartFile file,
            String caption) throws IOException {
 
        Student student = getStudent(email);
 
        // Check max photos limit
        long count = galleryRepository
            .countByStudentId(student.getId());
        if (count >= MAX_PHOTOS) {
            throw new RuntimeException(
                "Maximum " + MAX_PHOTOS
                + " photos allowed in gallery!");
        }
 
        // Validate image file
        String contentType = file.getContentType();
        if (contentType == null
                || !contentType.startsWith("image/")) {
            throw new RuntimeException(
                "Only image files are allowed!");
        }
 
        // Save file to disk
        Path folderPath = Paths.get(uploadDir, "gallery");
        Files.createDirectories(folderPath);
 
        String originalName = file.getOriginalFilename();
        String extension = originalName
            .substring(originalName.lastIndexOf("."));
        String uniqueName = UUID.randomUUID() + extension;
 
        Files.copy(file.getInputStream(),
            folderPath.resolve(uniqueName));
 
        // Save to database
        GalleryPhoto photo = new GalleryPhoto();
        photo.setStudent(student);
        photo.setPhotoPath(uniqueName);
        photo.setCaption(caption);
        photo.setDisplayOrder((int) count + 1);
 
        return galleryRepository.save(photo);
    }
 
    // ===================================================
    // GET all gallery photos of student
    // ===================================================
    public List<GalleryPhoto> getGallery(String email) {
        Student student = getStudent(email);
        return galleryRepository
            .findByStudentIdOrderByDisplayOrderAsc(
                student.getId());
    }
 
    // ===================================================
    // GET gallery by studentId (for public page)
    // ===================================================
    public List<GalleryPhoto> getGalleryByStudentId(
            Long studentId) {
        return galleryRepository
            .findByStudentIdOrderByDisplayOrderAsc(studentId);
    }
 
    // ===================================================
    // DELETE one photo from gallery
    // ===================================================
    @Transactional
    public void deletePhoto(String email, Long photoId) {
        Student student = getStudent(email);
 
        GalleryPhoto photo = galleryRepository
            .findById(photoId)
            .orElseThrow(() ->
                new RuntimeException("Photo not found!"));
 
        // Security - only own photo
        if (!photo.getStudent().getId()
                .equals(student.getId())) {
            throw new RuntimeException(
                "You can only delete your own photos!");
        }
 
        // Delete file from disk
        try {
            Path filePath = Paths.get(
                uploadDir, "gallery", photo.getPhotoPath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // File delete failed - still remove from DB
            System.out.println("Could not delete file: "
                + e.getMessage());
        }
 
        galleryRepository.deleteById(photoId);
    }
 
    private Student getStudent(String email) {
        return studentRepository.findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Student not found!"));
    }
}

/*
package com.studenttap.service;

import com.studenttap.model.GalleryPhoto;
import com.studenttap.model.Student;
import com.studenttap.repository.GalleryRepository;
import com.studenttap.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ✅ Upload gallery photo to Cloudinary
    public GalleryPhoto uploadPhoto(
            String username,
            MultipartFile file,
            String caption) throws IOException {

        Student student = studentRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new RuntimeException("Student not found"));

        // Max 20 photos check
        long count = galleryRepository
            .countByStudentId(student.getId());
        if (count >= 20) {
            throw new RuntimeException(
                "Maximum 20 photos allowed!");
        }

        // ✅ Upload to Cloudinary - permanent storage!
        String photoUrl = cloudinaryService
            .uploadPhoto(file, "gallery");

        // Save Cloudinary URL to database
        GalleryPhoto photo = new GalleryPhoto();
        photo.setStudent(student);
        photo.setPhotoPath(photoUrl); // Full Cloudinary URL
        photo.setCaption(caption);

        return galleryRepository.save(photo);
    }

    // Get gallery photos for a student
    public List<GalleryPhoto> getGalleryByStudentId(
            Long studentId) {
        return galleryRepository
            .findByStudentId(studentId);
    }

    // Delete gallery photo
    public void deletePhoto(
            String username, Long photoId) {

        Student student = studentRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new RuntimeException("Student not found"));

        GalleryPhoto photo = galleryRepository
            .findById(photoId)
            .orElseThrow(() ->
                new RuntimeException("Photo not found"));

        if (!photo.getStudent().getId()
                .equals(student.getId())) {
            throw new RuntimeException("Not authorized!");
        }

        // Delete from Cloudinary
        String publicId = cloudinaryService
            .getPublicIdFromUrl(photo.getPhotoPath());
        if (publicId != null) {
            cloudinaryService.deletePhoto(publicId);
        }

        galleryRepository.delete(photo);
    }
    
    public List<GalleryPhoto> getGallery(String email) {
        Student student = getStudent(email);
        return galleryRepository
            .findByStudentIdOrderByDisplayOrderAsc(
                student.getId());
    }

    private Student getStudent(String email) {
        return studentRepository.findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Student not found!"));
    }
}
*/
	
 