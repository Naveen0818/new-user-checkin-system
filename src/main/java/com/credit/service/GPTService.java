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
@RequiredArgsConstructor
public class GPTService {
    @Value("${openai.api.key}")
    private String apiKey;

    private final OpenAiService openAiService;

    public String getCreditExplanation(CreditData creditData, double[] probabilities) {
        String prompt = String.format(
            "Based on the following credit profile:\n" +
            "Age of Credit: %.1f years\n" +
            "Derogatory Marks: %d\n" +
            "Credit Utilization: %.1f%%\n" +
            "Missed Payments: %d\n" +
            "Credit Inquiries: %d\n" +
            "Total Accounts: %d\n" +
            "Credit Limit: $%.2f\n" +
            "Income: $%.2f\n\n" +
            "The model predicts the following probabilities:\n" +
            "Low: %.1f%%\n" +
            "Medium: %.1f%%\n" +
            "High: %.1f%%\n\n" +
            "Please provide a detailed explanation of this credit profile and the prediction, " +
            "including factors that influenced the decision and suggestions for improvement.",
            creditData.getAgeOfCredit(),
            creditData.getDerogatoryMarks(),
            creditData.getCreditUtilization(),
            creditData.getMissedPayments(),
            creditData.getCreditInquiries(),
            creditData.getTotalAccounts(),
            creditData.getCreditLimit(),
            creditData.getIncome(),
            probabilities[0] * 100,
            probabilities[1] * 100,
            probabilities[2] * 100
        );

        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", "You are a credit analysis expert. Provide detailed, professional analysis of credit profiles."));
            messages.add(new ChatMessage("user", prompt));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .temperature(0.7)
                .maxTokens(500)
                .build();

            return openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.error("Error generating credit explanation", e);
            return "Error generating explanation: " + e.getMessage();
        }
    }

    public String getCardRecommendations(CreditData creditData, double[] probabilities) {
        String prompt = String.format(
            "Based on the following credit profile:\n" +
            "Age of Credit: %.1f years\n" +
            "Derogatory Marks: %d\n" +
            "Credit Utilization: %.1f%%\n" +
            "Missed Payments: %d\n" +
            "Credit Inquiries: %d\n" +
            "Total Accounts: %d\n" +
            "Credit Limit: $%.2f\n" +
            "Income: $%.2f\n\n" +
            "The model predicts the following probabilities:\n" +
            "Low: %.1f%%\n" +
            "Medium: %.1f%%\n" +
            "High: %.1f%%\n\n" +
            "Please provide specific credit card recommendations based on this profile, " +
            "including why each card is suitable and what benefits they offer.",
            creditData.getAgeOfCredit(),
            creditData.getDerogatoryMarks(),
            creditData.getCreditUtilization(),
            creditData.getMissedPayments(),
            creditData.getCreditInquiries(),
            creditData.getTotalAccounts(),
            creditData.getCreditLimit(),
            creditData.getIncome(),
            probabilities[0] * 100,
            probabilities[1] * 100,
            probabilities[2] * 100
        );

        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", "You are a credit card recommendation expert. Provide detailed, personalized card recommendations."));
            messages.add(new ChatMessage("user", prompt));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .temperature(0.7)
                .maxTokens(1000)
                .build();

            return openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.error("Error generating card recommendations", e);
            return "Error generating recommendations: " + e.getMessage();
        }
    }
} 
