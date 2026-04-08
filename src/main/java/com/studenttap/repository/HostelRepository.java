

package com.studenttap.repository;

import com.studenttap.model.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostelRepository
        extends JpaRepository<Hostel, Long> {

    // Find hostel by owner
    Optional<Hostel> findByOwnerId(Long ownerId);

    // Find all active hostels
    List<Hostel> findByIsActiveTrue();

    // Find by city
    List<Hostel> findByCityAndIsActiveTrue(String city);

    // Find by gender type
    List<Hostel> findByGenderTypeAndIsActiveTrue(
        String genderType);
}