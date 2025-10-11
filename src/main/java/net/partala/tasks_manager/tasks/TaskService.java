package net.partala.tasks_manager.tasks;

import jakarta.persistence.EntityNotFoundException;
import net.partala.tasks_manager.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
            Task taskToCreate
    ) {
        var creator = userService.getUserEntityById(
                taskToCreate.creatorId());
        var assignUser = taskToCreate.assignedUserId() == null ?
                null :
                userService.getUserEntityById(
                    taskToCreate.assignedUserId());
        var taskToSave = mapper.toEntity(taskToCreate, creator, assignUser);
        taskToSave.setStatus(TaskStatus.CREATED);
        taskToSave.setCreateDateTime(LocalDateTime.now());
        var savedTaskEntity = repository.save(taskToSave);

        var savedTask = mapper.toDomain(savedTaskEntity);
        log.info("Task created successfully, createdTask = {}", savedTask);
        return savedTask;
    }

    @Transactional
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

        taskEntity.setAssignedUser(
                userService.getUserEntityById(
                        taskToUpdate.assignedUserId()));
        taskEntity.setTitle(taskToUpdate.title());
        taskEntity.setPriority(taskToUpdate.priority());
        taskEntity.setDeadlineDate(taskToUpdate.deadlineDate());

        var updatedTask = repository.save(taskEntity);

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

    @Transactional
    public Task startTask(
            Long taskId,
            Long assignUserWithId
    ) {

        if(assignUserWithId == null || assignUserWithId <= 0) {
            throw new IllegalArgumentException("Incorrect user id, id = " + assignUserWithId);
        }

        var taskEntityToStart = repository
                .findById(taskId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cannot find task with id = " + taskId
                ));

        if(!taskEntityToStart.getAssignedUser().getId()
                .equals(assignUserWithId)) {

            if(userService.canUserTakeTask(assignUserWithId)) {
                taskEntityToStart.setAssignedUser(
                        userService.getUserEntityById(assignUserWithId)
                );
            } else {
                throw new IllegalStateException("Cannot assign task to user, he already has max allowed amount");

            }
        }

        taskEntityToStart.setStatus(TaskStatus.IN_PROGRESS);

        var savedTask = repository.save(taskEntityToStart);

        log.info("Task started successfully, id = {}", taskId);
        return mapper.toDomain(savedTask);
    }

    @Transactional
    public Task completeTask(
            Long id
    ) {

        var taskToComplete = repository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Not found task with id = " + id
                ));
        if(taskToComplete.getDeadlineDate() == null
        || taskToComplete.getAssignedUser() == null) {
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
