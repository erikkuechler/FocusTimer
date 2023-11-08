package htwberlin.focustimer.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JwtTokenProvider is a component for handling JWT (JSON Web Token) operations
 * such as token generation, validation, and extraction of user information from tokens.
 *
 * Inspired by: https://www.youtube.com/watch?v=mn5UZYtPLjg
 */
@Component
public class JwtTokenProvider {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    /**
     * Generates a JWT token for a given user email.
     *
     * @param userEmail The user's email for whom the token is generated.
     * @return A JWT token as a String.
     */
    public String generateToken(String userEmail) {
        Instant now = Instant.now();
        Instant expiration = now.plus(7, ChronoUnit.DAYS);

        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

    }

    /**
     * Generates a JWT token for a user based on their authentication information.
     *
     * @param authentication The user's authentication information.
     * @return A JWT token as a String.
     */
    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return generateToken(user.getUsername());
    }

    /**
     * Retrieves the user's email from a JWT token.
     *
     * @param token The JWT token from which to extract the user's email.
     * @return The user's email as a String.
     */
    public String getUserMailFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validates the integrity and authenticity of a JWT token.
     *
     * @param token The JWT token to be validated.
     * @return `true` if the token is valid, `false` if not.
     */
    public boolean validateToken(String token) {
        
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
        
    }

}