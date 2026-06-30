package com.ELSystem.elsystem.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    public SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Generate token from username
    public String generateToken(String username){
        return Jwts.builder().
                subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // Extract username from token
    public String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Validate token
    public boolean validateToken(String token){
        try {
            Jwts.parser().verifyWith(getSigningKey())
                    .build().parseSignedClaims(token);
            return true;
        }catch (IllegalArgumentException e) {
            return false;
        }
    }
}
