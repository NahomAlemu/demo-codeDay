package com.codeday.productivity.exceptions;

/**
 * Custom exception class to indicate that a user was not found in the system.
 * This exception should be thrown when a search operation for a specific user
 * does not yield any results from the database.
 *
 * <p>
 * Extends the {@link RuntimeException} class, enabling it to be an unchecked exception.
 * </p>
 * @author Nahom Alemu
 * @version 1.0
 * @see RuntimeException
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message The detail message, saved for later retrieval by the {@link Throwable#getMessage()} method.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}