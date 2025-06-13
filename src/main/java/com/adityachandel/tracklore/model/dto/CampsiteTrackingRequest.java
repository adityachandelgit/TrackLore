package com.adityachandel.tracklore.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CampsiteTrackingRequest {
    private String campgroundId;

    private List<TrackedCampsite> campsites;

    @Data
    public static class TrackedCampsite {
        private String campsiteId;
        private List<LocalDate> dates;
    }
}