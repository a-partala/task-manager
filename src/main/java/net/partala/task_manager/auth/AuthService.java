package net.partala.task_manager.auth;

import jakarta.validation.Valid;
import net.partala.task_manager.auth.jwt.JwtResponse;
import net.partala.task_manager.auth.jwt.JwtService;
import net.partala.task_manager.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(
            UserDetailsService userDetailsService,
            AuthenticationManager authenticationManager, UserService userService,
            JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public void register(RegistrationRequest registrationRequest) {
        userService.createUser(registrationRequest);
    }

    public JwtResponse authenticate(LoginRequest loginRequest) {

        var authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username(),
                loginRequest.password());
        authenticationManager.authenticate(authToken);
        var userDetails = userDetailsService.loadUserByUsername(loginRequest.username());
        var jwt = jwtService.generateToken(userDetails);
        var expiresAt = Instant.now().plus(Duration.ofMinutes(jwtService.getExpirationMinutes()));
        return new JwtResponse(
                jwt,
                "Bearer",
                expiresAt
        );
    }

    public JwtResponse getEmailToken(
            SecurityUser securityUser
    ) {
        log.info("Called getEmailToken");

        var jwt = jwtService.generateToken(securityUser);
        var expiresAt = Instant.now().plus(Duration.ofMinutes(jwtService.getExpirationMinutes()));
        return new JwtResponse(
                jwt,
                "Bearer",
                expiresAt
        );
    }

    public void verifyEmail(
            String token
    ) {
        Long userId = jwtService.extractUserId(token);
        userService.verifyEmail(userId);
    }

}
