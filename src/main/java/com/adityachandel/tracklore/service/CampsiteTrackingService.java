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
            List<CampsiteTrackingEntity> newRecords = new ArrayList<>();

            for (CampsiteTrackingRequest.TrackedCampsite tc : request.getCampsites()) {
                for (LocalDate date : tc.getDates()) {
                    boolean exists = repository.existsByCampgroundIdAndCampsiteIdAndTrackedDate(
                            request.getCampgroundId(), tc.getCampsiteId(), date
                    );

                    if (!exists) {
                        CampsiteTrackingEntity entity = CampsiteTrackingEntity.builder()
                                .campgroundId(request.getCampgroundId())
                                .campsiteId(tc.getCampsiteId())
                                .trackedDate(date)
                                .build();
                        newRecords.add(entity);
                    }
                }
            }

            repository.saveAll(newRecords);
            log.info("Saved {} tracking records for campground {}", newRecords.size(), request.getCampgroundId());

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