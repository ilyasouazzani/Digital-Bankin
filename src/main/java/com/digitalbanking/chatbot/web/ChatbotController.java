package com.digitalbanking.chatbot.web;

import com.digitalbanking.chatbot.entities.ChatMessage;
import com.digitalbanking.chatbot.repositories.ChatMessageRepository;
import com.digitalbanking.chatbot.services.RagChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Controller REST pour le chatbot IA.
 * Exposé sur /api/chatbot - utilisé par l'interface Angular (Partie 5).
 */
@RestController
@RequestMapping("/api/chatbot")
@AllArgsConstructor
@Tag(name = "Chatbot IA", description = "API du chatbot bancaire RAG")
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final RagChatbotService ragChatbotService;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * Endpoint principal : reçoit un message et retourne la réponse du LLM.
     * Le sessionId est le username JWT de l'utilisateur connecté.
     */
    @PostMapping("/chat")
    @Operation(summary = "Envoie un message au chatbot IA et reçoit une réponse")
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody Map<String, String> body,
            Principal principal) {

        String sessionId = principal != null ? principal.getName() : "anonymous";
        String message   = body.get("message");

        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le message ne peut pas être vide"));
        }

        String response = ragChatbotService.chat(sessionId, message, "WEB");
        return ResponseEntity.ok(Map.of("response", response, "sessionId", sessionId));
    }

    /**
     * Récupère l'historique des conversations de l'utilisateur connecté.
     */
    @GetMapping("/history")
    @Operation(summary = "Récupère l'historique de conversation de l'utilisateur")
    public ResponseEntity<List<ChatMessage>> getHistory(Principal principal) {
        String sessionId = principal != null ? principal.getName() : "anonymous";
        List<ChatMessage> history = chatMessageRepository
                .findBySessionIdOrderByTimestampAsc(sessionId);
        return ResponseEntity.ok(history);
    }

    /**
     * Efface l'historique de conversation de l'utilisateur courant.
     */
    @DeleteMapping("/history")
    @Operation(summary = "Efface l'historique de conversation")
    public ResponseEntity<Void> clearHistory(Principal principal) {
        String sessionId = principal != null ? principal.getName() : "anonymous";
        List<ChatMessage> messages = chatMessageRepository
                .findBySessionIdOrderByTimestampAsc(sessionId);
        chatMessageRepository.deleteAll(messages);
        return ResponseEntity.noContent().build();
    }
}
