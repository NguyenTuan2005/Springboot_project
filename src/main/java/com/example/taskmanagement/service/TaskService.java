package com.example.taskmanagement.service;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDeadline(taskDetails.getDeadline());
        task.setStatus(taskDetails.getStatus());
        task.setAssignedUserId(taskDetails.getAssignedUserId());

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));

        taskRepository.delete(task);
    }

    public List<Task> getTasks(String status, Long assignedUserId) {
        if (status != null && assignedUserId != null) {
            return taskRepository.findByStatusAndAssignedUserId(Task.TaskStatus.valueOf(status), assignedUserId);
        } else if (status != null) {
            return taskRepository.findByStatus(Task.TaskStatus.valueOf(status));
        } else if (assignedUserId != null) {
            return taskRepository.findByAssignedUserId(assignedUserId);
        } else {
            return taskRepository.findAll();
        }
    }

    public List<Task> getTasksByStatus(Task.TaskStatus taskStatus) {
        return taskRepository.findByStatus(taskStatus);
    }

    public List<Task> getTasksByAssignedUserId(long assignedUserId) {
        return taskRepository.findByAssignedUserId(assignedUserId);
    }

    public Optional<Task> getTaskById(long taskId) {
        return taskRepository.findById(taskId);
    }
}
