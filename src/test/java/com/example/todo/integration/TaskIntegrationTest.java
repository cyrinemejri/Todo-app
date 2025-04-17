package com.example.todo.integration;

import com.example.todo.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long createdTaskId;

    @Test
    @Order(1)
    void testAddTask() throws Exception {
        Task task = new Task();
        task.setDescription("Test Integration Task");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Test Integration Task"))
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    Task created = objectMapper.readValue(json, Task.class);
                    createdTaskId = created.getId();
                });
    }

    @Test
    @Order(2)
    void testGetAllTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @Order(3)
    void testCompleteTask() throws Exception {
        mockMvc.perform(put("/tasks/{id}/complete", createdTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @Order(4)
    void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", createdTaskId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/tasks"))
                .andExpect(jsonPath("$[?(@.id==" + createdTaskId + ")]").doesNotExist());
    }
}
