package com.codeday.productivity.repository;

import com.codeday.productivity.entity.Goal;
import com.codeday.productivity.entity.Task;
import com.codeday.productivity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByUser(User user);

    Optional<Task> findByUserAndId(User user, int id);

    // New methods to handle tasks based on goals
    List<Task> findByGoal(Goal goal);

    Optional<Task> findByGoalAndId(Goal goal, int id);


    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.isCompleted = :completionStatus AND t.startDate >= :startDate AND t.endDate <= :endDate")
    List<Task> findByCompletionStatusAndDates(User user, String completionStatus, Instant startDate, Instant endDate);

}
