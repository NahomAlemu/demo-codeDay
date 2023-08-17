package com.codeday.productivity.controller;

import com.codeday.productivity.entity.Task;
import com.codeday.productivity.entity.User;
import com.codeday.productivity.service.TaskService;
import com.codeday.productivity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users/{userId}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping
    public Task createTask(@PathVariable int userId, @RequestBody Task task) {
        User user = userService.getUserById(userId);
        task.setUser(user);
        return taskService.saveTaskForUser(user, task);
    }

    @GetMapping
    public List<Task> getAllTasksByUser(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        return taskService.getAllTasksByUser(user);
    }

    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable int userId, @PathVariable int taskId) {
        User user = userService.getUserById(userId);
        return taskService.getTaskByUserAndId(user, taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable int userId, @PathVariable int taskId, @RequestBody Task task) {
        User user = userService.getUserById(userId);
        task.setUser(user);
        task.setId(taskId);
        return taskService.saveTaskForUser(user, task);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable int userId, @PathVariable int taskId) {
        User user = userService.getUserById(userId);
        taskService.deleteTask(user, taskId);
    }
}
