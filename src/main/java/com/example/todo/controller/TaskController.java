package com.example.todo.controller;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    @PostMapping
    public Task addTask(@RequestBody Task task){
        return taskRepository.save(task);
    }

    @PutMapping("/{id}/complete")
    public Task completeTask(@PathVariable Long id){
        Task task = taskRepository.findById(id).orElseThrow();
        task.setCompleted(true);
        return taskRepository.save(task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id){
        taskRepository.deleteById(id);
    }

}
