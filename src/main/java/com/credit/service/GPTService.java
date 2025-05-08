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

    public String getCardRecommendations(CreditData creditData, double[] probabilities, List<String> listOfOffers, String purchaseCategory) {
        StringBuilder recommendations = new StringBuilder();
        
        // Base recommendations based on credit profile
        if (probabilities[2] > 0.6) {
            recommendations.append("Based on your excellent credit profile, you have high approval chances for premium cards.\n");
        } else if (probabilities[1] > 0.4) {
            recommendations.append("Your credit profile suggests good approval chances for mid-tier cards.\n");
        } else {
            recommendations.append("Consider starter or secured cards to build your credit.\n");
        }

        // Add category-specific recommendations
        if (purchaseCategory != null) {
            switch (purchaseCategory.toLowerCase()) {
                case "travel":
                    recommendations.append("\nTravel Card Options:\n- Chase Sapphire Preferred\n- Capital One Venture\n- American Express Gold");
                    break;
                case "dining":
                    recommendations.append("\nDining Card Options:\n- American Express Gold\n- Chase Freedom Unlimited\n- Capital One Savor");
                    break;
                case "groceries":
                    recommendations.append("\nGrocery Card Options:\n- American Express Blue Cash Preferred\n- Chase Freedom\n- Discover it Cash Back");
                    break;
                case "gas":
                    recommendations.append("\nGas Card Options:\n- Citi Custom Cash\n- Chase Freedom\n- Discover it Gas & Restaurant");
                    break;
            }
        }

        // Add special offers if available
        if (listOfOffers != null && !listOfOffers.isEmpty()) {
            recommendations.append("\n\nCurrent Special Offers:\n");
            for (String offer : listOfOffers) {
                recommendations.append("- ").append(offer).append("\n");
            }
        }

        return recommendations.toString();
    }
} 