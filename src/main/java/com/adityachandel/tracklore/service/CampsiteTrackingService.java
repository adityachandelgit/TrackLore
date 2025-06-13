package com.adityachandel.tracklore.service;

import com.adityachandel.tracklore.model.dto.CampsiteTrackingRequest;
import com.adityachandel.tracklore.model.entity.CampsiteTrackingEntity;
import com.adityachandel.tracklore.repository.CampsiteTrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampsiteTrackingService {

    private final CampsiteTrackingRepository repository;

    public Map<String, List<CampsiteTrackingEntity>> getAllGroupedByCampground() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(CampsiteTrackingEntity::getCampgroundId));
    }

    public void trackSelectedCampsites(CampsiteTrackingRequest request) {
        try {
            List<CampsiteTrackingEntity> records = new ArrayList<>();

            for (CampsiteTrackingRequest.TrackedCampsite tc : request.getCampsites()) {
                for (LocalDate date : tc.getDates()) {
                    CampsiteTrackingEntity entity = CampsiteTrackingEntity.builder()
                            .campgroundId(request.getCampgroundId())
                            .campsiteId(tc.getCampsiteId())
                            .trackedDate(date)
                            .build();
                    records.add(entity);
                }
            }

            repository.saveAll(records);
            log.info("Saved {} tracking records for campground {}", records.size(), request.getCampgroundId());

        } catch (Exception e) {
            log.error("Failed to save selected campsites for campground {}: {}", request.getCampgroundId(), e.getMessage(), e);
        }
    }

    public List<CampsiteTrackingEntity> getByCampground(String campgroundId) {
        return repository.findByCampgroundId(campgroundId);
    }
    public void deleteAllByCampground(String campgroundId) {
        repository.deleteAllByCampgroundId(campgroundId);
    }

}