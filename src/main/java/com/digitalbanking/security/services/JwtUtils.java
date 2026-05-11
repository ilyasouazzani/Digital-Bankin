package com.digitalbanking.security.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilitaire JWT : génération, validation et extraction de claims.
 */
@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Génère un token JWT signé pour un utilisateur authentifié.
     * Inclut le username et la liste des rôles dans les claims.
     */
    public String generateToken(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrait le username (subject) du token JWT.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extrait la liste des rôles depuis les claims du token.
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return (List<String>) parseClaims(token).get("roles");
    }

    /**
     * Valide le token JWT : signature, expiration, format.
     * @return true si valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expiré : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT non supporté : {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT malformé : {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT signature invalide : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT vide : {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
