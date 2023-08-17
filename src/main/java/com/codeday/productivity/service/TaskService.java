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


    public Task saveTaskForUserAndGoal(User user, Goal goal, Task task) {
        if (!goal.getUser().equals(user)) {
            throw new IllegalArgumentException("Goal does not belong to the specified user");
        }
        task.setUser(user);
        task.setGoal(goal);
        return taskRepository.save(task);
    }

    // Removing a task from a user
    public void removeTaskForUser(User user, Task task) {
        user.getTasks().remove(task);
        task.setUser(null);
        taskRepository.delete(task);
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

    // Assuming a custom query for filtering, you may need to add an appropriate query to your repository
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

    // Additional custom methods can be added here.
}
