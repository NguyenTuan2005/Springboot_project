package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(Task.TaskStatus status);
    List<Task> findByAssignedUserId(Long assignedUserId);
    List<Task> findByStatusAndAssignedUserId(Task.TaskStatus status, Long assignedUserId);
}