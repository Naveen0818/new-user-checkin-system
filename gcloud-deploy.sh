#!/bin/bash
set -e

# Variables
PROJECT_ID="your-gcp-project-id"
SERVICE_NAME="checkin-system"
REGION="us-central1"
IMAGE_NAME="gcr.io/$PROJECT_ID/$SERVICE_NAME:latest"

# Build and package the application
./mvnw clean package -DskipTests

# Build the container image
echo "Building container image..."
docker build -t $IMAGE_NAME .

# Push the image to Google Container Registry
echo "Pushing image to Google Container Registry..."
docker push $IMAGE_NAME

# Deploy to Cloud Run
echo "Deploying to Cloud Run..."
gcloud run deploy $SERVICE_NAME \
  --image $IMAGE_NAME \
  --platform managed \
  --region $REGION \
  --allow-unauthenticated \
  --memory 512Mi \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod,SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:PORT/checkin_db,SPRING_DATASOURCE_USERNAME=postgres,SPRING_DATASOURCE_PASSWORD=postgres,OPENAI_API_KEY=your_api_key"

echo "Deployment completed!"
