


package com.studenttap.repository;
 
import com.studenttap.model.GalleryPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
 
@Repository
public interface GalleryRepository
        extends JpaRepository<GalleryPhoto, Long> {
 
    // Get all photos of a student ordered by display_order
    List<GalleryPhoto> findByStudentIdOrderByDisplayOrderAsc(
        Long studentId);
 
    // Count total photos (limit gallery size)
    long countByStudentId(Long studentId);
 
    // Delete by id and studentId (security check)
    void deleteByIdAndStudentId(Long id, Long studentId);

	List<GalleryPhoto> findByStudentId(Long studentId);

	
}