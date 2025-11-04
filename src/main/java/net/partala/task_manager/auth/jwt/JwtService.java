package net.partala.task_manager.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import net.partala.task_manager.auth.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
@ConfigurationProperties(prefix = "app.jwt")
public class JwtService {

    private final Logger log = LoggerFactory.getLogger(JwtService.class);
    @Setter
    private String secret;
    @Setter
    @Getter
    private long expirationMinutes;


    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails, TokenPurpose tokenPurpose) {
        Instant now = Instant.now();
        Instant expire = now.plus(Duration.ofMinutes(expirationMinutes));

        SecurityUser user = (SecurityUser) userDetails;
        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("purpose", tokenPurpose.name())
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expire))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractUserId(String token) {
        return parseAllClaims(token).get("userId", Long.class);
    }

    public TokenPurpose extractPurpose(String token) {
        return TokenPurpose.valueOf(parseAllClaims(token).get("purpose", String.class));
    }

    public String extractUsername(String token) {

        return parseAllClaims(token).getSubject();
    }

    public Instant extractExpiration(String token) {

        Date expire = parseAllClaims(token).getExpiration();
        return expire.toInstant();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {
        var usernameFromToken = extractUsername(token);

        return usernameFromToken.equals(userDetails.getUsername());
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
