package com.codeday.productivity.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents an Activity entity in the system. This entity is mapped to the "ACTIVITY_TBL" table
 * in the database. It contains information about the activity's description, type, duration, and
 * timestamps regarding its creation and last update.
 *
 * <p>
 * The class uses Lombok annotations for getter and setter methods,
 * and JPA annotations for defining the mapping to database table and columns.
 * </p>
 * @author Nahom Alemu
 * @version 1.0
 *
 */
@Entity
@Table(name = "ACTIVITY_TBL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_sequence")
    @SequenceGenerator(name = "activity_sequence", sequenceName = "activity_sequence", allocationSize = 1)
    private Integer id;

    private String title;

    private String description;

    private String type; // e.g., FITNESS, LEARNING, OTHER

    /**
     * Time when the user intended to start the activity. This could be set manually
     * or might be part of a planned schedule.
     */
    @Column(name = "start_date", columnDefinition = "TIMESTAMP")
    private Instant startDate;

    @Column(name = "end_date", columnDefinition = "TIMESTAMP")
    private Instant endDate;

    /**
     * Actual time when the user started the activity by triggering the "Start Timer"
     * action in the user interface.
     */
    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "stop_time")
    private Instant stopTime;

    @Column
    private long duration; // Calculated duration (endTime - startTime) in seconds

    @Column(name = "is_complete", columnDefinition = "VARCHAR(1) DEFAULT 'N'")
    private String isComplete;

    @Column(name = "last_updated",columnDefinition = "TIMESTAMP")
    private Instant lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    @JsonBackReference(value="goal-activity")
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value="user-activity")
    private User user;

    /**
     * This method is called before persisting an object, to ensure 'lastUpdated' is set.
     */
    @PrePersist
    public void prePersist() {
        this.lastUpdated = Instant.now();
        this.startTime = Instant.now();
    }

    /**
     * This method is called before updating an object, to ensure 'lastUpdated' is set.
     */
    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = Instant.now();
        this.stopTime = Instant.now();
    }
}