// ... existing code ...
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
// ... existing code ...
