package com.example.project_back.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Getter
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // ==========================
    // ACCESS TOKEN
    // ==========================

    public String generateAccessToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .claim("type", "ACCESS")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
//    accset TOKEN recrif them id cho jwt de check jwt o backlist == redis
    // ==========================
    // REFRESH TOKEN
    // ==========================

    public String generateRefreshToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .claim("type", "REFRESH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ==========================
    // GET USERNAME
    // ==========================

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // ==========================
    // CLAIMS
    // ==========================

    public Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ==========================
    // VALIDATE ACCESS TOKEN
    // ==========================

    public boolean validateAccessToken(String token) {

        try {

            Claims claims = getClaims(token);

            return "ACCESS".equals(claims.get("type", String.class));

        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    // ==========================
    // VALIDATE REFRESH TOKEN
    // ==========================

    public boolean validateRefreshToken(String token) {

        try {

            Claims claims = getClaims(token);

            return "REFRESH".equals(claims.get("type", String.class));

        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }
}