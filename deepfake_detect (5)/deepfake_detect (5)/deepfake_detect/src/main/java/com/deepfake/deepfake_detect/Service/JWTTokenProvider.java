package com.deepfake.deepfake_detect.Service;

import com.deepfake.deepfake_detect.Entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JWTTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JWTTokenProvider.class);

    private final Key jwtSecretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    public JWTTokenProvider() {
        this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * Kullanıcı için JWT token oluşturur.
     */
    public String generateToken(User user) {
        logger.info("Generating token for user: {}", user.getEmail());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecretKey)
                .compact();
    }

    /**
     * Token doğrulama işlemi
     */
    public boolean validateToken(String token) {
        try {
            logger.info("Validating token: {}", token);
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token);
            logger.info("Token is valid.");
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token is malformed: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Token signature is invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Token is null or empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Token içinden e-posta adresini ayıklar.
     */
    public String getEmailFromToken(String token) {
        logger.info("Extracting email from token.");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String email = claims.getSubject();
            logger.info("Email extracted from token: {}", email);
            return email;
        } catch (JwtException e) {
            logger.error("Error extracting email from token: {}", e.getMessage());
            throw new RuntimeException("Failed to extract email from token", e);
        }
    }
}
