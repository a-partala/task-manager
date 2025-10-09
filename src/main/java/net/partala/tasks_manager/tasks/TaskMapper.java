package net.partala.tasks_manager.tasks;

import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toDomain(TaskEntity entity) {

        return new Task(
                entity.getId(),
                entity.getTitle(),
                entity.getCreatorId(),
                entity.getAssignedUserId(),
                entity.getStatus(),
                entity.getCreateDateTime(),
                entity.getDeadlineDate(),
                entity.getDoneDateTime(),
                entity.getPriority()
        );
    }

    public TaskEntity toEntity(Task task) {
        return new TaskEntity(
                task.id(),
                task.title(),
                task.creatorId(),
                task.assignedUserId(),
                task.status(),
                task.createDateTime(),
                task.deadlineDate(),
                task.doneDateTime(),
                task.priority()
        );
    }

}
