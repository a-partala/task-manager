package net.partala.task_manager.tasks;

import jakarta.validation.Valid;
import net.partala.task_manager.users.UserIdRequest;
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
    public ResponseEntity<List<Task>> searchAllByFilter(
            @RequestParam(value = "creatorId", required = false) Long creatorId,
            @RequestParam(value = "assignedUserId", required = false) Long assignedUserId,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "pageNum", required = false) Integer pageNum
    ){
        log.info("Called getTasksWithFilter");
        var filter = new TaskSearchFilter(
                creatorId,
                assignedUserId,
                status,
                priority,
                pageSize,
                pageNum
        );

        var tasks = service.searchAllByFilter(filter);

        return ResponseEntity.status(HttpStatus.OK)
                .body(tasks);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody @Valid Task taskToCreate
    ) {
        log.info("Called createTask");

        var createdTask = service.createTask(taskToCreate);

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

        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id
    ) {
        log.info("Called deleteTask, id = {}", id);

        service.deleteTask(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{taskId}/start")
    public ResponseEntity<Task> startTask(
            @PathVariable Long taskId,
            @RequestBody @Valid UserIdRequest request
    ) {
        log.info("Called startTask, id = {}", taskId);

        var startedTask = service.startTask(
                taskId,
                request.userId()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(startedTask);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(
            @PathVariable Long id
    ) {
        log.info("Called completeTask, id = {}", id);

        var completedTask = service.completeTask(
                id
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(completedTask);
    }
}
