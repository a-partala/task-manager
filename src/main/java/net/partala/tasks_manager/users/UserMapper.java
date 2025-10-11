package net.partala.tasks_manager.users;

import net.partala.tasks_manager.tasks.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {

        return new User(
                entity.getId(),
                entity.getLogin(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRegistrationDateTime(),
                entity.getTasks() == null ?
                        null :
                        entity.getTasks()
                            .stream()
                            .map(TaskEntity::getId)
                            .toList()
        );
    }

    public UserEntity toEntity(User task, List<TaskEntity> tasks) {
        return new UserEntity(
                task.id(),
                task.login(),
                task.email(),
                task.password(),
                task.registrationDateTime(),
                tasks
        );
    }

}
