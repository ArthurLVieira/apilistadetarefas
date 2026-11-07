package com.project.apilistadetarefas.security.jwt;

import com.project.apilistadetarefas.security.UserDatails.CustomUserDatails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties props;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(CustomUserDatails userDatails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDatails.getId());
        claims.put("name",  userDatails.getName());
        claims.put("type", "access");

        return buildToken(claims, userDatails);
    }

    public String generateRefreshToken(CustomUserDatails userDatails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDatails.getId());
        claims.put("name", userDatails.getName());
        claims.put("type", "refresh");

        return buildToken(claims, userDatails);
    }

    private String buildToken(Map<String, Object> extraClaims, CustomUserDatails userDatails) {
        Instant now = Instant.now();

        return Jwts.builder().subject(userDatails.getUsername())
                .claims(extraClaims)
                .issuedAt(new Date())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(now.plusMillis(props.expiresIn())))
                .signWith(key)
                .compact();
    }

    public boolean isRefreshToken(String token) {
        return  "refresh".equals(parse(token).get("type", String.class));
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
