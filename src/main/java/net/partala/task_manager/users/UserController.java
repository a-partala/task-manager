package net.partala.task_manager.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable("id") Long id
    ) {
        log.info("Called getUserById, id = {}", id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getUserById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/promote")
    public ResponseEntity<UserResponse> promote(
            @PathVariable("id") Long id
    ) {
        log.info("Called promote, id = {}", id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(service.promote(id));
    }
}
