package net.partala.task_manager.tasks;

import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(TaskEntity entity) {

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
}
