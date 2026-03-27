


package com.studenttap.repository;

import com.studenttap.model.PersonalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PersonalDetailsRepository
        extends JpaRepository<PersonalDetails, Long> {

    // Find personal details by student id
    Optional<PersonalDetails> findByStudentId(Long studentId);
}