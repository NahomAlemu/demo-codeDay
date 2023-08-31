package com.codeday.productivity.repository;

import com.codeday.productivity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * The UserRepository interface extends the JpaRepository interface to provide a
 * mechanism for storage, retrieval, and search behavior for User entities.
 * This interface will automatically be implemented by Spring Data JPA.
 *
 * <p>
 * It provides custom query methods on top of the basic CRUD operations to handle
 * users based on their first name, last name, and email.
 * </p>
 * @author Nahom Alemu
 * @version 1.0
 * @see JpaRepository
 * @see User
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a User entity based on its first and last name.
     *
     * @param firstName The first name of the User to search for.
     * @param lastName  The last name of the User to search for.
     * @return The User entity that matches both the first and last name.
     */
    User findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Finds all User entities that have the given first name.
     *
     * @param firstName The first name of the User entities to search for.
     * @return A list of User entities that match the given first name.
     */
    List<User> findByFirstName(String firstName);

    /**
     * Finds all User entities that have the given last name.
     *
     * @param lastName The last name of the User entities to search for.
     * @return A list of User entities that match the given last name.
     */
    List<User> findByLastName(String lastName);

    /**
     * Finds a User entity based on its email.
     *
     * @param email The email address of the User to search for.
     * @return An Optional that contains the User entity if it exists, or empty if it does not.
     */
    Optional<User> findByEmail(String email);

}