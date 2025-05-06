package com.credit.service;

import com.credit.model.CreditCard;
import com.credit.model.CreditData;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
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

    public GPTService(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
    }

    public String getCreditExplanation(CreditData creditData, double predictionProbability) {
        String prompt = String.format("""
            Based on the following credit information, provide a detailed analysis of the credit eligibility:
            
            Age of Credit: %.1f years
            Derogatory Marks: %d
            Credit Utilization: %.1f%%
            Missed Payments: %d
            Credit Inquiries: %d
            Total Accounts: %d
            Credit Limit: $%.2f
            Annual Income: $%.2f
            
            Eligibility Probability: %.2f%%
            
            Please provide:
            1. A detailed analysis of the credit profile
            2. Key factors affecting the eligibility
            3. Specific recommendations for improvement
            4. Expected timeline for improvement
            """,
            creditData.getAgeOfCredit(),
            creditData.getDerogatoryMarks(),
            creditData.getCreditUtilization(),
            creditData.getMissedPayments(),
            creditData.getCreditInquiries(),
            creditData.getTotalAccounts(),
            creditData.getCreditLimit(),
            creditData.getIncome(),
            predictionProbability * 100
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

    public List<CreditCard> getCardRecommendations(
        int creditScore,
        double income,
        String creditHistory,
        List<CreditCard> availableCards
    ) {
        StringBuilder cardsInfo = new StringBuilder();
        for (CreditCard card : availableCards) {
            cardsInfo.append(String.format("""
                Card: %s
                Type: %s
                Annual Fee: $%.2f
                APR: %.2f%%
                Rewards: %s
                Required Credit Score: %d
                Features: %s
                
                """,
                card.getName(),
                card.getType(),
                card.getAnnualFee(),
                card.getApr(),
                card.getRewards(),
                card.getCreditScoreRequired(),
                String.join(", ", card.getFeatures())
            ));
        }

        String prompt = String.format("""
            Based on the following user profile and available credit cards, provide personalized recommendations:
            
            User Profile:
            - Credit Score: %d
            - Annual Income: $%.2f
            - Credit History: %s
            
            Available Credit Cards:
            %s
            
            Please:
            1. Select the top 3 most suitable cards
            2. Explain why each card is recommended
            3. Highlight the best features for this user
            4. Provide application tips
            """,
            creditScore,
            income,
            creditHistory,
            cardsInfo
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

            String response = openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();

            // Match recommendations with card data
            List<CreditCard> recommendations = new ArrayList<>();
            for (CreditCard card : availableCards) {
                if (response.toLowerCase().contains(card.getName().toLowerCase())) {
                    CreditCard recommendedCard = new CreditCard();
                    recommendedCard.setName(card.getName());
                    recommendedCard.setType(card.getType());
                    recommendedCard.setAnnualFee(card.getAnnualFee());
                    recommendedCard.setApr(card.getApr());
                    recommendedCard.setRewards(card.getRewards());
                    recommendedCard.setCreditScoreRequired(card.getCreditScoreRequired());
                    recommendedCard.setFeatures(card.getFeatures());
                    recommendedCard.setGptExplanation(response);
                    recommendations.add(recommendedCard);
                    if (recommendations.size() >= 3) {
                        break;
                    }
                }
            }
            return recommendations;
        } catch (Exception e) {
            log.error("Error generating card recommendations", e);
            return List.of();
        }
    }
} 