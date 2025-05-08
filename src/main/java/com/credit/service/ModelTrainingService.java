package com.credit.service;

import com.credit.model.TrainingData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class ModelTrainingService {
    private final CreditPredictionService predictionService;
    private final Random random = new Random();
    private final String modelPath = "models/credit_predictor.model";
    private Logistic classifier;

    public ModelTrainingService(CreditPredictionService predictionService) {
        this.predictionService = predictionService;
        loadModel();
    }

    private void loadModel() {
        try {
            File modelFile = new File(modelPath);
            if (modelFile.exists()) {
                log.info("Loading existing model from {}", modelPath);
                classifier = (Logistic) SerializationHelper.read(modelPath);
                log.info("Model loaded successfully");
            } else {
                log.info("No existing model found. Initializing new model");
                classifier = new Logistic();
            }
        } catch (Exception e) {
            log.error("Error loading model: {}", e.getMessage());
            classifier = new Logistic();
        }
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
            // Create attributes
            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(new Attribute("ageOfCredit"));
            attributes.add(new Attribute("derogatoryMarks"));
            attributes.add(new Attribute("ficoScore"));
            attributes.add(new Attribute("missedPayments"));
            attributes.add(new Attribute("creditInquiries"));
            attributes.add(new Attribute("totalAccounts"));
            attributes.add(new Attribute("creditLimit"));
            attributes.add(new Attribute("income"));
            
            // Create class attribute
            ArrayList<String> classValues = new ArrayList<>();
            classValues.add("Low");
            classValues.add("Medium");
            classValues.add("High");
            attributes.add(new Attribute("class", classValues));
            
            // Create dataset
            Instances dataset = new Instances("CreditEligibility", attributes, trainingData.size());
            dataset.setClassIndex(attributes.size() - 1);
            
            // Add instances
            for (TrainingData data : trainingData) {
                Instance instance = new DenseInstance(attributes.size());
                instance.setValue(attributes.get(0), data.getAgeOfCredit());
                instance.setValue(attributes.get(1), data.getDerogatoryMarks());
                instance.setValue(attributes.get(2), data.getFicoScore());
                instance.setValue(attributes.get(3), data.getMissedPayments());
                instance.setValue(attributes.get(4), data.getCreditInquiries());
                instance.setValue(attributes.get(5), data.getTotalAccounts());
                instance.setValue(attributes.get(6), data.getCreditLimit());
                instance.setValue(attributes.get(7), data.getIncome());
                instance.setValue(attributes.get(8), classValues.get(data.getEligibilityClass()));
                dataset.add(instance);
            }
            
            // Configure and train the classifier
            classifier.setMaxIts(100);
            classifier.setRidge(0.01);
            classifier.buildClassifier(dataset);
            
            // Save the model
            File modelDir = new File("models");
            if (!modelDir.exists()) {
                modelDir.mkdirs();
            }
            SerializationHelper.write(modelPath, classifier);
            
            log.info("Model training completed successfully");
        } catch (Exception e) {
            log.error("Error training model: {}", e.getMessage());
            throw new RuntimeException("Failed to train model", e);
        }
    }

    public double[] predictProbabilities(TrainingData data) {
        try {
            // Create attributes
            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(new Attribute("ageOfCredit"));
            attributes.add(new Attribute("derogatoryMarks"));
            attributes.add(new Attribute("ficoScore"));
            attributes.add(new Attribute("missedPayments"));
            attributes.add(new Attribute("creditInquiries"));
            attributes.add(new Attribute("totalAccounts"));
            attributes.add(new Attribute("creditLimit"));
            attributes.add(new Attribute("income"));
            
            // Create class attribute
            ArrayList<String> classValues = new ArrayList<>();
            classValues.add("Low");
            classValues.add("Medium");
            classValues.add("High");
            attributes.add(new Attribute("class", classValues));
            
            // Create dataset
            Instances dataset = new Instances("CreditEligibility", attributes, 1);
            dataset.setClassIndex(attributes.size() - 1);
            
            // Create instance
            Instance instance = new DenseInstance(attributes.size());
            instance.setValue(attributes.get(0), data.getAgeOfCredit());
            instance.setValue(attributes.get(1), data.getDerogatoryMarks());
            instance.setValue(attributes.get(2), data.getFicoScore());
            instance.setValue(attributes.get(3), data.getMissedPayments());
            instance.setValue(attributes.get(4), data.getCreditInquiries());
            instance.setValue(attributes.get(5), data.getTotalAccounts());
            instance.setValue(attributes.get(6), data.getCreditLimit());
            instance.setValue(attributes.get(7), data.getIncome());
            dataset.add(instance);
            
            // Get probability distribution
            double[] probs = classifier.distributionForInstance(instance);
            return probs;
        } catch (Exception e) {
            log.error("Error making prediction: {}", e.getMessage());
            return new double[]{1.0, 0.0, 0.0}; // Default to Low probability
        }
    }
} 