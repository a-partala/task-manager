package net.partala.task_manager.users;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UserIdRequest(
        @NotNull
        @Min(1)
        Long userId
) {
}
