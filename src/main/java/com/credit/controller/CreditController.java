package com.credit.controller;

import com.credit.model.CreditData;
import com.credit.service.CreditPredictionService;
import com.credit.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/credit")
@RequiredArgsConstructor
public class CreditController {
    private final CreditPredictionService predictionService;
    private final GPTService gptService;

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predictEligibility(@RequestBody CreditData creditData) {
        double[] probabilities = predictionService.predictEligibility(creditData);
        
        Map<String, Object> response = new HashMap<>();
        response.put("probabilities", Map.of(
            "low", probabilities[0],
            "medium", probabilities[1],
            "high", probabilities[2]
        ));
        
        // Get the class with highest probability
        int maxIndex = 0;
        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > probabilities[maxIndex]) {
                maxIndex = i;
            }
        }
        
        String[] classes = {"Low", "Medium", "High"};
        response.put("predictedClass", classes[maxIndex]);
        
        // Get GPT explanation
        String explanation = gptService.getCreditExplanation(creditData, probabilities);
        response.put("explanation", explanation);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> getRecommendations(@RequestBody CreditData creditData) {
        double[] probabilities = predictionService.predictEligibility(creditData);
        String recommendations = gptService.getCardRecommendations(creditData, probabilities);
        
        Map<String, Object> response = new HashMap<>();
        response.put("probabilities", Map.of(
            "low", probabilities[0],
            "medium", probabilities[1],
            "high", probabilities[2]
        ));
        response.put("recommendations", recommendations);
        
        return ResponseEntity.ok(response);
    }
} 
