package com.digitalbanking.chatbot.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring AI - OpenAI ChatClient.
 *
 * La clé API est lue depuis la variable d'environnement OPENAI_API_KEY.
 * Elle ne doit JAMAIS être écrite en dur dans le code source.
 *
 * Définir avant le démarrage :
 *   export OPENAI_API_KEY=sk-...
 * Ou dans application.properties :
 *   spring.ai.openai.api-key=${OPENAI_API_KEY}
 */
@Configuration
public class SpringAiConfig {

    @Value("${spring.ai.openai.api-key:${OPENAI_API_KEY:not-configured}}")
    private String openAiApiKey;

    @Bean
    public ChatClient chatClient() {
        OpenAiApi openAiApi = new OpenAiApi(openAiApiKey);
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel("gpt-3.5-turbo")
                .withTemperature(0.7f)
                .withMaxTokens(1024)
                .build();
        return new OpenAiChatClient(openAiApi, options);
    }
}
