package com.codeday.productivity.model;

import lombok.Data;

/**
 * CreateUserRequest is a data transfer object (DTO) that contains the fields
 * necessary for creating a new user entity. This object is used to pass data
 * between the client and the server when creating a new user.
 *
 * <p>
 * It encapsulates the basic details required for a new user, including their
 * first name, last name, email, and password.
 * </p>
 *
 * @author Nahom Alemu
 * @version 1.0
 */
@Data
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

}