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

    public TaskResponse getTaskById(
            Long id
    ) {
        var taskEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id=" + id
                ));

        return mapper.toResponse(taskEntity);
    }

    private TaskEntity getTaskEntityById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found task by id=" + id
                ));
    }

    public TaskResponse createTask(
            TaskRequest request,
            TaskActor actor
    ) {
        var creator = userService.getUserEntityById(actor.getId());
        var taskToSave = new TaskEntity();
        taskToSave.setCreator(creator);
        taskToSave.setStatus(TaskStatus.CREATED);
        taskToSave.setCreatedAt(LocalDateTime.now());

        taskToSave.setTitle(request.title());
        taskToSave.setDeadlineDateTime(request.deadlineDataTime());
        taskToSave.setPriority(request.priority());
        var savedTaskEntity = repository.save(taskToSave);

        var savedTask = mapper.toResponse(savedTaskEntity);
        log.info("Task created successfully, createdTask = {}", savedTask);
        return savedTask;
    }

    @Transactional
    public TaskResponse updateTask(
            Long taskId,
            TaskRequest request,
            TaskActor actor
    ) {
        var taskEntity = getTaskEntityById(taskId);

        if(!actor.canUpdate(taskEntity)) {
            throw new AccessDeniedException("You cannot update this task, id = " + taskId);
        }
        if(taskEntity.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot update done task. Change state first. id = " + taskId);
        }

        taskEntity.setTitle(request.title());
        taskEntity.setPriority(request.priority());
        taskEntity.setDeadlineDateTime(request.deadlineDataTime());

        var updatedTask = repository.save(taskEntity);

        log.info("Task updated successfully, id = {}, updatedTask = {}", taskId, updatedTask);
        return mapper.toResponse(updatedTask);
    }

    public void deleteTask(
            Long id,
            TaskActor actor
    ) {
        var task = getTaskEntityById(id);

        if(!actor.canDelete(task)) {
            throw new AccessDeniedException("You cannot delete this task, id = " + id);
        }

        repository.deleteById(id);
        log.info("Task deleted successfully, id = {}", id);
    }

    @Transactional
    public TaskResponse startTask(
            Long taskId,
            Long assignUserWithId,
            TaskActor actor
    ) {

        var taskEntityToStart = getTaskEntityById(taskId);

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
        return mapper.toResponse(savedTask);
    }

    @Transactional
    public TaskResponse completeTask(
            Long taskId,
            TaskActor actor) {

        var taskToComplete = getTaskEntityById(taskId);

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
        return mapper.toResponse(savedTask);
    }

    public List<TaskResponse> searchAllByFilter(TaskSearchFilter filter) {

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
                .map(mapper::toResponse)
                .toList();
    }
}
