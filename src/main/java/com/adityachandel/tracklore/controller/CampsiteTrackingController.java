package com.adityachandel.tracklore.controller;

import com.adityachandel.tracklore.model.dto.CampsiteTrackingRequest;
import com.adityachandel.tracklore.model.entity.CampsiteTrackingEntity;
import com.adityachandel.tracklore.service.CampsiteTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class CampsiteTrackingController {

    private final CampsiteTrackingService service;

    @PostMapping
    public void trackSelected(@RequestBody CampsiteTrackingRequest request) {
        service.trackSelectedCampsites(request);
    }

    @GetMapping("/{campgroundId}")
    public List<CampsiteTrackingEntity> list(@PathVariable String campgroundId) {
        return service.getByCampground(campgroundId);
    }

    @DeleteMapping("/{campgroundId}")
    public void deleteByCampground(@PathVariable String campgroundId) {
        service.deleteAllByCampground(campgroundId);
    }
}