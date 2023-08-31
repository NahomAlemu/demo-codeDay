package com.codeday.productivity.model;

import lombok.Data;
import java.time.Instant;

/**
 * UserResponse is a data transfer object (DTO) that represents the response
 * structure for a user entity. This object is used to pass data between
 * the server and the client when performing user-related operations.
 *
 * <p>
 * It encapsulates the essential details of a user after they've been processed
 * by the business logic, including their identification information, active status,
 * and timestamps related to their creation and last update.
 * </p>
 *
 * @author Nahom Alemu
 * @version 1.0
 */
@Data
public class UserResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String isActive;
    private Instant createdOn;
    private Instant lastUpdated;
}