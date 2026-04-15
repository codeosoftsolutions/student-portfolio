

package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.repository.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@CrossOrigin(origins = "*")
public class AdsPublicController {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    // ===================================================
    // 🌐 GET /api/public/ads
    // Returns active ads for all pages
    // ===================================================
    @GetMapping("/api/public/ads")
    public ResponseEntity<?> getActiveAds(
            @RequestParam(value = "target",
                defaultValue = "ALL")
            String target) {
        try {
            var ads = advertisementRepository
                .findByTargetAudienceInAndIsActiveTrue(
                    Arrays.asList(target, "ALL"));

            return ResponseEntity.ok(
                ApiResponse.success("Ads", ads));

        } catch (Exception e) {
            // Return empty list if no ads
            return ResponseEntity.ok(
                ApiResponse.success("Ads",
                    new java.util.ArrayList<>()));
        }
    }
}