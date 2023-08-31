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
     * Fetches all activities by a specific user.
     *
     * @param userId User ID
     * @return ResponseEntity with a list of activities or an error message.
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
            if (activityId != updatedActivity.getId()) {
                return new ResponseEntity<>("Activity ID mismatch", HttpStatus.BAD_REQUEST);
            }

            Activity existingActivity = activityService.findActivityByIdAndUser(activityId, user);

            // Update existingActivity using values from updatedActivity
            existingActivity.setDescription(updatedActivity.getDescription());

            Activity savedActivity = activityService.updateActivity(existingActivity);
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
