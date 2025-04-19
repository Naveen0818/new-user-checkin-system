#!/bin/bash
set -e

# Load environment variables
if [ -f .env.production ]; then
    export $(cat .env.production | grep -v '#' | sed 's/\r$//' | awk NF | xargs)
fi

# Build the application
echo "Building the application..."
./mvnw clean package -DskipTests

# Build Docker image
echo "Building Docker image..."
docker build -t checkin-app:latest .

# Run the application with Docker Compose
echo "Starting services with Docker Compose..."
docker-compose -f docker-compose.prod.yml up -d

echo "Deployment completed successfully!"
echo "The application is available at: http://localhost:8080/api"
echo "PgAdmin is available at: http://localhost:5050"
