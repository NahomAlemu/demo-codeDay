package com.codeday.productivity.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "TASK_TBL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String description;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant startDate;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant endDate;

    @Column(columnDefinition = "VARCHAR(1) DEFAULT 'N'")
    private String isCompleted;

    private int progress;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant lastUpdated;

    @Column
    private long timeSpent; // Assuming this represents time in seconds or milliseconds

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    @JsonBackReference(value="goal-task")
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference(value="user-task")
    private User user;

}
