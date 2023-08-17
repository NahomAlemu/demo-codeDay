package com.codeday.productivity.controller;

import com.codeday.productivity.entity.Goal;
import com.codeday.productivity.entity.Task;
import com.codeday.productivity.entity.User;
import com.codeday.productivity.service.GoalService;
import com.codeday.productivity.service.TaskService;
import com.codeday.productivity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users/{userId}/goals/{goalId}/tasks")
public class TaskController {

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
    public Task createTask(@PathVariable int userId, @PathVariable int goalId, @RequestBody Task task) {
        User user = userService.getUserById(userId);
        Goal goal = goalService.getGoalById(goalId);
        verifyUserGoalAssociation(user, goal);
        task.setUser(user);
        task.setGoal(goal);
        return taskService.saveTaskForUserAndGoal(user, goal, task);
    }

    @GetMapping
    public List<Task> getAllTasksByGoal(@PathVariable int userId, @PathVariable int goalId) {
        User user = userService.getUserById(userId);
        Goal goal = goalService.getGoalById(goalId);
        verifyUserGoalAssociation(user, goal);
        return taskService.getAllTasksByGoal(goal);
    }

    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable int userId, @PathVariable int goalId, @PathVariable int taskId) {
        User user = userService.getUserById(userId);
        Goal goal = goalService.getGoalById(goalId);
        verifyUserGoalAssociation(user, goal);
        return taskService.getTaskByGoalAndId(goal, taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable int userId, @PathVariable int goalId, @PathVariable int taskId, @RequestBody Task task) {
        User user = userService.getUserById(userId);
        Goal goal = goalService.getGoalById(goalId);
        verifyUserGoalAssociation(user, goal);
        task.setUser(user);
        task.setGoal(goal);
        task.setId(taskId);
        return taskService.updateTaskForGoal(goal, task);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable int userId, @PathVariable int goalId, @PathVariable int taskId) {
        User user = userService.getUserById(userId);
        Goal goal = goalService.getGoalById(goalId);
        verifyUserGoalAssociation(user, goal);
        taskService.deleteTaskByGoal(goal, taskId);
    }

    private void verifyUserGoalAssociation(User user, Goal goal) {
        if (goal.getUser().getId() != user.getId()) {
            throw new RuntimeException("Goal does not belong to the given user");
        }
    }
}
