package net.partala.tasks_manager.users;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<User> getUserById(
            @PathVariable("id") Long id
    ) {
        log.info("Called getUserById");

        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody @Valid User userToCreate
    ) {
        log.info("Called createUser");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createUser(userToCreate));
    }
}
