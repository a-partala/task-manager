package net.partala.task_manager.tasks;

import net.partala.task_manager.users.UserEntity;
import net.partala.task_manager.users.UserRole;
import net.partala.task_manager.users.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService service;

    @Mock
    private TaskMapper mapper;

    @Test
    void shouldThrowIfUserIsAlreadyWorking() {
        var user = new UserEntity();
        user.setId(1L);

        var task = new TaskEntity();
        task.setId(1L);
        task.setAssignedUser(user);
        task.setStatus(TaskStatus.IN_PROGRESS);

        when(repository.findById(1L)).thenReturn(Optional.of(task));

        var actor = new TaskActor(1L, List.of(UserRole.USER));

        assertThrows(IllegalStateException.class, () ->
                service.startTask(1L, 1L, actor));
    }

    @Test
    void shouldThrowLimit() {
        var user = new UserEntity();
        user.setId(1L);

        var task = new TaskEntity();
        task.setId(1L);
        task.setCreator(user);
        task.setStatus(TaskStatus.IN_PROGRESS);

        when(repository.findById(1L)).thenReturn(Optional.of(task));

        when(userService.canUserTakeTask(1L)).thenReturn(false);

        var actor = new TaskActor(1L, List.of(UserRole.USER));

        assertThrows(IllegalStateException.class, () ->
                service.startTask(1L, 1L, actor));
    }

    @Test
    void shouldCompleteIfUserAdmin() {
        var user = new UserEntity();
        user.setId(1L);

        var task = new TaskEntity();
        task.setId(1L);
        task.setCreator(user);
        task.setAssignedUser(user);
        task.setStatus(TaskStatus.IN_PROGRESS);

        when(mapper.toDomain(any())).thenReturn(mock(Task.class));
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        var actor = new TaskActor(2L, List.of(UserRole.ADMIN));
        var result = service.completeTask(1L, actor);
        assertNotNull(result);
    }

    @Test
    void shouldNotStartIfDoneAndLimit() {
        var user = new UserEntity();
        user.setId(1L);

        var task = new TaskEntity();
        task.setId(1L);
        task.setCreator(user);
        task.setAssignedUser(user);
        task.setStatus(TaskStatus.DONE);

        when(userService.canUserTakeTask(1L)).thenReturn(false);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        var actor = new TaskActor(1L, List.of(UserRole.USER));
        assertThrows(IllegalStateException.class, () ->
                service.startTask(1L, 1L, actor));
    }
}
