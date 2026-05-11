package com.digitalbanking.chatbot.repositories;

import com.digitalbanking.chatbot.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /** Récupère l'historique d'une session, trié par date croissante. */
    List<ChatMessage> findBySessionIdOrderByTimestampAsc(String sessionId);

    /** Récupère les N derniers messages d'une session pour le contexte RAG. */
    List<ChatMessage> findTop10BySessionIdOrderByTimestampDesc(String sessionId);
}
