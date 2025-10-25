package net.partala.task_manager.users;

import jakarta.validation.constraints.NotNull;

public record UserIdRequest(
        @NotNull Long userId
) {
}
