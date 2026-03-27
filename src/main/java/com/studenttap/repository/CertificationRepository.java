


package com.studenttap.repository;
 
import com.studenttap.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
 
@Repository
public interface CertificationRepository
        extends JpaRepository<Certification, Long> {
 
    // Get all certifications of a student
    List<Certification> findByStudentId(Long studentId);
 
    // Delete a certification by id and student id
    // (student can only delete their own)
    void deleteByIdAndStudentId(Long id, Long studentId);
}
 