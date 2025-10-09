package net.partala.tasks_manager.tasks;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        var savedTaskEntity = repository.save(taskToSave);

        var savedTask = mapper.toDomain(savedTaskEntity);
        log.info("Task created successfully, createdTask = {}", savedTask);
        return savedTask;
    }

    public Task updateTask(
            Long id,
            Task taskToUpdate
    ) {
        var taskEntity = repository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException(
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

        log.info("Task updated successfully, id = {}, updatedTask = {}", id, updatedTask);
        return mapper.toDomain(updatedTask);
    }

    public void deleteTask(
            Long id
    ) {
        if(!repository.existsById(id)) {
            throw new NoSuchElementException("Not found task with id = " + id);
        }

        repository.deleteById(id);
        log.info("Task deleted successfully, id = {}", id);
    }

    public Task startTask(
            Long taskId,
            Long assignedUserId
    ) {

        var taskToStart = repository
                .findById(taskId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Not found task with id = " + taskId
                ));

        if(assignedUserId == null || assignedUserId <= 0) {
            throw new IllegalArgumentException("Incorrect user id, id = " + assignedUserId);
        }

        if(!taskToStart.getAssignedUserId()
                .equals(assignedUserId)) {

            var userTasks = repository.getAllTasksAssignedToUser(assignedUserId);
            int tasksLimit = 5;
            if(userTasks.size() >= tasksLimit) {
                throw new IllegalStateException("Cannot assign task to user, he already has " + tasksLimit);
            }
        }

        taskToStart.setStatus(TaskStatus.IN_PROGRESS);
        taskToStart.setAssignedUserId(assignedUserId);

        var savedTask = repository.save(taskToStart);

        log.info("Task started successfully, id = {}", taskId);
        return mapper.toDomain(savedTask);
    }

    public Task completeTask(
            Long id
    ) {

        var taskToComplete = repository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Not found task with id = " + id
                ));
        if(taskToComplete.getDeadlineDate() == null
        || taskToComplete.getAssignedUserId() == null) {
            throw new IllegalArgumentException("Task doesn't have essential data");
        }
        taskToComplete.setStatus(TaskStatus.DONE);
        taskToComplete.setDoneDateTime(LocalDateTime.now());

        var savedTask = repository.save(taskToComplete);

        log.info("Task completed successfully, id = {}", id);
        return mapper.toDomain(savedTask);
    }

    public List<Task> searchAllByFilter(TaskSearchFilter filter) {

        int pageSize = filter.pageSize() != null ?
                filter.pageSize() : 10;
        int pageNum = filter.pageSize() != null ?
                filter.pageNum() : 0;

        Pageable pageable = Pageable
                .ofSize(pageSize)
                .withPage(pageNum);

        return repository.searchAllByFilter(
                filter.creatorId(),
                filter.assignedUserId(),
                filter.status(),
                filter.priority(),
                pageable
        );
    }
}
