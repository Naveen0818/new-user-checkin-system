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
    
    // Feature scaling parameters
    private double[] featureMeans;
    private double[] featureStds;

    public CreditPredictionService() {
        loadModel();
    }

    private void loadModel() {
        try {
            File modelFile = new File(modelPath);
            if (modelFile.exists()) {
                log.info("Loading existing model from {}", modelPath);
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFile))) {
                    weights = (double[][]) ois.readObject();
                    featureMeans = (double[]) ois.readObject();
                    featureStds = (double[]) ois.readObject();
                    log.info("Model loaded successfully");
                }
            } else {
                log.info("No existing model found. Initializing with random weights");
                // Initialize weights with Xavier/Glorot initialization
                weights = new double[numClasses][9]; // 8 features + bias
                double scale = Math.sqrt(2.0 / (9 + numClasses));
                for (int i = 0; i < weights.length; i++) {
                    for (int j = 0; j < weights[i].length; j++) {
                        weights[i][j] = (random.nextDouble() * 2 - 1) * scale;
                    }
                }
                // Initialize scaling parameters
                featureMeans = new double[8];
                featureStds = new double[8];
                Arrays.fill(featureStds, 1.0); // Default to no scaling
            }
        } catch (Exception e) {
            log.error("Error loading model: {}", e.getMessage());
            weights = new double[numClasses][9];
            featureMeans = new double[8];
            featureStds = new double[8];
            Arrays.fill(featureStds, 1.0);
        }
    }

    public double[] predictEligibility(CreditData creditData) {
        try {
            // Create feature vector
            double[] features = new double[] {
                1.0, // bias term
                creditData.getAgeOfCredit(),
                creditData.getDerogatoryMarks(),
                creditData.getFicoScore(),
                creditData.getMissedPayments(),
                creditData.getCreditInquiries(),
                creditData.getTotalAccounts(),
                creditData.getCreditLimit(),
                creditData.getIncome()
            };
            
            // Scale features (excluding bias term)
            for (int i = 1; i < features.length; i++) {
                features[i] = (features[i] - featureMeans[i-1]) / featureStds[i-1];
            }

            log.debug("Feature vector: {}", Arrays.toString(features));

            // Calculate probabilities for each class
            double[] probs = predictProbabilities(features);
            log.debug("Predicted probabilities: {}", Arrays.toString(probs));
            return probs;
        } catch (Exception e) {
            log.error("Error making prediction: {}", e.getMessage());
            return new double[]{1.0, 0.0, 0.0}; // Default to Low probability
        }
    }

    public void trainModel(List<double[]> features, List<Integer> labels) {
        try {
            log.info("Starting model training with {} samples", features.size());
            int numSamples = features.size();
            
            // Calculate feature means and standard deviations (excluding bias term)
            featureMeans = new double[8];
            featureStds = new double[8];
            
            // Calculate means
            for (double[] feature : features) {
                for (int i = 1; i < feature.length; i++) {
                    featureMeans[i-1] += feature[i];
                }
            }
            for (int i = 0; i < featureMeans.length; i++) {
                featureMeans[i] /= numSamples;
            }
            
            // Calculate standard deviations
            for (double[] feature : features) {
                for (int i = 1; i < feature.length; i++) {
                    double diff = feature[i] - featureMeans[i-1];
                    featureStds[i-1] += diff * diff;
                }
            }
            for (int i = 0; i < featureStds.length; i++) {
                featureStds[i] = Math.sqrt(featureStds[i] / numSamples);
                if (featureStds[i] < 1e-8) featureStds[i] = 1.0; // Prevent division by zero
            }
            
            // Scale features
            for (double[] feature : features) {
                for (int i = 1; i < feature.length; i++) {
                    feature[i] = (feature[i] - featureMeans[i-1]) / featureStds[i-1];
                }
            }
            
            // Training parameters
            double learningRate = 0.01;
            int numEpochs = 100;
            double lambda = 0.01; // L2 regularization parameter
            
            // Initialize weights with Xavier/Glorot initialization
            weights = new double[numClasses][9];
            double scale = Math.sqrt(2.0 / (9 + numClasses));
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i].length; j++) {
                    weights[i][j] = (random.nextDouble() * 2 - 1) * scale;
                }
            }
            
            // Gradient descent
            for (int epoch = 0; epoch < numEpochs; epoch++) {
                double[][] gradients = new double[numClasses][weights[0].length];
                double totalLoss = 0.0;
                
                // Compute gradients
                for (int i = 0; i < numSamples; i++) {
                    double[] probs = predictProbabilities(features.get(i));
                    int trueClass = labels.get(i);
                    
                    // Cross-entropy loss
                    totalLoss -= Math.log(probs[trueClass] + 1e-15);
                    
                    for (int c = 0; c < numClasses; c++) {
                        double error = probs[c] - (c == trueClass ? 1.0 : 0.0);
                        for (int j = 0; j < weights[c].length; j++) {
                            gradients[c][j] += error * features.get(i)[j];
                        }
                    }
                }
                
                // Add L2 regularization term to loss
                double regTerm = 0.0;
                for (int c = 0; c < numClasses; c++) {
                    for (int j = 0; j < weights[c].length; j++) {
                        regTerm += weights[c][j] * weights[c][j];
                    }
                }
                totalLoss += (lambda / 2) * regTerm;
                
                // Update weights with L2 regularization
                for (int c = 0; c < numClasses; c++) {
                    for (int j = 0; j < weights[c].length; j++) {
                        weights[c][j] -= learningRate * (gradients[c][j] / numSamples + lambda * weights[c][j]);
                    }
                }

                if ((epoch + 1) % 10 == 0) {
                    log.info("Epoch {}/{}, Loss: {}", epoch + 1, numEpochs, totalLoss / numSamples);
                }
            }
            
            // Save the trained model
            saveModel();
            log.info("Model training completed successfully");
            
        } catch (Exception e) {
            log.error("Error training model: {}", e.getMessage());
            throw new RuntimeException("Failed to train model", e);
        }
    }

    private double[] predictProbabilities(double[] features) {
        double[] logits = new double[numClasses];
        double maxLogit = Double.NEGATIVE_INFINITY;
        
        // Calculate logits and track maximum for numerical stability
        for (int c = 0; c < numClasses; c++) {
            logits[c] = 0;
            for (int j = 0; j < features.length; j++) {
                logits[c] += weights[c][j] * features[j];
            }
            maxLogit = Math.max(maxLogit, logits[c]);
        }
        
        // Apply softmax with numerical stability
        double[] probs = new double[numClasses];
        double sum = 0.0;
        
        for (int i = 0; i < numClasses; i++) {
            probs[i] = Math.exp(logits[i] - maxLogit);
            sum += probs[i];
        }
        
        for (int i = 0; i < numClasses; i++) {
            probs[i] /= sum;
        }
        
        return probs;
    }

    private void saveModel() {
        try {
            // Create models directory if it doesn't exist
            File modelDir = new File("models");
            if (!modelDir.exists()) {
                modelDir.mkdirs();
            }
            
            // Save the model and scaling parameters
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath))) {
                oos.writeObject(weights);
                oos.writeObject(featureMeans);
                oos.writeObject(featureStds);
                log.info("Model saved successfully to {}", modelPath);
            }
        } catch (Exception e) {
            log.error("Error saving model: {}", e.getMessage());
            throw new RuntimeException("Failed to save model", e);
        }
    }
}