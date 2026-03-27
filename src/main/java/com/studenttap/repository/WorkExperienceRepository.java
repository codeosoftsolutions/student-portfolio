


package com.studenttap.repository;
 
import com.studenttap.model.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
 
@Repository
public interface WorkExperienceRepository
        extends JpaRepository<WorkExperience, Long> {
 
    // Get all work experience of a student
    // ordered by display_order
    List<WorkExperience> findByStudentIdOrderByDisplayOrderAsc(
        Long studentId);
 
    // Delete experience by id and student id
    void deleteByIdAndStudentId(Long id, Long studentId);
}