package com.example.taskmanagement.controller;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.security.JwtUtil;
import com.example.taskmanagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(TaskController.class)
@WithMockUser(username = "test")
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.TaskStatus.TODO);
        testTask.setDeadline(LocalDate.now().plusDays(1));
    }

    @Test
    void testGetAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(testTask));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTask.getId()))
                .andExpect(jsonPath("$[0].title").value(testTask.getTitle()));
    }

    @Test
    void testCreateTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.title").value(testTask.getTitle()));
    }

    @Test
    void testCreateTask_InvalidInput() throws Exception {
        Task invalidTask = new Task();
        // Don't set required fields

        mockMvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTask() throws Exception {
        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(testTask);

        mockMvc.perform(put("/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.title").value(testTask.getTitle()));
    }

    @Test
    void testUpdateTask_NotFound() throws Exception {
        when(taskService.updateTask(eq(999L), any(Task.class))).thenThrow(new RuntimeException("Task not found"));

        mockMvc.perform(put("/tasks/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTask_NotFound() throws Exception {
        doThrow(new RuntimeException("Task not found")).when(taskService).deleteTask(999L);

        mockMvc.perform(delete("/tasks/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTasksByStatus() throws Exception {
        when(taskService.getTasksByStatus(Task.TaskStatus.TODO)).thenReturn(Arrays.asList(testTask));

        mockMvc.perform(get("/tasks?status=TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTask.getId()))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    void testGetTasksByAssignedUserId() throws Exception {
        when(taskService.getTasksByAssignedUserId(1L)).thenReturn(Arrays.asList(testTask));

        mockMvc.perform(get("/tasks?assignedUserId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTask.getId()));
    }
}