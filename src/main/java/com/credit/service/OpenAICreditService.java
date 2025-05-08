package com.credit.service;

import com.credit.model.CreditData;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenAICreditService {
    private final OpenAiService openAiService;
    private final CreditPredictionService predictionService;

    public OpenAICreditService(
            @Value("${openai.api.key}") String apiKey,
            CreditPredictionService predictionService) {
        this.openAiService = new OpenAiService(apiKey);
        this.predictionService = predictionService;
    }

    public Map<String, Object> predictWithAI(CreditData creditData) {
        // Get base prediction from statistical model
        double[] probabilities = predictionService.predictEligibility(creditData);
        
        // Create prompt for OpenAI
        String prompt = createPrompt(creditData, probabilities);
        
        // Get AI analysis
        String aiAnalysis = getAIAnalysis(prompt);
        
        // Combine results
        return Map.of(
            "probabilities", Map.of(
                "low", probabilities[0],
                "medium", probabilities[1],
                "high", probabilities[2]
            ),
            "aiAnalysis", aiAnalysis
        );
    }

    private String createPrompt(CreditData data, double[] probabilities) {
        return String.format("""
            Analyze this credit profile and provide a detailed assessment:
            
            Credit Profile:
            - FICO Score: %d
            - Age of Credit: %.1f years
            - Derogatory Marks: %d
            - Missed Payments: %d
            - Credit Inquiries: %d
            - Total Accounts: %d
            - Credit Limit: $%.2f
            - Annual Income: $%.2f
            
            Statistical Model Prediction:
            - Low Risk: %.1f%%
            - Medium Risk: %.1f%%
            - High Risk: %.1f%%
            
            Please provide:
            1. A detailed analysis of the credit profile
            2. Key factors affecting the credit score
            3. Specific recommendations for improvement
            4. Potential credit card options based on the profile
            5. Risk assessment and explanation
            """,
            data.getFicoScore(),
            data.getAgeOfCredit(),
            data.getDerogatoryMarks(),
            data.getMissedPayments(),
            data.getCreditInquiries(),
            data.getTotalAccounts(),
            data.getCreditLimit(),
            data.getIncome(),
            probabilities[0] * 100,
            probabilities[1] * 100,
            probabilities[2] * 100
        );
    }

    private String getAIAnalysis(String prompt) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), 
                "You are an expert credit analyst with deep knowledge of credit scoring, risk assessment, and credit card recommendations. " +
                "Provide detailed, accurate, and actionable analysis based on the credit profile data."));
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4")
                .messages(messages)
                .temperature(0.7)
                .maxTokens(1000)
                .build();

            return openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.error("Error getting AI analysis: {}", e.getMessage());
            return "Unable to generate AI analysis at this time.";
        }
    }
} 