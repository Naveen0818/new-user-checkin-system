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
            
            // Generate FICO score first as it influences other factors
            int ficoScore = generateFicoScore();
            sample.setFicoScore(ficoScore);
            
            // Generate correlated features based on FICO score
            if (ficoScore >= 740) {
                // Excellent credit profile
                sample.setAgeOfCredit(10 + random.nextDouble() * 10); // 10-20 years
                sample.setDerogatoryMarks(random.nextDouble() < 0.1 ? 1 : 0); // 10% chance of 1 mark
                sample.setMissedPayments(random.nextDouble() < 0.05 ? 1 : 0); // 5% chance of 1 missed payment
                sample.setCreditInquiries(random.nextInt(3)); // 0-2 inquiries
                sample.setTotalAccounts(5 + random.nextInt(10)); // 5-14 accounts
                sample.setCreditLimit(30000 + random.nextDouble() * 20000); // $30k-$50k
                sample.setIncome(80000 + random.nextDouble() * 120000); // $80k-$200k
            } else if (ficoScore >= 670) {
                // Good credit profile
                sample.setAgeOfCredit(5 + random.nextDouble() * 10); // 5-15 years
                sample.setDerogatoryMarks(random.nextDouble() < 0.3 ? random.nextInt(2) : 0); // 30% chance of 0-1 marks
                sample.setMissedPayments(random.nextDouble() < 0.2 ? random.nextInt(2) : 0); // 20% chance of 0-1 missed payments
                sample.setCreditInquiries(random.nextInt(4)); // 0-3 inquiries
                sample.setTotalAccounts(3 + random.nextInt(7)); // 3-9 accounts
                sample.setCreditLimit(15000 + random.nextDouble() * 15000); // $15k-$30k
                sample.setIncome(50000 + random.nextDouble() * 30000); // $50k-$80k
            } else {
                // Poor credit profile
                sample.setAgeOfCredit(random.nextDouble() * 5); // 0-5 years
                sample.setDerogatoryMarks(random.nextInt(5)); // 0-4 marks
                sample.setMissedPayments(random.nextInt(6)); // 0-5 missed payments
                sample.setCreditInquiries(random.nextInt(8)); // 0-7 inquiries
                sample.setTotalAccounts(random.nextInt(4)); // 0-3 accounts
                sample.setCreditLimit(random.nextDouble() * 10000); // $0-$10k
                sample.setIncome(random.nextDouble() * 50000); // $0-$50k
            }
            
            // Determine eligibility class based on rules
            sample.setEligibilityClass(determineEligibilityClass(sample));
            
            data.add(sample);
        }
        
        return data;
    }

    private int generateFicoScore() {
        double r = random.nextDouble();
        if (r < 0.2) {
            // 20% chance of excellent credit
            return 740 + random.nextInt(61); // 740-800
        } else if (r < 0.5) {
            // 30% chance of good credit
            return 670 + random.nextInt(70); // 670-739
        } else if (r < 0.8) {
            // 30% chance of fair credit
            return 580 + random.nextInt(90); // 580-669
        } else {
            // 20% chance of poor credit
            return 300 + random.nextInt(280); // 300-579
        }
    }

    private int determineEligibilityClass(TrainingData data) {
        double score = 0.0;
        
        // Credit history (0-15 points)
        score += Math.min(data.getAgeOfCredit() * 1.5, 15.0);
        
        // Derogatory marks (severe negative impact)
        score -= Math.pow(data.getDerogatoryMarks() + 1, 2) * 10.0; // Exponential penalty
        
        // FICO Score (0-30 points)
        int ficoScore = data.getFicoScore();
        if (ficoScore >= 800) {
            score += 30; // Exceptional
        } else if (ficoScore >= 740) {
            score += 25; // Very Good
        } else if (ficoScore >= 670) {
            score += 20; // Good
        } else if (ficoScore >= 580) {
            score += 10; // Fair
        } else {
            score += 5;  // Poor
        }
        
        // Payment history (severe negative impact)
        score -= Math.pow(data.getMissedPayments() + 1, 2) * 8.0; // Exponential penalty
        
        // Credit inquiries (0-5 points, negative impact)
        score -= data.getCreditInquiries() * 2.0;
        
        // Account diversity (0-10 points)
        score += Math.min(data.getTotalAccounts() * 2.0, 10.0);
        
        // Income to credit limit ratio (0-10 points)
        double incomeToLimitRatio = data.getIncome() / (data.getCreditLimit() + 1);
        score += Math.min(incomeToLimitRatio * 2.0, 10.0);
        
        // Determine class based on score
        if (score >= 60) return 2; // High
        if (score >= 30) return 1; // Medium
        return 0; // Low
    }

    private void trainModel(List<TrainingData> trainingData) {
        try {
            // Extract features and labels
            List<double[]> features = new ArrayList<>();
            List<Integer> labels = new ArrayList<>();
            
            for (TrainingData data : trainingData) {
                double[] featureVector = new double[] {
                    1.0, // bias term
                    data.getAgeOfCredit(),
                    data.getDerogatoryMarks(),
                    data.getFicoScore(),
                    data.getMissedPayments(),
                    data.getCreditInquiries(),
                    data.getTotalAccounts(),
                    data.getCreditLimit(),
                    data.getIncome()
                };
                features.add(featureVector);
                labels.add(data.getEligibilityClass());
            }
            
            // Train the model
            predictionService.trainModel(features, labels);
            log.info("Model training completed successfully");
        } catch (Exception e) {
            log.error("Error training model: {}", e.getMessage());
            throw new RuntimeException("Failed to train model", e);
        }
    }
} 
