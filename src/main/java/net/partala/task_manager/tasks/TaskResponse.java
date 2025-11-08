package net.partala.task_manager.tasks;

import java.time.LocalDateTime;

public record TaskResponse(

        Long id,
        String title,
        Long creatorId,
        Long assignedUserId,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime deadlineDate,
        LocalDateTime completedAt,
        TaskPriority priority
) {
}
