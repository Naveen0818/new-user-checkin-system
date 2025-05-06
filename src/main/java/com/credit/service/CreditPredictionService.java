package com.credit.service;

import com.credit.model.CreditData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.function.Sigmoid;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;

@Slf4j
@Service
public class CreditPredictionService {
    private double[] weights;
    private final String modelPath = "models/credit_predictor.model";
    private final Sigmoid sigmoid = new Sigmoid();

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

    public void trainModel(float[][] features, float[] labels) {
        try {
            // Convert training data
            double[][] trainingFeatures = new double[features.length][9];
            double[] trainingLabels = new double[labels.length];
            
            for (int i = 0; i < features.length; i++) {
                trainingFeatures[i][0] = 1.0; // bias term
                System.arraycopy(features[i], 0, trainingFeatures[i], 1, 8);
                trainingLabels[i] = labels[i];
            }

            // Simple gradient descent
            double learningRate = 0.01;
            int epochs = 100;
            
            for (int epoch = 0; epoch < epochs; epoch++) {
                for (int i = 0; i < trainingFeatures.length; i++) {
                    RealVector featureVector = new ArrayRealVector(trainingFeatures[i]);
                    RealVector weightVector = new ArrayRealVector(weights);
                    double prediction = sigmoid.value(featureVector.dotProduct(weightVector));
                    double error = prediction - trainingLabels[i];
                    
                    // Update weights
                    for (int j = 0; j < weights.length; j++) {
                        weights[j] -= learningRate * error * trainingFeatures[i][j];
                    }
                }
            }

            // Save model
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath))) {
                oos.writeObject(weights);
            }
            log.info("Model saved successfully");
        } catch (Exception e) {
            log.error("Error training model", e);
        }
    }
} 