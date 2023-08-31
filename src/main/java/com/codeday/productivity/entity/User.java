package com.codeday.productivity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a User entity in the system. This entity is mapped to the "USER_TBL" table
 * in the database. It contains information about the user's identity, status, and
 * timestamps regarding their creation and last update.
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
@Table(name = "USER_TBL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * The unique identifier for a user. This field is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(name = "is_active", columnDefinition = "VARCHAR(1) DEFAULT 'Y'")
    private String isActive;

    @Column(name = "created_on", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreatedDate
    private Instant createdOn;

    @Column(name = "last_updated", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @LastModifiedDate
    private Instant lastUpdated;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value="user-goal")
    private List<Goal> goals;

    @OneToMany(mappedBy = "user")
    private List<Activity> activities;
    /**
     * This method is called before persisting an object, to ensure 'isActive' is set.
     */
    @PrePersist
    public void prePersist() {
        if (isActive == null) { // check for null
            this.isActive = "Y";
        }
        this.createdOn = Instant.now();
        this.lastUpdated = Instant.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = Instant.now();
    }
}