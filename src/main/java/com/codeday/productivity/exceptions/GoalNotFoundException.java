package com.codeday.productivity.exceptions;

/**
 * GoalNotFoundException is an exception class that is thrown
 * when a goal is not found in the system.
 *
 * @author Nahom Alemu
 * @version 1.0
 * @see RuntimeException
 */
public class GoalNotFoundException extends RuntimeException {

    public GoalNotFoundException(String message) {
        super(message);
    }
}