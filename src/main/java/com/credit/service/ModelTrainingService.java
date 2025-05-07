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
            
            // Determine eligibility based on rules
            boolean eligible = determineEligibility(sample);
            sample.setEligible(eligible);
            
            data.add(sample);
        }
        
        return data;
    }

    private boolean determineEligibility(TrainingData data) {
        // Simple rule-based eligibility determination
        // These rules can be adjusted based on your requirements
        if (data.getDerogatoryMarks() > 3) return false;
        if (data.getMissedPayments() > 2) return false;
        if (data.getCreditUtilization() > 80) return false;
        if (data.getCreditInquiries() > 5) return false;
        
        // Income to credit limit ratio
        double incomeToLimitRatio = data.getIncome() / (data.getCreditLimit() + 1);
        if (incomeToLimitRatio < 0.1) return false;
        
        // Age of credit history
        if (data.getAgeOfCredit() < 1) return false;
        
        return true;
    }

    private void trainModel(List<TrainingData> trainingData) {
        try {
            // Convert training data to feature arrays
            List<double[]> features = new ArrayList<>();
            List<Double> labels = new ArrayList<>();
            
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
                labels.add(data.isEligible() ? 1.0 : 0.0);
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
