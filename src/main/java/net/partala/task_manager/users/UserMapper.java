package net.partala.task_manager.users;

import net.partala.task_manager.tasks.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRegistrationDateTime(),
                entity.getRoles(),
                entity.getTasks() == null ?
                        null :
                        entity.getTasks()
                            .stream()
                            .map(TaskEntity::getId)
                            .collect(Collectors.toSet()),
                entity.isEmailVerified()
        );
    }

    public UserEntity toEntity(User task, Set<TaskEntity> tasks) {
        return new UserEntity(
                task.id(),
                task.login(),
                task.email(),
                task.password(),
                task.registrationDateTime(),
                task.emailVerified(),
                new HashSet<>(task.roles()),
                tasks
        );
    }

}
