package com.codeday.productivity.service;

import com.codeday.productivity.exceptions.UserAlreadyExistsException;
import com.codeday.productivity.exceptions.UserNotFoundException;
import com.codeday.productivity.model.CreateUserRequest;
import com.codeday.productivity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.codeday.productivity.entity.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The UserService class is a Spring Service component responsible for business logic
 * related to the User entity. It interacts with the UserRepository interface to perform
 * CRUD operations on User entities in the database.
 *
 * <p>
 * This class handles a variety of scenarios including creating a new user, updating
 * an existing user, retrieving a user by ID, and other relevant operations.
 * It also includes checks for exceptions like "User Already Exists" and "User Not Found."
 * </p>
 *
 * @author Nahom Alemu
 * @version 1.0
 * @see User
 * @see UserRepository
 */
@Service
public class UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);
    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final String DEACTIVATED_STATUS = "N";

    /**
     * Constructs a UserService with the specified UserRepository.
     *
     * @param repository The UserRepository to use for CRUD operations.
     */
    @Autowired
    public UserService(UserRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Saves a new user to the database after performing validations and encoding the password.
     *
     * @param createUserRequest The request payload containing information to create a new user.
     * @return The saved User entity.
     * @throws IllegalArgumentException if the password is null or empty.
     * @throws UserAlreadyExistsException if a user with the given email already exists.
     */
    public User saveUser(CreateUserRequest createUserRequest) {
        // Log the validation attempt
        LOGGER.info("Validating CreateUserRequest fields");

        // Check if the password is null or empty and throw an exception if it is
        if (createUserRequest.getPassword() == null || createUserRequest.getPassword().isEmpty()) {
            LOGGER.error("Failed to save new user: Password cannot be null or empty");
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        // Convert CreateUserRequest to User entity
        User user = new User();
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        user.setEmail(createUserRequest.getEmail());

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));

        // Set timestamps for when the user is created and last updated
        Instant now = Instant.now();
        user.setCreatedOn(now);
        user.setLastUpdated(now);

        LOGGER.info("Attempting to save new user with email: {}", user.getEmail());
        Optional<User> existingUserByEmail = repository.findByEmail(user.getEmail());

        if (existingUserByEmail.isPresent()) {
            // If the email is already in use, throw an exception
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists.");
        }

        // Save the user to the database and return the saved entity
        return repository.save(user);
    }

    /**
     * Saves a list of new user entities to the database.
     *
     * @param users The list of User entities to save.
     * @return The list of saved User entities.
     */
    public List<User> saveUsers(List<User> users) {
        LOGGER.info("Attempting to save a list of users");

        List<User> savedUsers = new ArrayList<>();
        List<String> duplicateUsers = new ArrayList<>();

        for (User user : users) {
            Optional<User> existingUserByEmail = repository.findByEmail(user.getEmail());

            if (existingUserByEmail.isPresent()) {
                LOGGER.warn("Skipping user with duplicate email: {}", user.getEmail());
                duplicateUsers.add(user.getEmail());
                continue;
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            savedUsers.add(user);
        }

        if (!duplicateUsers.isEmpty()) {
            LOGGER.warn("Found duplicate users: {}", String.join(", ", duplicateUsers));
        }

        List<User> result = repository.saveAll(savedUsers);
        LOGGER.info("Successfully saved {} users", result.size());

        return result;
    }

    /**
     * Retrieves a list of all users from the database.
     *
     * @return A list of all User entities.
     */
    public List<User> getUsers() {
        LOGGER.info("Fetching all users");
        return repository.findAll();
    }

    /**
     * Retrieves a user entity by its ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The User entity with the specified ID.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     */
    public User getUserById(int id) {
        LOGGER.info("Fetching user by ID: {}", id);
        return repository.findById(id).orElseThrow(() -> {
            LOGGER.warn("User with ID {} does not exist", id);
            return new UserNotFoundException("User with ID " + id + " does not exist.");
        });
    }

    /**
     * Retrieves users with the specified first name.
     *
     * @param firstName The first name to search for.
     * @return A list of users with the specified first name.
     */
    public List<User> getUsersByFirstName(String firstName) {
        LOGGER.info("Fetching users by first name: {}", firstName);
        return repository.findByFirstName(firstName);
    }

    /**
     * Retrieves users with the specified last name.
     *
     * @param lastName The last name to search for.
     * @return A list of users with the specified last name.
     */
    public List<User> getUsersByLastName(String lastName) {
        LOGGER.info("Fetching users by last name: {}", lastName);
        return repository.findByLastName(lastName);
    }

    /**
     * Retrieves a user entity with the specified first and last name.
     *
     * @param firstName The first name to search for.
     * @param lastName The last name to search for.
     * @return The user entity that matches the specified first and last name.
     */
    public User getUserByFirstAndLastName(String firstName, String lastName) {
        LOGGER.info("Fetching user by first and last name: {} {}", firstName, lastName);
        return repository.findByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * Updates an existing user entity.
     *
     * @param user The User entity with updated information.
     * @return The updated User entity.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     */
    public User updateUser(User user) {
        LOGGER.info("Attempting to update user with ID: {}", user.getId());
        Optional<User> existingUser = repository.findById(user.getId());

        if (existingUser.isEmpty()) {
            LOGGER.warn("Failed to update user. User with ID {} does not exist", user.getId());
            throw new UserNotFoundException("User with ID " + user.getId() + " does not exist.");
        }

        // Update existing user
        User updatedUser = existingUser.get();

        if (user.getFirstName() != null) {
            updatedUser.setFirstName(user.getFirstName());
            LOGGER.debug("Updated first name for user with ID: {}", user.getId());
        }

        if (user.getLastName() != null) {
            updatedUser.setLastName(user.getLastName());
            LOGGER.debug("Updated last name for user with ID: {}", user.getId());
        }

        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
            LOGGER.debug("Updated email for user with ID: {}", user.getId());
        }

        if (user.getPassword() != null) {
            updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
            LOGGER.debug("Updated and hashed password for user with ID: {}", user.getId());
        }

        if (user.getIsActive() != null) {
            updatedUser.setIsActive(user.getIsActive());
            LOGGER.debug("Updated 'isActive' status for user with ID: {}", user.getId());
        }

        User savedUser = repository.save(updatedUser);
        LOGGER.info("Successfully updated user with ID: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Deactivates a user by setting its 'isActive' flag to the constant value "N".
     *
     * @param id The ID of the user to deactivate.
     * @return A message indicating that the user has been deactivated.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     */
    public String deactivateUser(int id) {
        Optional<User> existingUser = repository.findById(id);
        if (existingUser.isEmpty()) {
            throw new UserNotFoundException("User with ID " + id + " does not exist.");
        }

        User user = existingUser.get();
        user.setIsActive(DEACTIVATED_STATUS);
        repository.save(user);
        LOGGER.info("Successfully deactivated user with ID: {}", id);
        return "User deactivated || " + id;
    }
}