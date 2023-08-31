package com.codeday.productivity.service;

import com.codeday.productivity.entity.Goal;
import com.codeday.productivity.entity.Task;
import com.codeday.productivity.entity.User;
import com.codeday.productivity.repository.TaskRepository;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public Task saveTaskForGoal(Goal goal, Task task) {
        task.setGoal(goal);
        return taskRepository.save(task);
    }

    public List<Task> findByGoal(Goal goal) {
        return taskRepository.findByGoal(goal);
    }

    // Add a task to a specific goal
    public Task addTaskToGoal(Goal goal, Task task) {
        task.setGoal(goal);

        // Check if the goal already has tasks. If so, add the new task. If not, initialize and add.
        if(goal.getTasks() != null) {
            goal.getTasks().add(task);
        } else {
            List<Task> tasks = new ArrayList<>();
            tasks.add(task);
            goal.setTasks(tasks);
        }

        return taskRepository.save(task);
    }

    // Fetch all tasks associated with a specific goal
    public List<Task> getTasksByGoal(Goal goal) {
        return taskRepository.findByGoal(goal);
    }

    public List<Task> getAllTasksByGoal(Goal goal) {
        return taskRepository.findByGoal(goal);
    }

    public Optional<Task> getTaskByGoalAndId(Goal goal, int id) {
        return taskRepository.findByGoalAndId(goal, id);
    }

    public Task updateTaskForGoal(Goal goal, Task updatedTask) {
        Task task = taskRepository.findById(updatedTask.getId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (!task.getGoal().equals(goal)) {
            throw new IllegalArgumentException("Task does not belong to the specified goal");
        }
        task.setTitle(updatedTask.getTitle()); // assuming a title field, update similar fields
        // ... add more fields to update if needed
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

    public List<Task> filterTasks(User user, String completionStatus, Instant startDate, Instant endDate) {
        return taskRepository.findByCompletionStatusAndDates(user, completionStatus, startDate, endDate);
    }

    public void deleteTaskByGoal(Goal goal, int taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (!task.getGoal().equals(goal)) {
            throw new IllegalArgumentException("Task does not belong to the specified goal");
        }
        taskRepository.deleteById(taskId);
    }
}