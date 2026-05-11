package com.digitalbanking.chatbot.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entité stockant l'historique des conversations du chatbot.
 * Permet la persistance des sessions de conversation par utilisateur.
 */
@Entity
@Table(name = "chat_messages")
@Data @NoArgsConstructor @AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant de la session ou de l'utilisateur (username / chatId Telegram). */
    @Column(nullable = false, length = 150)
    private String sessionId;

    /** Rôle du message : "user" ou "assistant". */
    @Column(nullable = false, length = 20)
    private String role;

    /** Contenu textuel du message. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    /** Source du message : "WEB" ou "TELEGRAM". */
    @Column(length = 20)
    private String source;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
