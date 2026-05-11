package com.digitalbanking.chatbot.telegram;

import com.digitalbanking.chatbot.services.RagChatbotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Bot Telegram connecté au service RAG bancaire.
 *
 * Configuration requise dans application.properties :
 *   telegram.bot.token=${TELEGRAM_BOT_TOKEN}
 *   telegram.bot.username=${TELEGRAM_BOT_USERNAME}
 *
 * Les tokens sont injectés depuis des variables d'environnement (jamais en dur).
 */
@Component
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {

    private final RagChatbotService ragChatbotService;

    @Value("${telegram.bot.username:digitalbanking_bot}")
    private String botUsername;

    public TelegramBotService(
            @Value("${telegram.bot.token:TELEGRAM_TOKEN_NOT_SET}") String botToken,
            RagChatbotService ragChatbotService) {
        super(botToken);
        this.ragChatbotService = ragChatbotService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String chatId    = update.getMessage().getChatId().toString();
        String userText  = update.getMessage().getText();
        String username  = update.getMessage().getFrom().getUserName();

        log.info("[Telegram] Message de @{} (chatId={}): {}", username, chatId, userText);

        // Commande /start
        if ("/start".equals(userText)) {
            sendReply(chatId, """
                Bonjour ! Je suis votre assistant bancaire Digital Banking 🏦

                Je peux vous aider à :
                • Consulter des informations sur vos comptes
                • Comprendre les types de comptes disponibles
                • Répondre à vos questions bancaires

                Posez-moi votre question directement !
                """);
            return;
        }

        // Commande /help
        if ("/help".equals(userText)) {
            sendReply(chatId, """
                Commandes disponibles :
                /start - Message de bienvenue
                /help  - Afficher cette aide
                /stats - Statistiques globales de la banque

                Ou posez simplement votre question en texte libre !
                """);
            return;
        }

        // Commande /stats
        if ("/stats".equals(userText)) {
            String sessionId = "telegram_" + chatId;
            String response = ragChatbotService.chat(sessionId, "Donne-moi un résumé des statistiques de la banque", "TELEGRAM");
            sendReply(chatId, response);
            return;
        }

        // Message libre → RAG
        String sessionId = "telegram_" + chatId;
        String response = ragChatbotService.chat(sessionId, userText, "TELEGRAM");
        sendReply(chatId, response);
    }

    private void sendReply(String chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("Markdown")
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("[Telegram] Erreur envoi message : {}", e.getMessage());
        }
    }
}
