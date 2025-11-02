package net.partala.task_manager.users;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record User(

        @Null
        Long id,

        @NotNull
        String login,

        @NotNull
        @Email
        String email,

        @NotNull
        @Size(min = 8, max = 32)
        String password,

        @Null
        LocalDateTime registrationDateTime,

        @Null
        List<UserRole> roles,

        @Null
        List<Long> assignedTaskIds,

        @Null
        boolean emailVerified
) {
}
