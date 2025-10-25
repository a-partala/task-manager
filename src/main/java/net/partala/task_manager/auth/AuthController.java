package net.partala.task_manager.auth;

import jakarta.validation.Valid;
import net.partala.task_manager.auth.jwt.JwtResponse;
import net.partala.task_manager.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserService userService;

    public AuthController(
            AuthService authService,
            UserService userService
    ) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody @Valid
            RegistrationRequest registrationRequest
    ) {
        log.info("Called register");

        userService.createUser(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody @Valid
            LoginRequest loginRequest
    ) {
        log.info("Called login");

        var response = authService.getResponse(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
