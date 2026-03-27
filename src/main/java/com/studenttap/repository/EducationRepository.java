


package com.studenttap.repository;
 
import com.studenttap.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
 
@Repository
public interface EducationRepository
        extends JpaRepository<Education, Long> {
 
    // Get all education records of a student
    // ordered by display_order
    List<Education> findByStudentIdOrderByDisplayOrderAsc(
        Long studentId);
 
    // Find specific education by type
    // e.g. findByStudentIdAndEducationType(1L, "10TH")
    java.util.Optional<Education>
        findByStudentIdAndEducationType(
            Long studentId, String educationType);
}