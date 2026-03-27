

package com.studenttap.service;
 
import com.studenttap.model.Resume;
import com.studenttap.model.Student;
import com.studenttap.repository.ResumeRepository;
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
public class ResumeService {
 
    @Autowired
    private ResumeRepository resumeRepository;
 
    @Autowired
    private StudentRepository studentRepository;
 
    @Value("${app.upload.dir}")
    private String uploadDir;
 
    // ===================================================
    // UPLOAD resume PDF
    // Student uploads their own resume file
    // ===================================================
    public Resume uploadResume(
            String email,
            MultipartFile file,
            String templateUsed) throws IOException {
 
        Student student = getStudent(email);
 
        // Validate - only PDF allowed
        String contentType = file.getContentType();
        if (contentType == null
                || !contentType.equals(
                    "application/pdf")) {
            throw new RuntimeException(
                "Only PDF files are allowed for resume!");
        }
 
        // Save file to disk
        Path folderPath = Paths.get(uploadDir, "resumes");
        Files.createDirectories(folderPath);
 
        String uniqueName = UUID.randomUUID() + ".pdf";
        Files.copy(file.getInputStream(),
            folderPath.resolve(uniqueName));
 
        // Delete old resume file if exists
        Resume resume = resumeRepository
            .findByStudentId(student.getId())
            .orElse(new Resume());
 
        if (resume.getResumePath() != null) {
            try {
                Path oldFile = Paths.get(
                    uploadDir, "resumes",
                    resume.getResumePath());
                Files.deleteIfExists(oldFile);
            } catch (IOException e) {
                System.out.println(
                    "Could not delete old resume: "
                    + e.getMessage());
            }
        }
 
        // Save/update in database
        resume.setStudent(student);
        resume.setResumePath(uniqueName);
        resume.setTemplateUsed(
            templateUsed != null
                ? templateUsed : "CUSTOM");
        resume.setIsDownloadable(true);
 
        return resumeRepository.save(resume);
    }
 
    // ===================================================
    // SELECT a resume template
    // Student picks template 1, 2, 3, or 4
    // ===================================================
    /*public Resume selectTemplate(
            String email, String templateName) {
 
        Student student = getStudent(email);
 
        Resume resume = resumeRepository
            .findByStudentId(student.getId())
            .orElse(new Resume());
 
        resume.setStudent(student);
        resume.setTemplateUsed(templateName);
        // No file - template will be generated on the fly
        resume.setIsDownloadable(true);
 
        return resumeRepository.save(resume);
    }
 */
    
    public Resume selectTemplate(String email, String templateName) {

        Student student = getStudent(email);

        Resume resume = resumeRepository
            .findByStudentId(student.getId())
            .orElseGet(() -> {
                Resume r = new Resume();
                r.setStudent(student);
                return r;
            });

        // ✅ VALIDATE TEMPLATE
        if (templateName == null || templateName.isEmpty()) {
            throw new RuntimeException("Invalid template name");
        }

        resume.setTemplateUsed(templateName);
        resume.setIsDownloadable(true);

        return resumeRepository.save(resume);
    }
    
    
    // ===================================================
    // GET resume info of student
    // ===================================================
    public Resume getResume(String email) {
        Student student = getStudent(email);
        return resumeRepository
            .findByStudentId(student.getId())
            .orElse(null);
    }
 
    // ===================================================
    // GET resume by studentId (for public page download)
    // ===================================================
    public Resume getResumeByStudentId(Long studentId) {
        return resumeRepository
            .findByStudentId(studentId)
            .orElse(null);
    }
 
    // ===================================================
    // TOGGLE downloadable - student can disable download
    // ===================================================
    public Resume toggleDownloadable(String email) {
        Student student = getStudent(email);
        Resume resume = resumeRepository
            .findByStudentId(student.getId())
            .orElseThrow(() ->
                new RuntimeException(
                    "No resume found! Please upload first."));
 
        resume.setIsDownloadable(!resume.getIsDownloadable());
        return resumeRepository.save(resume);
    }
 
    private Student getStudent(String email) {
        return studentRepository.findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("Student not found!"));
    }
}
 