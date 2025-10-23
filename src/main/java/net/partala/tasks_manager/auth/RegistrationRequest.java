package net.partala.tasks_manager.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(
        @NotNull
        String username,

        @NotNull
        @Email
        String email,

        @NotNull
        String password
) {
}
