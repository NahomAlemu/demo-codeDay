package com.codeday.productivity.controller;

import com.codeday.productivity.model.CreateUserRequest;
import com.codeday.productivity.model.UserResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.codeday.productivity.entity.User;
import com.codeday.productivity.exceptions.UserAlreadyExistsException;
import com.codeday.productivity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.codeday.productivity.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * UserController provides RESTful API endpoints for managing users.
 * The class handles CRUD operations related to {@link User} entities and
 * employs {@link UserService} for the underlying business logic.
 *
 * <p>
 * It allows adding a new user, fetching users based on different criteria,
 * updating user information, and deactivating users.
 * </p>
 *
 * @author Nahom Alemu
 * @version 1.0
 */
@RestController
@RequestMapping("api/v1")
public class UserController {

    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    private final UserService service;

    /**
     * Initializes a new instance of {@code UserController}.
     *
     * @param service The UserService instance for handling business logic.
     */
    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * Adds a new user to the database.
     *
     * @param createUserRequest The request payload containing user details.
     * @return ResponseEntity containing either the created user or an error message.
     */
    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            User user = service.saveUser(createUserRequest);
            UserResponse userResponse = toUserResponse(user);
            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Bad Request: {}", e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.error("Internal Server Error: {}", e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("error", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Converts a {@link User} entity to a {@link UserResponse}.
     *
     * @param user The User entity.
     * @return The UserResponse.
     */
    private UserResponse toUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        // populate userResponse fields from user
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setIsActive(user.getIsActive());
        userResponse.setCreatedOn(user.getCreatedOn());
        userResponse.setLastUpdated(user.getLastUpdated());
        return userResponse;
    }

    /**
     * Adds multiple new {@link User} entities to the database in a batch operation.
     *
     * @param createUserRequests The list of {@link CreateUserRequest} objects to be added.
     * @return A {@link ResponseEntity} containing either the UserResponses for each user added or an error message.
     */
    @PostMapping("/users/batch")
    public ResponseEntity<?> addUsers(@RequestBody List<CreateUserRequest> createUserRequests){
        LOGGER.info("Attempting to add users in batch {}", createUserRequests);

        try {
            // Convert CreateUserRequest objects to User entities
            List<User> users = createUserRequests.stream()
                    .map(request -> {
                        User user = new User();
                        // Populate user fields using request
                        user.setFirstName(request.getFirstName());
                        user.setLastName(request.getLastName());
                        user.setEmail(request.getEmail());
                        user.setPassword(request.getPassword());
                        // Add any other fields as needed
                        return user;
                    })
                    .collect(Collectors.toList());

            // Call the service method that handles duplicates and saves users
            List<User> savedUsers = service.saveUsers(users);

            // Convert saved User entities to UserResponse objects
            List<UserResponse> userResponses = savedUsers.stream()
                    .map(this::toUserResponse)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(userResponses, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Bad Request: {}", e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.error("Internal Server Error: {}", e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("error", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves {@link User} entities based on provided first and/or last name.
     * If no criteria are provided, returns all users.
     *
     * @param firstName The first name to search for (optional).
     * @param lastName The last name to search for (optional).
     * @return A {@link ResponseEntity} containing either the users that match the criteria or an error message.
     */
    @GetMapping("/users")
    public ResponseEntity<?> findAllUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName
    ) {
        LOGGER.info("Finding all users filtered by firstName: {}, lastName: {}", firstName, lastName);
        List<User> users = new ArrayList<>();

        if (firstName != null && lastName != null) {
            User user = service.getUserByFirstAndLastName(firstName, lastName);
            if (user != null) {
                return new ResponseEntity<>(List.of(user), HttpStatus.OK);
            }
        } else if (firstName != null) {
            users = service.getUsersByFirstName(firstName);
        } else if (lastName != null) {
            users = service.getUsersByLastName(lastName);
        } else {
            return new ResponseEntity<>(service.getUsers(), HttpStatus.OK);
        }

        if (users.isEmpty()) {
            return new ResponseEntity<>("User doesn't exist", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    /**
     * Retrieves a {@link User} entity by its ID.
     *
     * @param id The ID of the user.
     * @return The {@link User} entity with the specified ID.
     */
    @GetMapping("/users/{id}")
    public User findUserById(@PathVariable int id) {
        LOGGER.info("Finding user by ID: {}", id);
        return service.getUserById(id);
    }

    /**
     * Updates an existing {@link User} entity.
     *
     * @param id The ID of the user to be updated.
     * @param user The new {@link User} details.
     * @return The updated {@link User} entity.
     */
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        user.setId(id);
        LOGGER.info("Updating user with ID: {} with data: {}", id, user);
        return service.updateUser(user);
    }

    /**
     * Deactivates a {@link User} entity by setting its 'isActive' field to 'N'.
     *
     * @param id The ID of the user to be deactivated.
     * @return A message confirming the deactivation.
     */
    @PutMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable int id) {
        LOGGER.info("Deactivating user with ID: {}", id);
        return service.deactivateUser(id);
    }

    /**
     * Exception handler for {@link UserNotFoundException}.
     *
     * @param ex The caught exception.
     * @return A {@link ResponseEntity} with a 404 status and the exception's message.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        LOGGER.error("User not found exception: {}", ex.getMessage());
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    /**
     * Exception handler for {@link UserAlreadyExistsException}.
     *
     * @param ex The caught exception.
     * @return A {@link ResponseEntity} with a 409 status and the exception's message.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        LOGGER.error("User already exists exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}