package com.codeday.productivity.service;

import com.codeday.productivity.entity.Goal;
import com.codeday.productivity.entity.User;
import com.codeday.productivity.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository repository;

    @Autowired  // Constructor injection
    public GoalService(GoalRepository repository) {
        this.repository = repository;
    }

    public Goal saveGoalForUser(User user, Goal goal){

        goal.setUser(user);
        user.getGoals().add(goal);
        return repository.save(goal);
    }

    public void removeGoalForUser(User user, Goal goal) {
        user.getGoals().remove(goal);
        goal.setUser(null);
        repository.delete(goal);
    }

    public List<Goal> getAllGoalsByUser(User user){
        return repository.findByUser(user);
    }

    public List<Goal> getAllGoalsByUserAndCompletion(User user, String isComplete){
        return repository.findByUserAndIsComplete(user, isComplete);
    }

    public List<Goal> getAllGoalsByUserAndStartDate(User user, Instant startDate){
        return repository.findByUserAndStartDate(user, startDate);
    }
}


