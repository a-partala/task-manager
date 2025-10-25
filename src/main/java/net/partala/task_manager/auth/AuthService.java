package net.partala.task_manager.auth;

import net.partala.task_manager.auth.jwt.JwtResponse;
import net.partala.task_manager.auth.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserDetailsService userDetailsService,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public JwtResponse getResponse(LoginRequest loginRequest) {

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
}
