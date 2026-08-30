package org.example.spring_practice_tasks.impl.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtil {

    // В реальном проекте хранить в application.properties и не хардкодить
    private static final String SECRET = "1232132142152334f43superSecretKeyMin32CharsLongForHS256";
    private static final long EXPIRATION_MS = 3600_000L; // 1 час

    public String generateToken(String subject, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public boolean isValidToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Object rolesObj = extractAllClaims(token).get("roles");
        if (!(rolesObj instanceof List<?> list)) {
            return List.of();
        }
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) list;
        return roles;
    }
}
