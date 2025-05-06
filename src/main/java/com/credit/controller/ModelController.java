package com.credit.controller;

import com.credit.service.ModelTrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/model")
@RequiredArgsConstructor
public class ModelController {
    private final ModelTrainingService modelTrainingService;

    @PostMapping("/train")
    public ResponseEntity<String> trainModel() {
        try {
            modelTrainingService.trainModelWithSampleData();
            return ResponseEntity.ok("Model training completed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error training model: " + e.getMessage());
        }
    }
} 