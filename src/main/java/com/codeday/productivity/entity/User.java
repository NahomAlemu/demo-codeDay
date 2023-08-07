package com.codeday.productivity.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Table(name = "USER_TBL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_active", columnDefinition = "VARCHAR(1) DEFAULT 'Y'")
    private String isActive;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdOn;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Instant lastUpdated;

}

