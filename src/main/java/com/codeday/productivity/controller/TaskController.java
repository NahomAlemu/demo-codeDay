package com.codeday.productivity.controller;

import com.codeday.productivity.entity.Goal;
import com.codeday.productivity.entity.Task;
import com.codeday.productivity.entity.User;
import com.codeday.productivity.service.GoalService;
import com.codeday.productivity.service.TaskService;
import com.codeday.productivity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

@RestController
@RequestMapping("api/v1/users/{userId}/goals/{goalId}/tasks")
public class TaskController {
    private static final Logger logger = LogManager.getLogger(TaskController.class);
    private final TaskService taskService;
    private final UserService userService;
    private final GoalService goalService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, GoalService goalService) {
        this.taskService = taskService;
        this.userService = userService;
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@PathVariable int userId, @PathVariable int goalId, @RequestBody Task task) {
        try {
            User user = userService.getUserById(userId);
            Goal goal = goalService.getGoalById(goalId);
            verifyUserGoalAssociation(user, goal);
            task.setGoal(goal);
            Task createdTask = taskService.saveTaskForGoal(goal, task);
            return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating task", e);
            return new ResponseEntity<>("Error creating task", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTasksByGoal(@PathVariable int userId, @PathVariable int goalId) {
        try {
            User user = userService.getUserById(userId);
            Goal goal = goalService.getGoalById(goalId);
            verifyUserGoalAssociation(user, goal);
            List<Task> tasks = taskService.getAllTasksByGoal(goal);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching tasks", e);
            return new ResponseEntity<>("Error fetching tasks", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable int userId, @PathVariable int goalId, @PathVariable int taskId) {
        try {
            User user = userService.getUserById(userId);
            Goal goal = goalService.getGoalById(goalId);
            verifyUserGoalAssociation(user, goal);
            Task task = taskService.getTaskByGoalAndId(goal, taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found"));
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching task by id", e);
            return new ResponseEntity<>("Error fetching task by id", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable int userId, @PathVariable int goalId, @PathVariable int taskId, @RequestBody Task task) {
        try {
            User user = userService.getUserById(userId);
            Goal goal = goalService.getGoalById(goalId);
            verifyUserGoalAssociation(user, goal);
            task.setGoal(goal);
            task.setId(taskId);
            Task updatedTask = taskService.updateTaskForGoal(goal, task);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating task", e);
            return new ResponseEntity<>("Error updating task", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable int userId, @PathVariable int goalId, @PathVariable int taskId) {
        try {
            User user = userService.getUserById(userId);
            Goal goal = goalService.getGoalById(goalId);
            verifyUserGoalAssociation(user, goal);
            taskService.deleteTaskByGoal(goal, taskId);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting task", e);
            return new ResponseEntity<>("Error deleting task", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void verifyUserGoalAssociation(User user, Goal goal) {
        if (goal.getUser().getId() != user.getId()) {
            logger.error("Goal does not belong to the given user");
            throw new RuntimeException("Goal does not belong to the given user");
        }
    }
}