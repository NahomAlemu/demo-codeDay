package com.codeday.productivity.repository;

import com.codeday.productivity.entity.Goal;
import com.codeday.productivity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
    List<Goal> findByUser(User user);

    List<Goal> findByUserAndIsComplete(User user, String isComplete);
    List<Goal> findByUserAndStartDate(User user, Instant startDate);
}

