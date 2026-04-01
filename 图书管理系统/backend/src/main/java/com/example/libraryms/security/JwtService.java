package com.example.libraryms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
  private final SecretKey key;
  private final String issuer;
  private final long accessMinutes;

  public JwtService(
      @Value("${app.security.jwt.secret}") String secret,
      @Value("${app.security.jwt.issuer}") String issuer,
      @Value("${app.security.jwt.access-token-minutes}") long accessMinutes
  ) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.issuer = issuer;
    this.accessMinutes = accessMinutes;
  }

  public String generateToken(Long userId, String username, String role) {
    Instant now = Instant.now();
    Instant exp = now.plus(accessMinutes, ChronoUnit.MINUTES);
    return Jwts.builder()
        .issuer(issuer)
        .subject(String.valueOf(userId))
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claims(Map.of(
            "username", username,
            "role", role
        ))
        .signWith(key)
        .compact();
  }

  public Claims parse(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .requireIssuer(issuer)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}

