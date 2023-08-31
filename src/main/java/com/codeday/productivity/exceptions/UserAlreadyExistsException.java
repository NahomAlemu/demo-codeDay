package com.codeday.productivity.exceptions;

/**
 * Custom exception class to indicate that a user already exists in the system.
 * This exception should be thrown when attempting to create a new user with
 * an email address that is already associated with another user in the database.
 *
 * <p>
 * Extends the {@link RuntimeException} class, enabling it to be an unchecked exception.
 * </p>
 * @author Nahom Alemu
 * @version 1.0
 * @see RuntimeException
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message.
     *
     * @param message The detail message, saved for later retrieval by the {@link Throwable#getMessage()} method.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}