package com.credit.service;

import com.credit.model.CreditData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.function.Sigmoid;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class CreditPredictionService {
    private double[] weights;
    private final String modelPath = "models/credit_predictor.model";
    private final Sigmoid sigmoid = new Sigmoid();
    private Random random = new Random();

    public CreditPredictionService() {
        loadModel();
    }

    private void loadModel() {
        try {
            if (new File(modelPath).exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelPath))) {
                    weights = (double[]) ois.readObject();
                }
            } else {
                // Initialize weights with small random values
                weights = new double[9]; // 8 features + bias
                for (int i = 0; i < weights.length; i++) {
                    weights[i] = Math.random() * 0.1 - 0.05;
                }
            }
        } catch (Exception e) {
            log.error("Error loading model", e);
            weights = new double[9];
        }
    }

    public double predictEligibility(CreditData creditData) {
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

            // Calculate prediction
            RealVector featureVector = new ArrayRealVector(features);
            RealVector weightVector = new ArrayRealVector(weights);
            double prediction = sigmoid.value(featureVector.dotProduct(weightVector));
            return prediction;
        } catch (Exception e) {
            log.error("Error making prediction", e);
            return 0.0;
        }
    }

    public void trainModel(List<double[]> features, List<Double> labels) {
        try {
            int numSamples = features.size();
            int numFeatures = 8; // Number of features in CreditData
            
            // Initialize weights if not already done
            if (weights == null) {
                weights = new double[numFeatures + 1]; // +1 for bias term
                for (int i = 0; i < weights.length; i++) {
                    weights[i] = random.nextDouble() * 0.01 - 0.005; // Small random values
                }
            }
            
            // Convert features to double[][] array
            double[][] trainingFeatures = new double[numSamples][numFeatures + 1];
            for (int i = 0; i < numSamples; i++) {
                trainingFeatures[i][0] = 1.0; // Bias term
                System.arraycopy(features.get(i), 0, trainingFeatures[i], 1, numFeatures);
            }
            
            // Convert labels to double[] array
            double[] trainingLabels = labels.stream().mapToDouble(Double::doubleValue).toArray();
            
            // Training parameters
            double learningRate = 0.01;
            int numEpochs = 100;
            
            // Gradient descent
            for (int epoch = 0; epoch < numEpochs; epoch++) {
                double[] gradients = new double[weights.length];
                
                // Compute gradients
                for (int i = 0; i < numSamples; i++) {
                    double prediction = predict(trainingFeatures[i]);
                    double error = prediction - trainingLabels[i];
                    
                    for (int j = 0; j < weights.length; j++) {
                        gradients[j] += error * trainingFeatures[i][j];
                    }
                }
                
                // Update weights
                for (int j = 0; j < weights.length; j++) {
                    weights[j] -= learningRate * gradients[j] / numSamples;
                }
            }
            
            // Save the trained model
            saveModel();
            
        } catch (Exception e) {
            log.error("Error training model", e);
            throw new RuntimeException("Failed to train model", e);
        }
    }

    private double predict(double[] features) {
        RealVector featureVector = new ArrayRealVector(features);
        RealVector weightVector = new ArrayRealVector(weights);
        return sigmoid.value(featureVector.dotProduct(weightVector));
    }

    private void saveModel() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath))) {
            oos.writeObject(weights);
        } catch (Exception e) {
            log.error("Error saving model", e);
        }
    }
} 
