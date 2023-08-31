package com.codeday.productivity.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GOAL_TBL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "goal_sequence")
    @SequenceGenerator(name = "goal_sequence", sequenceName = "goal_sequence", allocationSize = 1)
    private Integer id;

    private String title;

    private String description;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant startDate;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant endDate;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant dueDate;

    @Column(columnDefinition = "VARCHAR(1) DEFAULT 'N'")
    private String isComplete;

    private int progress;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference(value="user-goal")
    private User user;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value="goal-task")
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "goal")
    private List<Activity> activities;

}
