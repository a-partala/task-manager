package net.partala.task_manager.users;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(

        @Null
        Long id,

        @NotNull
        String login,

        @NotNull
        @Email
        String email,

        @Null
        LocalDateTime registrationDateTime,

        @Null
        Set<UserRole> roles,

        @Null
        Set<Long> assignedTaskIds,

        @Null
        boolean emailVerified
) {
}
