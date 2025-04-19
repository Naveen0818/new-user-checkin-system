/*
package com.example.checkin.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

*/
/**
 * Configuration for OpenAI service.
 *//*

@Configuration
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.timeout:30}")
    private int timeoutSeconds;

    */
/**
     * Creates and configures the OpenAI service client.
     * 
     * @return Configured OpenAiService instance
     *//*

    @Bean
    public OpenAiService openAiService() {
        // Try to get API key from environment if not in properties
        String apiKey = openAiApiKey.isEmpty() || "your-api-key-here".equals(openAiApiKey) ? 
                        System.getenv("OPENAI_API_KEY") : openAiApiKey;
        
        if (apiKey == null || apiKey.isEmpty() || "your-api-key-here".equals(apiKey)) {
            throw new IllegalStateException("OpenAI API key is not configured. Please set it in application.properties or as an environment variable OPENAI_API_KEY");
        }
        
        return new OpenAiService(apiKey, Duration.ofSeconds(timeoutSeconds));
    }
}
*/
