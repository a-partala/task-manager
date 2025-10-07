package net.partala.tasks_manager.tasks;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;

public record Task (

        @Null
        Long id,

        String title,

        Long creatorId,

        Long assignedUserId,

        @Null
        TaskStatus status,

        @Null
        LocalDateTime createDateTime,

        LocalDateTime deadlineDate,

        TaskPriority priority
) {
}
