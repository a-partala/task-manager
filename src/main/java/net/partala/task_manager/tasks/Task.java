package net.partala.task_manager.tasks;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;

public record Task(

        @Null
        Long id,

        String title,

        @NotNull
        Long creatorId,

        Long assignedUserId,

        @Null
        TaskStatus status,

        @Null
        LocalDateTime createDateTime,

        @NotNull @Future
        LocalDateTime deadlineDate,

        @Null
        LocalDateTime doneDateTime,

        @NotNull
        TaskPriority priority
) {
}
