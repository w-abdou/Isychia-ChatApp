package com.isychia.isychiachatapp;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.security.Key;

public class JWTAuthentication {
    private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a secure secret key
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    // Generate JWT Token
    public static String generateJWTToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername()) // We'll use username as subject (since you don't have ID field)
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiry date
                .signWith(secretKey) // Sign the token with secret key
                .compact();
    }

    // Validate JWT Token
    public static boolean validateJWTToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Date expiration = claimsJws.getBody().getExpiration();
            return expiration.after(new Date()); // Check if token is still valid
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Invalid token
        }
    }
}
