package com.example.todo.repository;

import com.example.todo.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void testSaveAndFindById() {
        Task task = new Task();
        task.setDescription("Repository Test Task");

        Task savedTask = taskRepository.save(task);

        Optional<Task> fetchedTask = taskRepository.findById(savedTask.getId());
        assertTrue(fetchedTask.isPresent());
        assertEquals("Repository Test Task", fetchedTask.get().getDescription());
        assertFalse(fetchedTask.get().isCompleted());
    }

    @Test
    void testDelete() {
        Task task = new Task();
        task.setDescription("To be deleted");
        Task saved = taskRepository.save(task);

        taskRepository.deleteById(saved.getId());

        Optional<Task> deleted = taskRepository.findById(saved.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testFindAll() {
        Task task1 = new Task();
        task1.setDescription("Task 1");
        Task task2 = new Task();
        task2.setDescription("Task 2");

        taskRepository.save(task1);
        taskRepository.save(task2);

        assertEquals(2, taskRepository.findAll().size());
    }
}
