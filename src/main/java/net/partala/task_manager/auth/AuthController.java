package net.partala.task_manager.auth;

import jakarta.validation.Valid;
import net.partala.task_manager.auth.jwt.JwtResponse;
import net.partala.task_manager.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody @Valid
            RegistrationRequest registrationRequest
    ) {
        log.info("Called register");

        authService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody @Valid
            LoginRequest loginRequest
    ) {
        log.info("Called login");

        var response = authService.authenticate(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    //todo: actually send verification
    @PostMapping("/send-email-verification")
    public ResponseEntity<JwtResponse> sendEmailVerification(
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        log.info("Called getEmailToken");

        var jwtResponse = authService.getEmailToken(securityUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(jwtResponse);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam(value = "token", required = true)
            String token
    ) {
        log.info("Called verifyEmail");

        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
