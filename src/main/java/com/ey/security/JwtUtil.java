

package com.ey.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // >= 32 bytes. (Move to application.properties in real projects)
    private final String jwtSecret = "ThisIsASecretKeyThatIsAtLeast32CharsLong";
    private final long jwtExpirationMs = 86400000L; // 24h

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** Generate JWT with email as subject (no extra claims). */
    public String generateToken(String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email)           // store email as 'sub'
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Extract email (subject) from token. */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** Validate signature + expiration. */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

 // ---- Reset token (separate secret + short expiry) ----
     private final String resetSecret = "ThisIsAResetSecretKeyAlsoAtLeast32CharsLong";
     private final long resetExpirationMs = 15 * 60 * 1000L; // 15 minutes

     private SecretKey getResetSigningKey() {
         byte[] keyBytes = resetSecret.getBytes(StandardCharsets.UTF_8);
         return Keys.hmacShaKeyFor(keyBytes);
     }

/** Generate short-lived reset token with email as subject (no extra claims). */
    public String generateResetToken(String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + resetExpirationMs);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getResetSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

/** Validate reset token (signature + expiration) using reset secret. */
    public boolean validateResetToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getResetSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Extract email (subject) from reset token. */
       public String extractEmailFromResetToken(String token) {
           return Jwts.parserBuilder()
                   .setSigningKey(getResetSigningKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
       }



}


