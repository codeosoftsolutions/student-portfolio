

package com.studenttap.repository;
 
import com.studenttap.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
 
@Repository
public interface ResumeRepository
        extends JpaRepository<Resume, Long> {
 
    // Each student has only one resume
    Optional<Resume> findByStudentId(Long studentId);
 
    // Find by student email through join
    Optional<Resume> findByStudentEmail(String email);

	
    
    
}