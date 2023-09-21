package com.codeday.productivity.service;

import com.codeday.productivity.entity.Activity;
import com.codeday.productivity.entity.User;
import com.codeday.productivity.entity.Goal;
import com.codeday.productivity.exceptions.ActivityGoalMismatchException;
import com.codeday.productivity.exceptions.GoalNotFoundException;
import com.codeday.productivity.exceptions.UnauthorizedException;
import com.codeday.productivity.repository.ActivityRepository;
import com.codeday.productivity.exceptions.ActivityNotFoundException;
import com.codeday.productivity.repository.GoalRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing activities within the application.
 * This includes operations such as creating, updating, deleting, and fetching activities.
 * It also provides operations to start and stop activities.
 *
 * @author Nahom Alemu
 */
@Service
public class ActivityService {

    private static final Logger LOGGER = LogManager.getLogger(ActivityService.class);
    private final ActivityRepository activityRepository;
    private final GoalRepository goalRepository;
    private final GoalService goalService;

    /**
     * Constructor to initialize repositories and services.
     *
     * @param activityRepository The activity repository.
     * @param goalRepository     The goal repository.
     * @param goalService        The goal service.
     */
    @Autowired
    public ActivityService(ActivityRepository activityRepository, GoalRepository goalRepository, GoalService goalService) {
        this.activityRepository = activityRepository;
        this.goalRepository = goalRepository;
        this.goalService = goalService;
    }

    /**
     * Saves an activity for a user and goal.
     *
     * @param user     The user for whom the activity is saved.
     * @param activity The activity to save.
     * @param goalId   The goal ID associated with the activity.
     * @return The saved activity.
     * @throws GoalNotFoundException When the goal is not found.
     */
    public Activity saveActivityForUserAndGoal(User user, Activity activity, int goalId) {
        // Fetch the Goal entity using its ID
        Optional<Goal> optionalGoal = goalRepository.findById(goalId);
        if (optionalGoal.isEmpty()) {
            throw new GoalNotFoundException("Goal not found");
        }
        Goal goal = optionalGoal.get();

        // Set the user and the associated goal
        activity.setUser(user);
        activity.setGoal(goal);

        return activityRepository.save(activity);
    }

    /**
     * Finds an activity by its ID and validates if it belongs to the given user.
     *
     * @param activityId The ID of the activity to find.
     * @param user The user who owns the activity.
     * @return The found activity.
     * @throws ActivityNotFoundException When the activity is not found.
     * @throws UnauthorizedException When the activity does not belong to the given user.
     */
    public Activity findActivityByIdAndUser(int activityId, User user) {
        Optional<Activity> optionalActivity = activityRepository.findById(activityId);
        if (optionalActivity.isEmpty()) {
            throw new ActivityNotFoundException("Activity not found");
        }
        Activity activity = optionalActivity.get();
        if (!activity.getUser().equals(user)) {
            throw new UnauthorizedException("Unauthorized");
        }
        return activity;
    }

    /**
     * Starts an activity for a given user and goal.
     *
     * @param user The user starting the activity.
     * @param activityId The ID of the activity to start.
     * @param goalId The ID of the goal to which the activity belongs.
     * @return The updated activity.
     * @throws ActivityGoalMismatchException When the activity and goal do not match.
     */
    public Activity startActivity(User user, int activityId, int goalId) {
        // Retrieve the activity and validate it against the user and goal
        Activity activity = findActivityByIdAndUser(activityId, user);

        // Validate that the activity belongs to the goal
        Goal goal = goalService.getGoalById(goalId);
        if (!activity.getGoal().getId().equals(goal.getId())) {
            throw new ActivityGoalMismatchException("Activity does not belong to the goal");
        }

        // Update the activity to indicate it has started
        activity.setIsComplete("N");
        activity.setStartTime(Instant.now());

        return activityRepository.save(activity);
    }

    /**
     * Stops an ongoing activity for a given user and goal.
     *
     * @param user The user stopping the activity.
     * @param activityId The ID of the activity to stop.
     * @param goalId The ID of the goal to which the activity belongs.
     * @return The updated activity.
     * @throws ActivityNotFoundException When the activity is not found.
     * @throws UnauthorizedException When the activity does not belong to the user.
     * @throws ActivityGoalMismatchException When the activity and goal do not match.
     */
    public Activity stopActivity(User user, int activityId, int goalId) {
        Optional<Activity> optionalActivity = activityRepository.findById(activityId);

        if (optionalActivity.isEmpty()) {
            throw new ActivityNotFoundException("Activity not found");
        }

        Activity activity = optionalActivity.get();

        // Validate that the activity belongs to the user
        if (!activity.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Activity does not belong to the user");
        }

        // Validate that the activity belongs to the goal
        Goal goal = goalService.getGoalById(goalId);
        if (!activity.getGoal().getId().equals(goal.getId())) {
            throw new ActivityGoalMismatchException("Activity does not belong to the goal");
        }

        // Stop the activity and update the stopTime
        Instant stopTime = Instant.now();
        activity.setStopTime(stopTime);

        // Calculate the duration
        long duration = Duration.between(activity.getStartTime(), stopTime).getSeconds();
        activity.setDuration(duration);

        return activityRepository.save(activity);
    }

