package com.digitalbanking.security.filters;

import com.digitalbanking.security.services.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtre d'authentification JWT.
 * Intercepte POST /api/auth/login, vérifie les credentials,
 * et retourne un token JWT en cas de succès.
 */
@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        try {
            Map<?, ?> credentials = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            String username = (String) credentials.get("username");
            String password = (String) credentials.get("password");
            log.info("Tentative d'authentification pour : {}", username);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture credentials : " + e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        String token = jwtUtils.generateToken(authResult);
        log.info("Authentification réussie pour : {}", authResult.getName());

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("access_token", token);
        tokenMap.put("username", authResult.getName());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(response.getOutputStream(), tokenMap);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        log.warn("Échec d'authentification : {}", failed.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<String, String> error = new HashMap<>();
        error.put("error", "Identifiants invalides");
        error.put("message", failed.getMessage());
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
