package com.codeday.productivity.repository;

import com.codeday.productivity.entity.Activity;
import com.codeday.productivity.entity.Goal;
import com.codeday.productivity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * ActivityRepository Interface.
 * This repository interface extends JpaRepository and defines methods for database operations
 * related to the Activity entity.
 *
 * <p>
 * Each method defined in this interface performs a different type of query operation
 * on the Activity table in the database.
 * </p>
 *
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    /**
     * Find activities by associated user.
     *
     * @param user The user for which to fetch activities.
     * @return List of activities related to the given user.
     */
    List<Activity> findByUser(User user);

    /**
     * Find activities by associated goal ID.
     *
     * @param goalId The ID of the goal for which to fetch activities.
     * @return List of activities related to the goal ID.
     */
    List<Activity> findByGoalIdAndUserId(int goalId, int userId);

    /**
     * Find activities by associated goal entity.
     *
     * @param goal The goal entity for which to fetch activities.
     * @return List of activities related to the given goal entity.
     */
    List<Activity> findByGoal(Goal goal);

    /**
     * Find activity by associated user and activity ID.
     *
     * @param user The user entity.
     * @param id   The activity ID.
     * @return Optional containing the found activity or empty if not found.
     */
    Optional<Activity> findByUserAndId(User user, int id);

    /**
     * Find activities by user and completion status.
     *
     * @param user The user entity.
     * @return List of activities based on the user and completion status.
     */
    @Query("SELECT a FROM Activity a WHERE a.user = :user AND a.isComplete = :isComplete")
    List<Activity> findByUserAndIsComplete(User user, String isComplete);

    /**
     * Find activities by user and a date range.
     *
     * @param user      The user entity.
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return List of activities falling within the specified date range for the given user.
     */
    List<Activity> findByUserAndStartDateBetween(User user, Instant startDate, Instant endDate);
}