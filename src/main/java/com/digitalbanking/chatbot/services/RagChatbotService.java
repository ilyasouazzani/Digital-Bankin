package com.digitalbanking.chatbot.services;

import com.digitalbanking.chatbot.entities.ChatMessage;
import com.digitalbanking.chatbot.repositories.ChatMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service RAG (Retrieval-Augmented Generation).
 *
 * Architecture :
 *   1. Récupère le contexte bancaire documentaire (knowledge base).
 *   2. Récupère les données en temps réel via BankingContextService (function calling).
 *   3. Construit un historique de conversation pour le contexte multi-tours.
 *   4. Soumet le tout au LLM (OpenAI GPT) via Spring AI ChatClient.
 *   5. Persiste chaque échange en base.
 */
@Service
@AllArgsConstructor
@Slf4j
public class RagChatbotService {

    private final ChatClient chatClient;
    private final ChatMessageRepository chatMessageRepository;
    private final BankingContextService bankingContextService;

    /**
     * Traite un message utilisateur et retourne la réponse de l'IA.
     *
     * @param sessionId identifiant unique de la session (username ou chatId Telegram)
     * @param userInput message de l'utilisateur
     * @param source    "WEB" ou "TELEGRAM"
     * @return réponse textuelle du LLM
     */
    public String chat(String sessionId, String userInput, String source) {
        log.info("[RAG] Session={} Source={} Message={}", sessionId, source, userInput);

        // 1. Construire le prompt système avec le contexte RAG
        String systemPrompt = buildSystemPrompt(userInput);

        // 2. Récupérer l'historique de la session (10 derniers messages)
        List<Message> conversationHistory = buildConversationHistory(sessionId);

        // 3. Ajouter le message utilisateur courant
        conversationHistory.add(new UserMessage(userInput));

        // 4. Construire le prompt complet
        List<Message> allMessages = new ArrayList<>();
        allMessages.add(new SystemMessage(systemPrompt));
        allMessages.addAll(conversationHistory);

        // 5. Appel au LLM via Spring AI
        String response;
        try {
            Prompt prompt = new Prompt(allMessages);
            response = chatClient.call(prompt).getResult().getOutput().getContent();
        } catch (Exception e) {
            log.error("[RAG] Erreur appel LLM : {}", e.getMessage());
            response = "Je suis temporairement indisponible. Veuillez réessayer dans quelques instants.";
        }

        // 6. Persister le message utilisateur et la réponse
        persistMessage(sessionId, "user", userInput, source);
        persistMessage(sessionId, "assistant", response, source);

        return response;
    }

    /**
     * Construit le prompt système enrichi avec le contexte RAG.
     * Détecte les intentions de l'utilisateur pour appeler les bonnes fonctions.
     */
    private String buildSystemPrompt(String userInput) {
        StringBuilder context = new StringBuilder();

        // Contexte documentaire (knowledge base statique)
        context.append(bankingContextService.getBankingKnowledgeBase());
        context.append("\n\n");

        // Function Calling : données en temps réel selon l'intention détectée
        String lowerInput = userInput.toLowerCase();

        if (lowerInput.contains("résumé") || lowerInput.contains("statistique")
                || lowerInput.contains("total") || lowerInput.contains("combien")) {
            context.append("=== DONNÉES TEMPS RÉEL ===\n");
            context.append(bankingContextService.getBankingSummary());
            context.append("\n");
        }

        if (lowerInput.contains("solde") || lowerInput.contains("compte")
                || lowerInput.contains("balance")) {
            // Extraction basique d'UUID dans le message
            String accountId = extractAccountId(userInput);
            if (accountId != null) {
                context.append("=== SOLDE COMPTE ===\n");
                context.append(bankingContextService.getAccountBalance(accountId));
                context.append("\n");
            }
        }

        if (lowerInput.contains("client") || lowerInput.contains("customer")) {
            String name = extractName(userInput);
            if (name != null) {
                context.append("=== INFO CLIENT ===\n");
                context.append(bankingContextService.getCustomerInfo(name));
                context.append("\n");
            }
        }

        return """
            Tu es un assistant bancaire intelligent et professionnel pour Digital Banking.
            Tu réponds en français, de manière concise, précise et bienveillante.
            Tu utilises exclusivement les données bancaires fournies dans le contexte.
            Si une information n'est pas disponible, dis-le clairement sans inventer.
            Ne révèle jamais les détails techniques internes (JWT, BCrypt, etc.) aux utilisateurs.

            CONTEXTE BANCAIRE :
            """ + context;
    }

    private List<Message> buildConversationHistory(String sessionId) {
        List<Message> history = new ArrayList<>();
        List<ChatMessage> pastMessages = chatMessageRepository
                .findTop10BySessionIdOrderByTimestampDesc(sessionId);

        // Inverser pour ordre chronologique
        for (int i = pastMessages.size() - 1; i >= 0; i--) {
            ChatMessage msg = pastMessages.get(i);
            if ("user".equals(msg.getRole())) {
                history.add(new UserMessage(msg.getContent()));
            } else {
                history.add(new AssistantMessage(msg.getContent()));
            }
        }
        return history;
    }

    private void persistMessage(String sessionId, String role, String content, String source) {
        ChatMessage msg = new ChatMessage();
        msg.setSessionId(sessionId);
        msg.setRole(role);
        msg.setContent(content);
        msg.setSource(source);
        chatMessageRepository.save(msg);
    }

    /** Extrait un UUID v4 d'un texte (pour le function calling sur les comptes). */
    private String extractAccountId(String text) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
            java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher m = p.matcher(text);
        return m.find() ? m.group() : null;
    }

    /** Extrait un nom propre approximatif après "client" ou "de". */
    private String extractName(String text) {
        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].equalsIgnoreCase("client") || words[i].equalsIgnoreCase("de")) {
                String candidate = words[i + 1].replaceAll("[^a-zA-ZÀ-ÿ]", "");
                if (candidate.length() > 2) return candidate;
            }
        }
        return null;
    }
}
