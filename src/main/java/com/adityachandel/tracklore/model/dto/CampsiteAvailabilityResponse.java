package com.adityachandel.tracklore.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CampsiteAvailabilityResponse {
    private Map<String, Campsite> campsites;
    private int count;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Campsite {
        @JsonProperty("campsite_id")
        private String campsiteId;

        private String site;
        private String loop;

        @JsonProperty("campsite_reserve_type")
        private String campsiteReserveType;

        private Map<String, String> availabilities;
        private Map<String, Integer> quantities;

        @JsonProperty("campsite_type")
        private String campsiteType;

        @JsonProperty("type_of_use")
        private String typeOfUse;

        @JsonProperty("max_num_people")
        private Integer maxNumPeople;
    }
}