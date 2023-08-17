package com.codeday.productivity.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "USER_TBL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customId")
    @GenericGenerator(name = "customId", strategy = "com.codeday.productivity.util.CustomIdGeneratorWrapper") // custom generator
    private int id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "VARCHAR(1) DEFAULT 'Y'")
    private String isActive;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdOn;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Instant lastUpdated;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value="user-goal")
    private List<Goal> goals;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value="user-task")
    private List<Task> tasks = new ArrayList<>();

}

