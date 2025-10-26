package net.partala.task_manager.tasks;

import jakarta.persistence.EntityNotFoundException;
import net.partala.task_manager.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;
    private final UserService userService;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, UserService userService, TaskMapper mapper) {
        this.repository = repository;
        this.userService = userService;
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

    public Task createTask(
            Task taskToCreate,
            TaskActor actor
    ) {
        var creator = userService.getUserEntityById(actor.getId());
        var taskToSave = mapper.toEntity(taskToCreate, creator, null);
        taskToSave.setStatus(TaskStatus.CREATED);
        taskToSave.setCreateDateTime(LocalDateTime.now());
        var savedTaskEntity = repository.save(taskToSave);

        var savedTask = mapper.toDomain(savedTaskEntity);
        log.info("Task created successfully, createdTask = {}", savedTask);
        return savedTask;
    }

    @Transactional
    public Task updateTask(
            Long taskId,
            Task taskData,
            TaskActor actor
    ) {
        var taskEntity = repository
                .findById(taskId)
                .orElseThrow(() -> new NoSuchElementException(
            "Not found task with id = " + taskId
        ));

        if(!actor.canUpdate(taskEntity)) {
            throw new AccessDeniedException("You cannot update this task, id = " + taskId);
        }
        if(taskEntity.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot update done task. Change state first. id = " + taskId);
        }

        taskEntity.setTitle(taskData.title());
        taskEntity.setPriority(taskData.priority());
        taskEntity.setDeadlineDate(taskData.deadlineDate());
        taskEntity.setAssignedUser(
                userService.getUserEntityById(
                        taskData.assignedUserId()));

        var updatedTask = repository.save(taskEntity);

        log.info("Task updated successfully, id = {}, updatedTask = {}", taskId, updatedTask);
        return mapper.toDomain(updatedTask);
    }

    public void deleteTask(
            Long id,
            TaskActor actor
    ) {
        if(!repository.existsById(id)) {
            throw new NoSuchElementException("Not found task with id = " + id);
        }

        repository.deleteById(id);
        log.info("Task deleted successfully, id = {}", id);
    }

    @Transactional
    public Task startTask(
            Long taskId,
            Long assignUserWithId,
            TaskActor actor
    ) {

        var taskEntityToStart = repository
                .findById(taskId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cannot find task with id = " + taskId
                ));

        if(!actor.canStartTask(taskEntityToStart)) {
            throw new AccessDeniedException("You cannot start this task, id = " + taskId);
        }

        boolean isSameAssignedUser = taskEntityToStart.getAssignedUser() != null &&
                taskEntityToStart
                .getAssignedUser()
                .getId()
                .equals(assignUserWithId);

        if(taskEntityToStart.getStatus() == TaskStatus.IN_PROGRESS && isSameAssignedUser) {
            throw new IllegalStateException("User is already working on task with id = " + taskId);
        }

        if(!userService.canUserTakeTask(assignUserWithId)) {
            throw new IllegalStateException("Cannot assign task to the user, he already has max allowed amount");
        }

        taskEntityToStart.setStatus(TaskStatus.IN_PROGRESS);
        if(!isSameAssignedUser) {
            taskEntityToStart.setAssignedUser(
                    userService.getUserEntityById(assignUserWithId)
            );
        }

        var savedTask = repository.save(taskEntityToStart);

        log.info("Task started successfully, id = {}", taskId);
        return mapper.toDomain(savedTask);
    }

    @Transactional
    public Task completeTask(
            Long taskId,
            TaskActor actor) {

        var taskToComplete = repository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Not found task with id = " + taskId
                ));

        if(taskToComplete.getAssignedUser() == null) {
            throw new IllegalArgumentException("Task doesn't have essential data");
        }

        if(taskToComplete.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Task is already done, id = " + taskId);
        }

        if(!actor.canComplete(taskToComplete)) {
            throw new AccessDeniedException("You cannot complete this task, id = " + taskId);
        }

        taskToComplete.setStatus(TaskStatus.DONE);
        taskToComplete.setDoneDateTime(LocalDateTime.now());
        var savedTask = repository.save(taskToComplete);

        log.info("Task completed successfully, id = {}", taskId);
        return mapper.toDomain(savedTask);
    }

    public List<Task> searchAllByFilter(TaskSearchFilter filter) {

        int pageSize = filter.pageSize() != null ?
                filter.pageSize() : 10;
        int pageNum = filter.pageNum() != null ?
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
        )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
