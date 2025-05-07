package com.credit.service;

import com.credit.model.CreditData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class CreditPredictionService {
    private double[][] weights; // [numClasses][numFeatures+1]
    private final String modelPath = "models/credit_predictor.model";
    private final int numClasses = 3; // Low, Medium, High
    private final Random random = new Random();

    public CreditPredictionService() {
        loadModel();
    }

    private void loadModel() {
        try {
            if (new File(modelPath).exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelPath))) {
                    weights = (double[][]) ois.readObject();
                }
            } else {
                // Initialize weights with small random values
                weights = new double[numClasses][9]; // 8 features + bias
                for (int i = 0; i < weights.length; i++) {
                    for (int j = 0; j < weights[i].length; j++) {
                        weights[i][j] = random.nextDouble() * 0.01 - 0.005;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error loading model", e);
            weights = new double[numClasses][9];
        }
    }

    public double[] predictEligibility(CreditData creditData) {
        try {
            // Create feature vector
            double[] features = new double[] {
                1.0, // bias term
                creditData.getAgeOfCredit(),
                creditData.getDerogatoryMarks(),
                creditData.getCreditUtilization(),
                creditData.getMissedPayments(),
                creditData.getCreditInquiries(),
                creditData.getTotalAccounts(),
                creditData.getCreditLimit(),
                creditData.getIncome()
            };

            // Calculate probabilities for each class
            return predictProbabilities(features);
        } catch (Exception e) {
            log.error("Error making prediction", e);
            return new double[]{1.0, 0.0, 0.0}; // Default to Low probability
        }
    }

    public void trainModel(List<double[]> features, List<Integer> labels) {
        try {
            int numSamples = features.size();
            int numFeatures = 8; // Number of features in CreditData
            
            // Initialize weights if not already done
            if (weights == null) {
                weights = new double[numClasses][numFeatures + 1]; // +1 for bias term
                for (int i = 0; i < weights.length; i++) {
                    for (int j = 0; j < weights[i].length; j++) {
                        weights[i][j] = random.nextDouble() * 0.01 - 0.005;
                    }
                }
            }
            
            // Convert features to double[][] array
            double[][] trainingFeatures = new double[numSamples][numFeatures + 1];
            for (int i = 0; i < numSamples; i++) {
                trainingFeatures[i][0] = 1.0; // Bias term
                System.arraycopy(features.get(i), 0, trainingFeatures[i], 1, numFeatures);
            }
            
            // Training parameters
            double learningRate = 0.01;
            int numEpochs = 100;
            
            // Gradient descent
            for (int epoch = 0; epoch < numEpochs; epoch++) {
                double[][] gradients = new double[numClasses][weights[0].length];
                
                // Compute gradients
                for (int i = 0; i < numSamples; i++) {
                    double[] probs = predictProbabilities(trainingFeatures[i]);
                    int trueClass = labels.get(i);
                    
                    for (int c = 0; c < numClasses; c++) {
                        double error = probs[c] - (c == trueClass ? 1.0 : 0.0);
                        for (int j = 0; j < weights[c].length; j++) {
                            gradients[c][j] += error * trainingFeatures[i][j];
                        }
                    }
                }
                
                // Update weights
                for (int c = 0; c < numClasses; c++) {
                    for (int j = 0; j < weights[c].length; j++) {
                        weights[c][j] -= learningRate * gradients[c][j] / numSamples;
                    }
                }
            }
            
            // Save the trained model
            saveModel();
            
        } catch (Exception e) {
            log.error("Error training model", e);
            throw new RuntimeException("Failed to train model", e);
        }
    }

    private double[] predictProbabilities(double[] features) {
        double[] logits = new double[numClasses];
        
        // Calculate logits for each class
        for (int c = 0; c < numClasses; c++) {
            logits[c] = 0;
            for (int j = 0; j < features.length; j++) {
                logits[c] += weights[c][j] * features[j];
            }
        }
        
        // Apply softmax to get probabilities
        return softmax(logits);
    }

    private double[] softmax(double[] logits) {
        double max = Arrays.stream(logits).max().orElse(0);
        double sum = 0.0;
        double[] exps = new double[logits.length];
        
        for (int i = 0; i < logits.length; i++) {
            exps[i] = Math.exp(logits[i] - max);
            sum += exps[i];
        }
        
        for (int i = 0; i < exps.length; i++) {
            exps[i] /= sum;
        }
        
        return exps;
    }

    private void saveModel() {
        try {
            // Create models directory if it doesn't exist
            File modelDir = new File("models");
            if (!modelDir.exists()) {
                modelDir.mkdirs();
            }
            
            // Save the model
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath))) {
                oos.writeObject(weights);
                log.info("Model saved successfully to {}", modelPath);
            }
        } catch (Exception e) {
            log.error("Error saving model", e);
            throw new RuntimeException("Failed to save model", e);
        }
    }
} 