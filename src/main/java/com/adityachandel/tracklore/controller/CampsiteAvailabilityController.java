package com.adityachandel.tracklore.controller;


import com.adityachandel.tracklore.model.dto.CampsiteAvailabilityResponse;
import com.adityachandel.tracklore.service.CampsiteAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/campsites")
@RequiredArgsConstructor
public class CampsiteAvailabilityController {

    private final CampsiteAvailabilityService availabilityService;

    @GetMapping("/availability")
    public ResponseEntity<CampsiteAvailabilityResponse> getAvailability(@RequestParam String campgroundId, @RequestParam String startDate) {
        try {
            CampsiteAvailabilityResponse response = availabilityService.fetchCampsiteAvailability(campgroundId, startDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}