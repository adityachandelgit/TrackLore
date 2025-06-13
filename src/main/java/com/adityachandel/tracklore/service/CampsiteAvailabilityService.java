package com.adityachandel.tracklore.service;

import com.adityachandel.tracklore.model.dto.CampsiteAvailabilityResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class CampsiteAvailabilityService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CampsiteAvailabilityService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CampsiteAvailabilityResponse fetchCampsiteAvailability(String campgroundId, String startDateIso) {
        try {
            String encodedDate = URLEncoder.encode(startDateIso, StandardCharsets.UTF_8);
            String url = String.format(
                    "https://www.recreation.gov/api/camps/availability/campground/%s/month?start_date=%s",
                    campgroundId, encodedDate
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("accept", "application/json, text/plain, */*")
                    .header("accept-language", "en-US,en;q=0.9")
                    .header("user-agent", "Java HttpClient")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                CampsiteAvailabilityResponse value = objectMapper.readValue(response.body(), CampsiteAvailabilityResponse.class);
                return value;
            } else {
                System.err.println("HTTP error: " + response.statusCode());
                System.err.println("Response body: " + response.body());
                return null;
            }

        } catch (Exception e) {
            // Log exception
            System.err.println("Exception during campsite fetch: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}