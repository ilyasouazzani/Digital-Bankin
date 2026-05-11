package com.digitalbanking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration Spring Security — Phase 1 (temporaire / développement).
 *
 * Pour la Partie 1 du projet, toutes les requêtes sont autorisées sans
 * authentification afin de permettre les tests de l'API REST et Swagger.
 *
 * ATTENTION : Cette configuration sera REMPLACÉE en Partie 3 par une
 * sécurité Stateless JWT complète avec gestion des rôles.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactive CSRF (inutile pour API Stateless REST)
            .csrf(AbstractHttpConfigurer::disable)
            // Autorise tout le monde pour la phase de développement
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
}
