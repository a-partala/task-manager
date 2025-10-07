package net.partala.tasks_manager.tasks;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable("id") Long id
    ){
        log.info("Called getTaskById, id={}", id);
        var task = service.getTaskById(id);

        return ResponseEntity.ok()
                .body(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(){
        log.info("Called getAllTasks");

        var tasks = service.getAllTasks();

        return ResponseEntity.status(HttpStatus.OK)
                .body(tasks);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody @Valid Task taskToCreate
    ) {
        log.info("Called createTask");

        var createdTask = service.createTask(taskToCreate);

        log.info("Task created successfully, createdTask = {}", createdTask);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid Task taskToUpdate
    ) {
        log.info("Called updateTask, id = {}, taskToUpdate = {}", id, taskToUpdate);

        var updatedTask = service.updateTask(id, taskToUpdate);

        log.info("Task updated successfully, id = {}, updatedTask = {}", id, updatedTask);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedTask);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> updateTask(
            @PathVariable Long id
    ) {
        log.info("Called deleteTask, id = {}", id);

        service.deleteTask(id);

        log.info("Task deleted successfully, id = {}", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
