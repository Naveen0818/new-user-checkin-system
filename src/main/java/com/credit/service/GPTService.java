package com.credit.service;

import com.credit.model.CreditCard;
import com.credit.model.CreditData;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GPTService {
    private final OpenAiService openAiService;

    @Value("${openai.api.key:dummy-key}")
    private String apiKey;

    public GPTService(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
    }

    public String getCreditExplanation(CreditData creditData, double[] probabilities) {
        // Provide a default explanation without calling OpenAI API
        return String.format(
            "Credit Profile Analysis:\n" +
            "- Age of Credit: %.1f years\n" +
            "- FICO Score: %d (Excellent: 800+, Very Good: 740-799, Good: 670-739)\n" +
            "- Payment History: %d missed payments, %d derogatory marks\n" +
            "- Credit Mix: %d total accounts\n" +
            "- Credit Inquiries: %d recent inquiries\n\n" +
            "Prediction Probabilities:\n" +
            "- Low: %.1f%%\n" +
            "- Medium: %.1f%%\n" +
            "- High: %.1f%%",
            creditData.getAgeOfCredit(),
            creditData.getFicoScore(),
            creditData.getMissedPayments(),
            creditData.getDerogatoryMarks(),
            creditData.getTotalAccounts(),
            creditData.getCreditInquiries(),
            probabilities[0] * 100,
            probabilities[1] * 100,
            probabilities[2] * 100
        );
    }

    public String getCardRecommendations(CreditData creditData, double[] probabilities) {
        // Provide default recommendations without calling OpenAI API
        if (probabilities[2] > 0.5) { // High probability
            return "Based on your excellent credit profile, you may qualify for premium credit cards with high rewards.";
        } else if (probabilities[1] > 0.5) { // Medium probability
            return "Consider mid-tier credit cards with moderate rewards and benefits.";
        } else { // Low probability
            return "Focus on secured credit cards or credit builder products to improve your credit score.";
        }
    }
} 
