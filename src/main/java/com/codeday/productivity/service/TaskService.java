package com.codeday.productivity.service;

import com.codeday.productivity.entity.Task;
import com.codeday.productivity.entity.User;
import com.codeday.productivity.repository.TaskRepository;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final GoalService goalService;

    @Autowired
    public TaskService(TaskRepository taskRepository, GoalService goalService) {
        this.taskRepository = taskRepository;
        this.goalService = goalService;
    }

    public Task saveTaskForUser(User user, Task task) {
        task.setUser(user);
        user.getTasks().add(task);
        return taskRepository.save(task);
    }

    // Removing a task from a user
    public void removeTaskForUser(User user, Task task) {
        user.getTasks().remove(task);
        task.setUser(null);
        taskRepository.delete(task);
    }

    public List<Task> getAllTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }

    public Optional<Task> getTaskByUserAndId(User user, int id) {
        return taskRepository.findByUserAndId(user, id);
    }

    public Task updateProgress(int taskId, int progress) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setProgress(progress);
        return taskRepository.save(task);
    }

    public Task recordTimeSpent(int taskId, long timeSpent) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setTimeSpent(task.getTimeSpent() + timeSpent);
        return taskRepository.save(task);
    }

    public Task markTaskAsComplete(int taskId, boolean isComplete) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setIsCompleted(isComplete ? "Y" : "N");
        return taskRepository.save(task);
    }

    // Assuming a custom query for filtering, you may need to add an appropriate query to your repository
    public List<Task> filterTasks(User user, String completionStatus, Instant startDate, Instant endDate) {
        return taskRepository.findByCompletionStatusAndDates(user, completionStatus, startDate, endDate);
    }

    public void deleteTask(User user, int id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            if (task.getUser().equals(user)) {
                taskRepository.deleteById(id);
            } else {
                throw new IllegalArgumentException("Task does not belong to the specified user");
            }
        } else {
            throw new IllegalArgumentException("Task not found");
        }
    }

    // Additional custom methods can be added here.
}
