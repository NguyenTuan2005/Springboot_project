package com.example.taskmanagement.service;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.TaskStatus.TODO);
        testTask.setDeadline(LocalDate.now().plusDays(1));
    }

    @Test
    void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task createdTask = taskService.createTask(testTask);

        assertNotNull(createdTask);
        assertEquals(testTask.getTitle(), createdTask.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(testTask));

        List<Task> tasks = taskService.getAllTasks();

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(testTask.getTitle(), tasks.get(0).getTitle());
    }

    @Test
    void testUpdateTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");

        Task result = taskService.updateTask(1L, updatedTask);

        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
    }

    @Test
    void testUpdateTask_NotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.updateTask(999L, new Task()));
    }

    @Test
    void testDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(testTask);
    }

    @Test
    void testDeleteTask_NotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(999L));
    }

    @Test
    void testGetTasksByStatus() {
        when(taskRepository.findByStatus(Task.TaskStatus.TODO)).thenReturn(Arrays.asList(testTask));

        List<Task> tasks = taskService.getTasksByStatus(Task.TaskStatus.TODO);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(Task.TaskStatus.TODO, tasks.get(0).getStatus());
    }

    @Test
    void testGetTasksByAssignedUserId() {
        testTask.setAssignedUserId(1L);
        when(taskRepository.findByAssignedUserId(1L)).thenReturn(Arrays.asList(testTask));

        List<Task> tasks = taskService.getTasksByAssignedUserId(1L);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(1L, tasks.get(0).getAssignedUserId());
    }

    @Test
    void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        Optional<Task> result = taskService.getTaskById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTask.getId(), result.get().getId());
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.getTaskById(999L);

        assertFalse(result.isPresent());
    }
}