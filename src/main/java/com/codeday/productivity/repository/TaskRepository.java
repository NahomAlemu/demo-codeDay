package com.codeday.productivity.repository;

import com.codeday.productivity.entity.Goal;
import com.codeday.productivity.entity.Task;
import com.codeday.productivity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Integer> {


    // New methods to handle tasks based on goals
    List<Task> findByGoal(Goal goal);

    Optional<Task> findByGoalAndId(Goal goal, int id);

    // Query to join Task, Goal, and User entities based on your data model
    @Query("SELECT t FROM Task t JOIN t.goal g JOIN g.user u WHERE u = :user AND t.isCompleted = :completionStatus AND t.startDate >= :startDate AND t.endDate <= :endDate")
    List<Task> findByCompletionStatusAndDates(@Param("user") User user, @Param("completionStatus") String completionStatus, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}