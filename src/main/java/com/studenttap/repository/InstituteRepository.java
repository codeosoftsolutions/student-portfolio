package com.studenttap.repository;

import com.studenttap.model.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstituteRepository
        extends JpaRepository<Institute, Long> {

    Optional<Institute> findByOwnerId(Long ownerId);

    List<Institute> findByIsActiveTrue();

    List<Institute> findByCityAndIsActiveTrue(String city);

    List<Institute> findByInstituteTypeAndIsActiveTrue(
        String instituteType);
}