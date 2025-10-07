package net.partala.tasks_manager.tasks;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Task getTaskById(
            Long id
    ) {
        var taskEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id=" + id
                ));

        return mapper.toDomain(taskEntity);
    }

    public List<Task> getAllTasks() {
        var tasks = repository.findAll();

        return tasks
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    public Task createTask(
            Task taskToCreate
    ) {
        var taskToSave = mapper.toEntity(taskToCreate);
        taskToSave.setStatus(TaskStatus.CREATED);
        taskToSave.setCreateDateTime(LocalDateTime.now());
        var savedTask = repository.save(taskToSave);

        return mapper.toDomain(savedTask);
    }

    public Task updateTask(
            Long id,
            Task taskToUpdate
    ) {
        var taskEntity = repository.findById(id).orElseThrow(() -> new NoSuchElementException(
            "Not found task with id = " + id
        ));
        if(taskEntity.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot update done task. Change state first. id = " + id);
        }

        var taskToSave = mapper.toEntity(taskToUpdate);
        taskToSave.setId(taskEntity.getId());
        taskToSave.setCreateDateTime(taskEntity.getCreateDateTime());
        taskToSave.setCreateDateTime(taskToUpdate.createDateTime());
        taskToSave.setStatus(taskToUpdate.status());

        var updatedTask = repository.save(taskToSave);

        return mapper.toDomain(updatedTask);
    }

    public void deleteTask(
            Long id
    ) {
        if(!repository.existsById(id)) {
            throw new NoSuchElementException("Not found task with id = " + id);
        }

        repository.deleteById(id);
    }
}