    /**
     * Retrieves an activity by its ID.
     *
     * @param id The ID of the activity to be retrieved.
     * @return The retrieved activity.
     * @throws ActivityNotFoundException When the activity is not found.
     */
    public Activity getActivityById(int id) {
        LOGGER.info("Fetching activity by ID: {}", id);
        return activityRepository.findById(id).orElseThrow(() -> {
            LOGGER.warn("Activity with ID {} does not exist", id);
            return new ActivityNotFoundException("Activity with ID " + id + " does not exist.");
        });
    }

    /**
     * Updates an existing activity.
     *
     * @param updatedActivity The activity with updated information.
     * @return The updated activity.
     */
    public Activity updateActivity(int id, Activity updatedActivity) {
        LOGGER.info("Attempting to update activity with ID: {}", id);

        Activity existingActivity = getActivityById(id);

        if (updatedActivity.getDescription() != null || existingActivity.getDescription() == null) {
            existingActivity.setDescription(updatedActivity.getDescription());
        }

        if (updatedActivity.getTitle() != null || existingActivity.getTitle() == null) {
            existingActivity.setTitle(updatedActivity.getTitle());
        }

        if (updatedActivity.getIsComplete() != null || existingActivity.getIsComplete() == null) {
            existingActivity.setIsComplete(updatedActivity.getIsComplete());
        }

        if (updatedActivity.getType() != null || existingActivity.getType() == null) {
            existingActivity.setType(updatedActivity.getType());
        }

        Activity savedActivity = activityRepository.save(existingActivity);
        LOGGER.info("Successfully updated activity with ID: {}", savedActivity.getId());

        return savedActivity;
    }

    /**
     * Fetches the activities associated with a specific goal ID for a given user.
     *
     * @param goalId The ID of the goal for which activities are being fetched.
     * @param user The User object representing the user for whom activities need to be fetched.
     * @return A list of activities that are associated with the specified goal ID and user.
     */
    public List<Activity> findByGoalId(int goalId, User user) {
        return activityRepository.findByGoalIdAndUserId(goalId, user.getId());
    }

    /**
     * Fetches the activities for a given user based on their completion status.
     *
     * @param user The User object representing the user for whom activities need to be fetched.
     * @param isComplete The completion status ('Y' for complete, 'N' for not complete) to filter the activities.
     * @return A list of activities that match the specified completion status and user.
     */
    public List<Activity> findByUserAndIsComplete(User user, String isComplete) {
        return activityRepository.findByUserAndIsComplete(user, isComplete);
    }

    /**
     * Fetches the activities for a given user that fall within a specified date range.
     *
     * @param user The User object representing the user for whom activities need to be fetched.
     * @param startDate The starting date (as an Instant object) of the date range.
     * @param endDate The ending date (as an Instant object) of the date range.
     * @return A list of activities that fall within the specified date range for the given user.
     */
    public List<Activity> findByUserAndStartDateBetween(User user, Instant startDate, Instant endDate) {
        return activityRepository.findByUserAndStartDateBetween(user, startDate, endDate);
    }

    /**
     * Fetches the activities associated with a specific goal.
     *
     * @param goal The Goal object for which activities are being fetched.
     * @return A list of activities that are associated with the specified goal.
     */
    public List<Activity> findByGoal(Goal goal) {
        return activityRepository.findByGoal(goal);
    }

    /**
     * Fetches an activity by its ID for a specific user.
     *
     * @param user The User object representing the user for whom the activity is being fetched.
     * @param activityId The ID of the activity to be fetched.
     * @return The activity that matches the specified ID and user.
     * @throws ActivityNotFoundException If the activity with the given ID is not found for the user.
     */
    public Activity findByUserAndId(User user, int activityId) {
        return activityRepository.findByUserAndId(user, activityId).orElseThrow(() -> new ActivityNotFoundException("Activity not found for the user"));
    }

    /**
     * Fetches all activities for a given user.
     *
     * @param user The User object representing the user for whom activities are being fetched.
     * @return A list of all activities that are associated with the specified user.
     */
    public List<Activity> getAllActivitiesByUser(User user) {
        return activityRepository.findByUser(user);
    }

    /**
     * Deletes an activity by its ID.
     *
     * @param id The ID of the activity to be deleted.
     * @throws ActivityNotFoundException When the activity is not found.
     */
    public void deleteActivity(int id) {
        LOGGER.info("Attempting to delete activity with ID: {}", id);
        if (!activityRepository.existsById(id)) {
            LOGGER.warn("Failed to delete activity. Activity with ID {} does not exist", id);
            throw new ActivityNotFoundException("Activity with ID " + id + " does not exist.");
        }
        activityRepository.deleteById(id);
        LOGGER.info("Successfully deleted activity with ID: {}", id);
    }

}