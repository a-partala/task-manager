package net.partala.task_manager.users;

import net.partala.task_manager.tasks.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(UserEntity entity) {

        return new UserResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
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
}
