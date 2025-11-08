package net.partala.task_manager.tasks;

import net.partala.task_manager.users.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toDomain(TaskEntity entity) {

        return new TaskResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getCreator() == null ?
                    null :
                    entity.getCreator().getId(),
                entity.getAssignedUser() == null ?
                        null :
                        entity.getAssignedUser().getId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getDeadlineDateTime(),
                entity.getDoneDateTime(),
                entity.getPriority()
        );
    }

    public TaskEntity toEntity(TaskResponse task, UserEntity creator, UserEntity assignedUser) {
        return new TaskEntity(
                task.id(),
                task.title(),
                creator,
                assignedUser,
                task.status(),
                task.createdAt(),
                task.deadlineDate(),
                task.completedAt(),
                task.priority()
        );
    }

}
