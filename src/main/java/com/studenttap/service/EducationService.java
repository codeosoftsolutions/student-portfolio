

package com.studenttap.service;

import com.studenttap.dto.CertificationRequest;

import com.studenttap.dto.EducationRequest;
import com.studenttap.dto.WorkExperienceRequest;
import com.studenttap.model.Certification;
import com.studenttap.model.Education;
import com.studenttap.model.Student;
import com.studenttap.model.WorkExperience;
import com.studenttap.repository.CertificationRepository;
import com.studenttap.repository.EducationRepository;
import com.studenttap.repository.StudentRepository;
import com.studenttap.repository.WorkExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

@Service
public class EducationService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private WorkExperienceRepository workExperienceRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // ===================================================
    // Helper - get student from email
    // ===================================================
    private Student getStudent(String email) {
        return studentRepository.findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Student not found!"));
    }

    // ===================================================
    // EDUCATION - Save or Update
    // If same educationType exists, update it
    // Otherwise create new record
    // ===================================================
    public Education saveEducation(String email,
                                   EducationRequest request) {
        Student student = getStudent(email);

        // Check if this education type already exists
        Education education = educationRepository
            .findByStudentIdAndEducationType(
                student.getId(),
                request.getEducationType().toUpperCase())
            .orElse(new Education());

        education.setStudent(student);
        education.setEducationType(
            request.getEducationType().toUpperCase());
        education.setInstitutionName(
            request.getInstitutionName());

        if (request.getBoardUniversity() != null)
            education.setBoardUniversity(
                request.getBoardUniversity());

        if (request.getFieldOfStudy() != null)
            education.setFieldOfStudy(
                request.getFieldOfStudy());

        if (request.getPercentageCgpa() != null)
            education.setPercentageCgpa(
                request.getPercentageCgpa());

        if (request.getYearOfPassing() != null)
            education.setYearOfPassing(
                request.getYearOfPassing());

        if (request.getDisplayOrder() != null)
            education.setDisplayOrder(
                request.getDisplayOrder());

        return educationRepository.save(education);
    }

    // ===================================================
    // EDUCATION - Get all by student
    // ===================================================
    public List<Education> getAllEducation(String email) {
        Student student = getStudent(email);
        return educationRepository
            .findByStudentIdOrderByDisplayOrderAsc(
                student.getId());
    }

    // ===================================================
    // EDUCATION - Get all by studentId (for public page)
    // ===================================================
    public List<Education> getEducationByStudentId(
            Long studentId) {
        return educationRepository
            .findByStudentIdOrderByDisplayOrderAsc(studentId);
    }

    // ===================================================
    // EDUCATION - Delete one record
    // ===================================================
    @Transactional
    public void deleteEducation(String email, Long educationId) {
        Student student = getStudent(email);

        Education education = educationRepository
            .findById(educationId)
            .orElseThrow(() ->
                new RuntimeException("Education record not found!"));

        // Make sure student can only delete their own record
        if (!education.getStudent().getId()
                .equals(student.getId())) {
            throw new RuntimeException(
                "You can only delete your own records!");
        }

        educationRepository.deleteById(educationId);
    }

    // ===================================================
    // CERTIFICATION - Add new
    // ===================================================
    public Certification addCertification(
            String email,
            CertificationRequest request) {

        Student student = getStudent(email);

        Certification cert = new Certification();
        cert.setStudent(student);
        cert.setCourseName(request.getCourseName());
        cert.setIssuingOrg(request.getIssuingOrg());
        cert.setIssueDate(request.getIssueDate());
        cert.setCredentialUrl(request.getCredentialUrl());

        return certificationRepository.save(cert);
    }

    // ===================================================
    // CERTIFICATION - Upload certificate image
    // ===================================================
    public Certification uploadCertificateImage(
            String email,
            Long certId,
            MultipartFile file) throws IOException {

        Student student = getStudent(email);

        Certification cert = certificationRepository
            .findById(certId)
            .orElseThrow(() ->
                new RuntimeException("Certification not found!"));

        // Security check
        if (!cert.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Access denied!");
        }

        // Save image file
        Path folderPath = Paths.get(uploadDir, "certificates");
        Files.createDirectories(folderPath);

        String originalName = file.getOriginalFilename();
        String extension = originalName
            .substring(originalName.lastIndexOf("."));
        String uniqueName = UUID.randomUUID() + extension;

        Files.copy(file.getInputStream(),
            folderPath.resolve(uniqueName));

        cert.setCertificateImg(uniqueName);
        return certificationRepository.save(cert);
    }

    // ===================================================
    // CERTIFICATION - Get all by student
    // ===================================================
    public List<Certification> getAllCertifications(
            String email) {
        Student student = getStudent(email);
        return certificationRepository
            .findByStudentId(student.getId());
    }

    // ===================================================
    // CERTIFICATION - Get all by studentId (public page)
    // ===================================================
    public List<Certification> getCertificationsByStudentId(
            Long studentId) {
        return certificationRepository
            .findByStudentId(studentId);
    }

    // ===================================================
    // CERTIFICATION - Delete
    // ===================================================
    @Transactional
    public void deleteCertification(String email,
                                    Long certId) {
        Student student = getStudent(email);
        certificationRepository
            .deleteByIdAndStudentId(certId, student.getId());
    }

    // ===================================================
    // WORK EXPERIENCE - Add new (for experienced students)
    // ===================================================
    public WorkExperience addWorkExperience(
            String email,
            WorkExperienceRequest request) {

        Student student = getStudent(email);

        WorkExperience exp = new WorkExperience();
        exp.setStudent(student);
        exp.setCompanyName(request.getCompanyName());
        exp.setJobTitle(request.getJobTitle());
        exp.setJobLocation(request.getJobLocation());
        exp.setStartDate(request.getStartDate());
        exp.setEndDate(request.getEndDate());
        exp.setIsCurrent(
            request.getIsCurrent() != null
                ? request.getIsCurrent() : false);
        exp.setDescription(request.getDescription());
        exp.setDisplayOrder(
            request.getDisplayOrder() != null
                ? request.getDisplayOrder() : 0);

        return workExperienceRepository.save(exp);
    }

    // ===================================================
    // WORK EXPERIENCE - Get all by student
    // ===================================================
    public List<WorkExperience> getAllWorkExperience(
            String email) {
        Student student = getStudent(email);
        return workExperienceRepository
            .findByStudentIdOrderByDisplayOrderAsc(
                student.getId());
    }

    // ===================================================
    // WORK EXPERIENCE - Get all by studentId (public page)
    // ===================================================
    public List<WorkExperience> getWorkExperienceByStudentId(
            Long studentId) {
        return workExperienceRepository
            .findByStudentIdOrderByDisplayOrderAsc(studentId);
    }

    // ===================================================
    // WORK EXPERIENCE - Delete
    // ===================================================
    @Transactional
    public void deleteWorkExperience(String email,
                                     Long expId) {
        Student student = getStudent(email);
        workExperienceRepository
            .deleteByIdAndStudentId(expId, student.getId());
    }
}