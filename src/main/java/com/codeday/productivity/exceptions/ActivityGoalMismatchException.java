package com.codeday.productivity.exceptions;

/**
 * ActivityGoalMismatchException is an exception class that is thrown
 * when an activity does not belong to the expected goal.
 *
 * @author Your Name
 * @version 1.0
 * @see RuntimeException
 */
public class ActivityGoalMismatchException extends RuntimeException {

    /**
     * Default constructor for ActivityGoalMismatchException.
     */
    public ActivityGoalMismatchException(String message) {
        super(message);
    }
}