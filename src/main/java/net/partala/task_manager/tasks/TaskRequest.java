package net.partala.task_manager.tasks;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskRequest(

        @NotBlank
        String title,
        @Future
        LocalDateTime deadlineDataTime,
        @NotNull
        TaskPriority priority
) {
}
