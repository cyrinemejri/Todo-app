package com.example.todo.controller;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
//@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        public TaskRepository taskRepository() {
            return Mockito.mock(TaskRepository.class);
        }
    }

    @Test
    void testGetAllTasks() throws Exception {
        Task task1 = new Task();
        task1.setDescription("Learn Spring");

        Task task2 = new Task();
        task2.setDescription("Build Project");

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testAddTask() throws Exception {
        Task task = new Task();
        task.setDescription("New Task");

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("New Task"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void testCompleteTask() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setDescription("Complete this");
        task.setCompleted(false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task completedTask = new Task();
        completedTask.setId(1L);
        completedTask.setDescription("Complete this");
        completedTask.setCompleted(true);

        when(taskRepository.save(any(Task.class))).thenReturn(completedTask);

        mockMvc.perform(put("/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskRepository).deleteById(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk());
    }
}

