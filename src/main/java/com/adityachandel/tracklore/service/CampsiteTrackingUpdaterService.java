package com.adityachandel.tracklore.service;

import com.adityachandel.tracklore.model.dto.CampsiteAvailabilityResponse.Campsite;
import com.adityachandel.tracklore.model.entity.CampsiteTrackingEntity;
import com.adityachandel.tracklore.repository.CampsiteTrackingRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampsiteTrackingUpdaterService {

    private final CampsiteTrackingRepository repository;
    private final CampsiteAvailabilityService availabilityService;
    private final TrackloreEmailService emailService;

    private static final String NOTIFY_EMAIL = "aditya.chandel101@gmail.com";

    @PostConstruct
    public void onStartup() {
        log.info("Running campsite status refresh on startup...");
        refreshTrackedCampsiteStatuses();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void refreshTrackedCampsiteStatuses() {
        log.info("Starting scheduled refresh of campsite statuses");

        List<CampsiteTrackingEntity> allTracked = repository.findAll();
        var grouped = groupByCampgroundAndMonth(allTracked);
        for (var campgroundEntry : grouped.entrySet()) {
            String campgroundId = campgroundEntry.getKey();
            for (var monthEntry : campgroundEntry.getValue().entrySet()) {
                String yearMonth = monthEntry.getKey();
                String startDateIso = yearMonth + "-01T00:00:00.000Z";
                try {
                    Thread.sleep(5000);
                    var response = availabilityService.fetchCampsiteAvailability(campgroundId, startDateIso);
                    if (response == null || response.getCampsites() == null) {
                        log.warn("No availability for campground {} month {}", campgroundId, yearMonth);
                        continue;
                    }
                    handleMonthUpdate(campgroundId, monthEntry.getValue(), response.getCampsites());
                    log.info("Updated campground {} month {}", campgroundId, yearMonth);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("Tracking update interrupted");
                    return;
                } catch (Exception e) {
                    log.error("Error updating campground {} month {}: {}", campgroundId, yearMonth, e.getMessage(), e);
                }
            }
        }

        log.info("Scheduled refresh completed");
    }

    private Map<String, Map<String, List<CampsiteTrackingEntity>>> groupByCampgroundAndMonth(List<CampsiteTrackingEntity> tracked) {
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return tracked.stream().collect(Collectors.groupingBy(
                CampsiteTrackingEntity::getCampgroundId,
                Collectors.groupingBy(e -> e.getTrackedDate().format(monthFormatter))
        ));
    }

    private void handleMonthUpdate(String campgroundId, List<CampsiteTrackingEntity> entities, Map<String, Campsite> campsiteMap) {
        List<CampsiteTrackingEntity> becameAvailableList = new ArrayList<>();

        for (var entity : entities) {
            Campsite campsite = campsiteMap.get(entity.getCampsiteId());
            if (campsite == null || campsite.getAvailabilities() == null) continue;

            String dateKey = entity.getTrackedDate() + "T00:00:00Z";
            String latestStatus = campsite.getAvailabilities().get(dateKey);
            Integer latestQuantity = campsite.getQuantities() != null
                    ? campsite.getQuantities().getOrDefault(dateKey, 0)
                    : null;

            boolean updated = false;
            boolean becameAvailable = false;

            if (!Objects.equals(latestStatus, entity.getStatus())) {
                becameAvailable = isNowAvailable(latestStatus) && !isNowAvailable(entity.getStatus());
                entity.setStatus(latestStatus);
                updated = true;
            }

            if (!Objects.equals(latestQuantity, entity.getQuantity())) {
                entity.setQuantity(latestQuantity);
                updated = true;
            }

            updated |= updateField(entity::getSite, entity::setSite, campsite.getSite());
            updated |= updateField(entity::getLoop, entity::setLoop, campsite.getLoop());
            updated |= updateField(entity::getCampsiteType, entity::setCampsiteType, campsite.getCampsiteType());
            updated |= updateField(entity::getTypeOfUse, entity::setTypeOfUse, campsite.getTypeOfUse());
            updated |= updateField(entity::getReserveType, entity::setReserveType, campsite.getCampsiteReserveType());
            updated |= updateField(entity::getMaxPeople, entity::setMaxPeople, campsite.getMaxNumPeople());

            if (updated) {
                repository.save(entity);
                if (becameAvailable) {
                    becameAvailableList.add(entity);
                }
            }
        }

        if (!becameAvailableList.isEmpty()) {
            sendBatchAvailabilityAlert(campgroundId, becameAvailableList);
        }
    }

    private <T> boolean updateField(Supplier<T> getter, Consumer<T> setter, T newValue) {
        if (!Objects.equals(getter.get(), newValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    private boolean isNowAvailable(String status) {
        return status != null && status.equalsIgnoreCase("Available");
    }

    private void sendBatchAvailabilityAlert(String campgroundId, List<CampsiteTrackingEntity> availableEntities) {
        String subject = "🟢 Campsites Available at Campground " + campgroundId;
        StringBuilder body = new StringBuilder("One or more campsites you’re tracking just became available!\n\n");

        for (CampsiteTrackingEntity entity : availableEntities) {
            body.append(String.format("""
                • Site: %s (%s)
                  Date: %s
                  Type: %s
                  Status: %s
                  Quantity: %s

                """,
                    entity.getSite(),
                    entity.getLoop(),
                    entity.getTrackedDate(),
                    entity.getCampsiteType(),
                    entity.getStatus(),
                    entity.getQuantity()
            ));
        }

        body.append("👉 Book now: https://www.recreation.gov/camping/campgrounds/").append(campgroundId);
        emailService.sendReportEmail(NOTIFY_EMAIL, subject, body.toString());
    }
}