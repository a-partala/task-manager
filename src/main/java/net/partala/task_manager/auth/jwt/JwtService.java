package net.partala.task_manager.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
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

    @Setter
    private String secret;
    @Setter
    @Getter
    private long expirationMinutes;


    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expire = now.plus(Duration.ofMinutes(expirationMinutes));

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expire))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
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
        var username = userDetails.getUsername();
        boolean isNotExpired = extractExpiration(token).isAfter(Instant.now());

        return username.equals(userDetails.getUsername()) &&
                isNotExpired;
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
