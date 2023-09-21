package com.codeday.productivity.controller;

import com.codeday.productivity.entity.Activity;
import com.codeday.productivity.entity.User;
import com.codeday.productivity.exceptions.ActivityNotFoundException;
import com.codeday.productivity.exceptions.UnauthorizedException;
import com.codeday.productivity.service.ActivityService;
import com.codeday.productivity.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * ActivityController class to handle activity-related HTTP requests.
 */
@RestController
@RequestMapping("api/v1/users/{userId}/activities")
public class ActivityController {
    private static final Logger logger = LogManager.getLogger(ActivityController.class);
    private final ActivityService activityService;
    private final UserService userService;

    /**
     * Constructs a new instance of ActivityController.
     *
     * @param activityService ActivityService to handle logic related to activities.
     * @param userService     UserService to handle logic related to users.
     */
    @Autowired
    public ActivityController(ActivityService activityService, UserService userService) {
        this.activityService = activityService;
        this.userService = userService;
    }

    /**
     * Creates a new activity for a user with a specific goal.
     *
     * @param userId    User ID
     * @param goalId    Goal ID
     * @param activity  Activity object
     * @return ResponseEntity with the created activity or an error message.
     */
    @PostMapping("/{goalId}")
    public ResponseEntity<?> createActivity(@PathVariable int userId,
                                            @PathVariable int goalId,
                                            @RequestBody Activity activity) {
        try {
            User user = userService.getUserById(userId);
            Activity createdActivity = activityService.saveActivityForUserAndGoal(user, activity, goalId);
            return new ResponseEntity<>(createdActivity, HttpStatus.CREATED);
        } catch (ActivityNotFoundException | UnauthorizedException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error creating activity", e);
            return new ResponseEntity<>("Error creating activity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches the activities associated with a specific goal ID for a given user.
     *
     * @param userId The ID of the user for whom the activities need to be fetched.
     * @param goalId The ID of the goal for which activities are being fetched.
     * @return A ResponseEntity containing a list of activities for the specified goal ID
     *         and HTTP status OK, or an error message and HTTP status INTERNAL_SERVER_ERROR.
     */
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<?> getActivitiesByGoalId(@PathVariable int userId, @PathVariable int goalId) {
        try {
            User user = userService.getUserById(userId);
            List<Activity> activities = activityService.findByGoalId(goalId, user);
            return new ResponseEntity<>(activities, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching activities by goal ID", e);
            return new ResponseEntity<>("Error fetching activities", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches all activities for a user specified by the user ID.
     *
     * @param userId The ID of the user for whom the activities are to be fetched.
     * @return A ResponseEntity containing a list of activities associated with the user
     *         if the operation is successful, or an error message otherwise.
     */
    @GetMapping
    public ResponseEntity<?> getAllActivitiesByUser(@PathVariable int userId) {
        try {
            User user = userService.getUserById(userId);
            List<Activity> activities = activityService.getAllActivitiesByUser(user);
            return new ResponseEntity<>(activities, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching activities", e);
            return new ResponseEntity<>("Error fetching activities", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches the activities for a given user based on their completion status.
     *
     * @param userId The ID of the user for whom the activities need to be fetched.
     * @param isComplete The completion status to filter the activities.
     * @return A ResponseEntity containing a list of activities that match the completion status
     *         and HTTP status OK, or an error message and HTTP status INTERNAL_SERVER_ERROR.
     */
    @GetMapping("/status/{isComplete}")
    public ResponseEntity<?> getActivitiesByCompletionStatus(@PathVariable int userId, @PathVariable String isComplete) {
        try {
            User user = userService.getUserById(userId);
            List<Activity> activities = activityService.findByUserAndIsComplete(user, isComplete);
            return new ResponseEntity<>(activities, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching activities by completion status", e);
            return new ResponseEntity<>("Error fetching activities", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches the activities for a given user that fall within a specified date range.
     *
     * @param userId The ID of the user for whom the activities need to be fetched.
     * @param startDate The starting date of the date range.
     * @param endDate The ending date of the date range.
     * @return A ResponseEntity containing a list of activities that fall within the date range
     *         and HTTP status OK, or an error message and HTTP status INTERNAL_SERVER_ERROR.
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getActivitiesByDateRange(@PathVariable int userId,
                                                      @RequestParam("startDate") Instant startDate,
                                                      @RequestParam("endDate") Instant endDate) {
        try {
            User user = userService.getUserById(userId);
            List<Activity> activities = activityService.findByUserAndStartDateBetween(user, startDate, endDate);
            return new ResponseEntity<>(activities, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching activities by date range", e);
            return new ResponseEntity<>("Error fetching activities", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Starts an existing activity.
     *
     * @param userId     User ID
     * @param goalId     Goal ID
     * @param activityId Activity ID
     * @return ResponseEntity with the updated activity or an error message.
     */
    @PutMapping("/{goalId}/{activityId}/start")
    public ResponseEntity<?> startActivity(@PathVariable int userId, @PathVariable int goalId, @PathVariable int activityId) {
        try {
            User user = userService.getUserById(userId);
            Activity activity = activityService.startActivity(user, activityId, goalId);
            return new ResponseEntity<>(activity, HttpStatus.OK);
        } catch (ActivityNotFoundException | UnauthorizedException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error starting activity", e);
            return new ResponseEntity<>("Error starting activity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Stops an existing activity.
     *
     * @param userId     User ID
     * @param goalId     Goal ID
     * @param activityId Activity ID
     * @return ResponseEntity with the updated activity or an error message.
     */
    @PutMapping("/{goalId}/{activityId}/stop")
    public ResponseEntity<?> stopActivity(@PathVariable int userId, @PathVariable int goalId, @PathVariable int activityId) {
        try {
            User user = userService.getUserById(userId);
            Activity activity = activityService.stopActivity(user, activityId, goalId);
            return new ResponseEntity<>(activity, HttpStatus.OK);
        } catch (ActivityNotFoundException | UnauthorizedException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error stopping activity", e);
            return new ResponseEntity<>("Error stopping activity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing activity.
     *
     * @param userId         User ID
     * @param activityId     Activity ID
     * @param updatedActivity Updated activity object
     * @return ResponseEntity with the updated activity or an error message.
     */
    @PutMapping("/{activityId}")
    public ResponseEntity<?> updateActivity(@PathVariable int userId,
                                            @PathVariable int activityId,
                                            @RequestBody Activity updatedActivity) {
        try {
            User user = userService.getUserById(userId);

            // Set the ID from the path into updatedActivity
            updatedActivity.setId(activityId);

            // Validate if the activity exists and belongs to the user
            activityService.findActivityByIdAndUser(activityId, user);

            // Delegate all updates to the updateActivity method in the service layer
            Activity savedActivity = activityService.updateActivity(activityId, updatedActivity);
            return new ResponseEntity<>(savedActivity, HttpStatus.OK);
        } catch (ActivityNotFoundException | UnauthorizedException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error updating activity", e);
            return new ResponseEntity<>("Error updating activity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes an existing activity.
     *
     * @param userId     User ID
     * @param activityId Activity ID
     * @return ResponseEntity with a success message or an error message.
     */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<?> deleteActivity(@PathVariable int userId, @PathVariable int activityId) {
        try {
            User user = userService.getUserById(userId);

            // Use the existingActivity variable to check if the activity exists for the user
            Activity existingActivity = activityService.findActivityByIdAndUser(activityId, user);

            activityService.deleteActivity(existingActivity.getId());
            return new ResponseEntity<>("Activity deleted successfully", HttpStatus.OK);
        } catch (ActivityNotFoundException | UnauthorizedException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error deleting activity", e);
            return new ResponseEntity<>("Error deleting activity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}