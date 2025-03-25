import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.security.Key;

public class JWTAuthentication {
    private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a secure secret key
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    // Generate JWT Token
    public static String generateJWTToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserID()) // Use user ID as subject
                .claim("username", user.getUsername()) // Add claims (username, email, etc.)
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiry date
                .signWith(secretKey) // Sign the token with secret key
                .compact();
    }

    // Validate JWT Token
    public static boolean validateJWTToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
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

