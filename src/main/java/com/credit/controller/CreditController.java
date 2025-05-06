package com.credit.controller;

import com.credit.model.CreditCard;
import com.credit.model.CreditData;
import com.credit.service.CreditPredictionService;
import com.credit.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credit")
@RequiredArgsConstructor
public class CreditController {
    private final CreditPredictionService predictionService;
    private final GPTService gptService;

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predictEligibility(@RequestBody CreditData creditData) {
        try {
            double probability = predictionService.predictEligibility(creditData);
            String explanation = gptService.getCreditExplanation(creditData, probability);

            return ResponseEntity.ok(Map.of(
                "probability", probability,
                "explanation", explanation
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error processing prediction: " + e.getMessage()));
        }
    }

    @PostMapping("/recommend")
    public ResponseEntity<List<CreditCard>> getCardRecommendations(
        @RequestParam int creditScore,
        @RequestParam double income,
        @RequestParam String creditHistory,
        @RequestBody List<CreditCard> availableCards
    ) {
        try {
            List<CreditCard> recommendations = gptService.getCardRecommendations(
                creditScore,
                income,
                creditHistory,
                availableCards
            );
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 