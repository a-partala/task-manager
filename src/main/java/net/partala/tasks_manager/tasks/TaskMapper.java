package net.partala.tasks_manager.tasks;

import net.partala.tasks_manager.users.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toDomain(TaskEntity entity) {

        return new Task(
                entity.getId(),
                entity.getTitle(),
                entity.getCreator() == null ?
                    null :
                    entity.getCreator().getId(),
                entity.getAssignedUser() == null ?
                        null :
                        entity.getAssignedUser().getId(),
                entity.getStatus(),
                entity.getCreateDateTime(),
                entity.getDeadlineDate(),
                entity.getDoneDateTime(),
                entity.getPriority()
        );
    }

    public TaskEntity toEntity(Task task, UserEntity creator, UserEntity assignedUser) {
        return new TaskEntity(
                task.id(),
                task.title(),
                creator,
                assignedUser,
                task.status(),
                task.createDateTime(),
                task.deadlineDate(),
                task.doneDateTime(),
                task.priority()
        );
    }

}
