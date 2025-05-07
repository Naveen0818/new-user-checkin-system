package com.credit.service;

import com.credit.model.TrainingData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class ModelTrainingService {
    private final CreditPredictionService predictionService;
    private final Random random = new Random();

    public ModelTrainingService(CreditPredictionService predictionService) {
        this.predictionService = predictionService;
    }

    public void trainModelWithSampleData() {
        List<TrainingData> trainingData = generateSampleData(1000);
        trainModel(trainingData);
    }

    private List<TrainingData> generateSampleData(int size) {
        List<TrainingData> data = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            TrainingData sample = new TrainingData();
            
            // Generate realistic credit data
            sample.setAgeOfCredit(random.nextDouble() * 20); // 0-20 years
            sample.setDerogatoryMarks(random.nextInt(10)); // 0-9 marks
            sample.setCreditUtilization(random.nextDouble() * 100); // 0-100%
            sample.setMissedPayments(random.nextInt(12)); // 0-11 missed payments
            sample.setCreditInquiries(random.nextInt(10)); // 0-9 inquiries
            sample.setTotalAccounts(random.nextInt(20)); // 0-19 accounts
            sample.setCreditLimit(random.nextDouble() * 50000); // $0-$50,000
            sample.setIncome(random.nextDouble() * 200000); // $0-$200,000
            
            // Determine eligibility class based on rules
            sample.setEligibilityClass(determineEligibilityClass(sample));
            
            data.add(sample);
        }
        
        return data;
    }

    private int determineEligibilityClass(TrainingData data) {
        // Calculate a score based on various factors
        double score = 0;
        
        // Credit history (0-20 points)
        score += data.getAgeOfCredit() * 1.0;
        
        // Derogatory marks (0-20 points, negative impact)
        score -= data.getDerogatoryMarks() * 2.0;
        
        // Credit utilization (0-20 points, negative impact)
        score -= data.getCreditUtilization() * 0.2;
        
        // Payment history (0-20 points, negative impact)
        score -= data.getMissedPayments() * 1.5;
        
        // Credit inquiries (0-10 points, negative impact)
        score -= data.getCreditInquiries() * 1.0;
        
        // Account diversity (0-10 points)
        score += data.getTotalAccounts() * 0.5;
        
        // Income to credit limit ratio (0-20 points)
        double incomeToLimitRatio = data.getIncome() / (data.getCreditLimit() + 1);
        score += Math.min(incomeToLimitRatio * 10, 20);
        
        // Determine class based on score
        if (score < 30) return 0; // Low
        if (score < 60) return 1; // Medium
        return 2; // High
    }

    private void trainModel(List<TrainingData> trainingData) {
        try {
            // Convert training data to feature arrays
            List<double[]> features = new ArrayList<>();
            List<Integer> labels = new ArrayList<>();
            
            for (TrainingData data : trainingData) {
                features.add(new double[] {
                    data.getAgeOfCredit(),
                    data.getDerogatoryMarks(),
                    data.getCreditUtilization(),
                    data.getMissedPayments(),
                    data.getCreditInquiries(),
                    data.getTotalAccounts(),
                    data.getCreditLimit(),
                    data.getIncome()
                });
                labels.add(data.getEligibilityClass());
            }
            
            // Train the model
            predictionService.trainModel(features, labels);
            log.info("Model training completed successfully");
            
        } catch (Exception e) {
            log.error("Error training model", e);
            throw new RuntimeException("Failed to train model", e);
        }
    }
} 