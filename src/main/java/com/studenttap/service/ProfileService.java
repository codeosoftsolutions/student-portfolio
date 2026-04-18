


package com.studenttap.service;

import com.studenttap.dto.PersonalDetailsRequest;

import com.studenttap.dto.ProfileRequest;
import com.studenttap.dto.PublicProfileResponse;
import com.studenttap.model.PersonalDetails;
import com.studenttap.model.Student;
import com.studenttap.repository.PersonalDetailsRepository;
import com.studenttap.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PersonalDetailsRepository personalDetailsRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // ===================================================
    // GET logged-in student details
    // ===================================================
    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Student not found!"));
    }

    // ===================================================
    // UPDATE basic profile info
    // (name, bio, designation, contact, social links)
    // ===================================================
    public Student updateProfile(String email, ProfileRequest request) {

        Student student = getStudentByEmail(email);

        // Update only fields that are not null
        if (request.getFullName() != null)
            student.setFullName(request.getFullName());

        if (request.getDesignation() != null)
            student.setDesignation(request.getDesignation());

        if (request.getBio() != null)
            student.setBio(request.getBio());

        if (request.getPhone() != null)
            student.setPhone(request.getPhone());

        if (request.getAlternatePhone() != null)
            student.setAlternatePhone(request.getAlternatePhone());

        if (request.getEmailPublic() != null)
            student.setEmailPublic(request.getEmailPublic());

        if (request.getAddress() != null)
            student.setAddress(request.getAddress());

        if (request.getGoogleMapsLink() != null)
            student.setGoogleMapsLink(request.getGoogleMapsLink());

        if (request.getLinkedinUrl() != null)
            student.setLinkedinUrl(request.getLinkedinUrl());

        if (request.getGithubUrl() != null)
            student.setGithubUrl(request.getGithubUrl());

        if (request.getWebsiteUrl() != null)
            student.setWebsiteUrl(request.getWebsiteUrl());

        if (request.getIsFresher() != null)
            student.setIsFresher(request.getIsFresher());

        return studentRepository.save(student);
    }

    // ===================================================
    // UPLOAD profile photo
    // ===================================================
    public String uploadProfilePhoto(String email,
                                     MultipartFile file) throws IOException {

        Student student = getStudentByEmail(email);

        String fileName = saveFile(file, "profiles");
        student.setProfilePhoto(fileName);
        studentRepository.save(student);

        return fileName;
    }

    // ===================================================
    // UPLOAD cover photo
    // ===================================================
    public String uploadCoverPhoto(String email,
                                   MultipartFile file) throws IOException {

        Student student = getStudentByEmail(email);

        String fileName = saveFile(file, "covers");
        student.setCoverPhoto(fileName);
        studentRepository.save(student);

        return fileName;
    }

    // ===================================================
    // SAVE or UPDATE personal details
    // (DOB, gender, skills, languages etc.)
    // ===================================================
    public PersonalDetails savePersonalDetails(String email,
                                               PersonalDetailsRequest request) {

        Student student = getStudentByEmail(email);

        // Check if personal details already exist
        PersonalDetails details = personalDetailsRepository
            .findByStudentId(student.getId())
            .orElse(new PersonalDetails());

        details.setStudent(student);

        if (request.getDateOfBirth() != null)
            details.setDateOfBirth(request.getDateOfBirth());

        if (request.getGender() != null)
            details.setGender(request.getGender());

        if (request.getNationality() != null)
            details.setNationality(request.getNationality());

        if (request.getLanguagesKnown() != null)
            details.setLanguagesKnown(request.getLanguagesKnown());

        if (request.getHobbies() != null)
            details.setHobbies(request.getHobbies());

        if (request.getSkills() != null)
            details.setSkills(request.getSkills());

        if (request.getTotalExperience() != null)
            details.setTotalExperience(request.getTotalExperience());

        if (request.getCareerObjective() != null)
            details.setCareerObjective(request.getCareerObjective());

        return personalDetailsRepository.save(details);
    }

    // ===================================================
    // GET PUBLIC profile by username
    // This is called when NFC card is tapped
    // Returns FULL profile for public view
    // ===================================================
    public PublicProfileResponse getPublicProfile(String username) {

        Student student = studentRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new RuntimeException("Profile not found!"));

        if (!student.getIsActive()) {
            throw new RuntimeException("This profile is not active.");
        }

        PublicProfileResponse response = new PublicProfileResponse();

        // Basic info
        response.setUsername(student.getUsername());
        response.setFullName(student.getFullName());
        response.setDesignation(student.getDesignation());
        response.setBio(student.getBio());
        response.setIsFresher(student.getIsFresher());

        // Photo URLs
        if (student.getProfilePhoto() != null)
            response.setProfilePhoto("/uploads/profiles/"
                + student.getProfilePhoto());

        if (student.getCoverPhoto() != null)
            response.setCoverPhoto("/uploads/covers/"
                + student.getCoverPhoto());

        // Contact
        response.setPhone(student.getPhone());
        response.setAlternatePhone(student.getAlternatePhone());
        response.setEmailPublic(student.getEmailPublic());
        response.setAddress(student.getAddress());
        response.setGoogleMapsLink(student.getGoogleMapsLink());

        // Social
        response.setLinkedinUrl(student.getLinkedinUrl());
        response.setGithubUrl(student.getGithubUrl());
        response.setWebsiteUrl(student.getWebsiteUrl());

        // Personal details
        personalDetailsRepository
            .findByStudentId(student.getId())
            .ifPresent(details -> {
                response.setDateOfBirth(details.getDateOfBirth());
                response.setGender(details.getGender());
                response.setNationality(details.getNationality());
                response.setLanguagesKnown(details.getLanguagesKnown());
                response.setHobbies(details.getHobbies());
                response.setSkills(details.getSkills());
                response.setTotalExperience(details.getTotalExperience());
                response.setCareerObjective(details.getCareerObjective());
            });

        return response;
    }

    // ===================================================
    // HELPER - Save uploaded file to disk
    // ===================================================
    private String saveFile(MultipartFile file,
                            String subFolder) throws IOException {

        // Create folder if not exists
        Path folderPath = Paths.get(uploadDir, subFolder);
        Files.createDirectories(folderPath);

        // Generate unique file name
        String originalName = file.getOriginalFilename();
        String extension = originalName
            .substring(originalName.lastIndexOf("."));
        String uniqueName = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = folderPath.resolve(uniqueName);
        Files.copy(file.getInputStream(), filePath);

        return uniqueName;
    }
}