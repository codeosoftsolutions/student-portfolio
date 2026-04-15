

package com.studenttap.repository;

import com.studenttap.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository
        extends JpaRepository<Advertisement, Long> {

    // Get all active ads
    List<Advertisement> findByIsActiveTrueOrderByDisplayOrderAsc();

    // Get ads by target
    List<Advertisement> findByTargetAudienceAndIsActiveTrue(
        String targetAudience);

    // Get ads for specific audience + ALL
    List<Advertisement> findByTargetAudienceInAndIsActiveTrue(
        List<String> audiences);
}