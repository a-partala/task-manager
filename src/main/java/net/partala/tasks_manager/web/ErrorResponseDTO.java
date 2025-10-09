package net.partala.tasks_manager.web;

import java.time.LocalDateTime;

public record ErrorResponseDTO (
        String message,
        String detailedMessage,
        LocalDateTime errorTime
) {
}
