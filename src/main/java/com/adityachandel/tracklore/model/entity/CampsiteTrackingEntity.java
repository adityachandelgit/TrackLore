package com.adityachandel.tracklore.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "campsite_tracking", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"campground_id", "campsite_id", "tracked_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampsiteTrackingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campground_id", nullable = false)
    private String campgroundId;

    @Column(name = "campsite_id", nullable = false)
    private String campsiteId;

    @Column(name = "tracked_date", nullable = false)
    private LocalDate trackedDate;

    @Column(name = "site")
    private String site;

    @Column(name = "loop_name")
    private String loop;

    @Column(name = "reserve_type")
    private String reserveType;

    @Column(name = "campsite_type")
    private String campsiteType;

    @Column(name = "type_of_use")
    private String typeOfUse;

    @Column(name = "status")
    private String status;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "max_people")
    private Integer maxPeople;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}