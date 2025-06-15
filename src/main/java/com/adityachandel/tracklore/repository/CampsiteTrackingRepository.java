package com.adityachandel.tracklore.repository;

import com.adityachandel.tracklore.model.entity.CampsiteTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampsiteTrackingRepository extends JpaRepository<CampsiteTrackingEntity, Long> {

    List<CampsiteTrackingEntity> findByCampgroundId(String campgroundId);

    @Modifying
    void deleteAllByCampgroundId(String campgroundId);

    boolean existsByCampgroundIdAndCampsiteIdAndTrackedDate(String campgroundId, String campsiteId, LocalDate trackedDate);
}
