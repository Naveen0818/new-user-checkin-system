# User Check-in System

A comprehensive Spring Boot application for managing user check-ins, planned visits, and events within an organization.

## Features

- **User Management**: Registration, authentication, and hierarchical organization structure (User → Manager → Director → Executive)
- **Check-In System**: Track when users arrive at and leave office locations
- **Planned Visits**: Schedule upcoming office visits up to 2 weeks in advance
- **Peer Visibility**: View planned visits of peers under the same manager
- **Event Management**: Managers can organize events and invite team members
- **Location Management**: Assign users to dedicated locations

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT Authentication
- Spring Data JPA
- H2 Database (for development)
- Maven

## API Endpoints

### Authentication

- `POST /auth/register` - Register a new user
- `POST /auth/login` - Authenticate and get JWT token

### User Management

- `GET /users/me` - Get current user details
- `GET /users` - Get all users (requires MANAGER+ role)
- `GET /users/{id}` - Get user by ID
- `GET /users/manager/{managerId}` - Get users by manager
- `PUT /users/{id}/manager/{managerId}` - Assign manager to user

### Check-In Management

- `POST /checkins/location/{locationId}` - Check in at a location
- `PUT /checkins/{checkInId}/checkout` - Check out
- `GET /checkins/active` - Get active check-in
- `GET /checkins/user` - Get current user's check-ins
- `GET /checkins/manager/daterange` - Get check-ins for a manager's team

### Planned Visits

- `POST /visits/location/{locationId}` - Create a planned visit
- `PUT /visits/{visitId}/status/{status}` - Update visit status
- `GET /visits/user/upcoming` - Get user's upcoming visits
- `GET /visits/peers/upcoming` - Get peers' upcoming visits
- `GET /visits/manager/upcoming` - Get team's upcoming visits

### Event Management

- `POST /events/location/{locationId}` - Create an event (MANAGER+ role)
- `POST /events/{eventId}/attendees/{userId}` - Add attendee to event
- `GET /events/organizer` - Get events organized by current user
- `GET /events/attendee` - Get events where current user is an attendee

### Location Management

- `GET /locations` - Get all locations
- `POST /locations` - Create a new location (DIRECTOR+ role)
- `PUT /locations/{id}` - Update a location (DIRECTOR+ role)

## Production Deployment Guide

This guide outlines the steps to deploy the User Check-in System to a production environment.

### Prerequisites

- Java 17 or later
- PostgreSQL 14 or later
- Docker and Docker Compose (for containerized deployment)
- An OpenAI API key

### Deployment Options

#### Option 1: Docker Compose Deployment (Self-hosted)

1. Configure environment variables:
   ```
   cp .env.production.example .env.production
   ```
   
2. Edit `.env.production` with your actual values:
   - Database credentials
   - OpenAI API key
   - JWT secret

3. Run the deployment script:
   ```
   ./deploy.sh
   ```

4. The application will be available at `http://your-server-ip:8080/api`

#### Option 2: AWS Elastic Beanstalk

1. Install the AWS EB CLI
2. Initialize your EB environment:
   ```
   eb init
   ```

3. Create a new environment:
   ```
   eb create checkin-system-prod
   ```

4. Configure environment variables in the EB Console or with:
   ```
   eb setenv SPRING_PROFILES_ACTIVE=prod SPRING_DATASOURCE_URL=jdbc:postgresql://... OPENAI_API_KEY=sk-...
   ```

5. Deploy the application:
   ```
   eb deploy
   ```

#### Option 3: Digital Ocean App Platform

1. Connect your GitHub repository to Digital Ocean
2. Use the provided `.do/app.yaml` as a template
3. Configure your environment variables in the Digital Ocean console
4. Deploy the application

### Database Migration

The application uses Flyway for database migrations. On startup, Flyway will automatically apply any pending migrations.

For a clean installation, the initial schema is created by `V1__init_schema.sql`.

### Monitoring and Maintenance

- The application exposes health and metrics endpoints at `/api/actuator/health` and `/api/actuator/metrics`
- Set up monitoring tools to track application performance
- Regular database backups are recommended

### Security Considerations

- Store sensitive information (API keys, passwords) in environment variables or a secure secrets manager
- Keep your JWT secret secure and rotate it periodically
- Enable HTTPS for all traffic in production
- Implement rate limiting for API endpoints

## Setup and Running

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install` to build the project
4. Run `mvn spring-boot:run` to start the application
5. Access the API at `http://localhost:8080/api`
6. Access the H2 console at `http://localhost:8080/api/h2-console`

## Database

The application uses an H2 in-memory database by default with the following credentials:
- JDBC URL: `jdbc:h2:mem:checkindb`
- Username: `sa`
- Password: `password`

## Sample Users

The application is pre-loaded with sample users in a hierarchical structure:

- **Executive**: username: `executive`, password: `password`
- **Director**: username: `director1`, password: `password`
- **Manager**: username: `manager1`, password: `password`
- **User**: username: `user1`, password: `password`

## Testing with cURL

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"user1","password":"password"}'
```

### Check-in
```bash
curl -X POST http://localhost:8080/api/checkins/location/1 -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Create Planned Visit
```bash
curl -X POST http://localhost:8080/api/visits/location/1 -H "Authorization: Bearer YOUR_TOKEN_HERE" -H "Content-Type: application/json" -d '{"plannedStartTime":"2025-03-10T09:00:00","plannedEndTime":"2025-03-10T17:00:00","purpose":"Regular work day"}'
```

### View Upcoming Visits
```bash
curl -X GET http://localhost:8080/api/visits/user/upcoming -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## API Documentation Tools

### Swagger/OpenAPI UI

The application includes Swagger/OpenAPI documentation, which provides an interactive UI to explore and test the API endpoints:

1. Start the application
2. Navigate to: http://localhost:8080/api/swagger-ui.html
3. Use the UI to explore endpoints, see request/response schemas, and test API calls

### Postman Collection

A comprehensive Postman collection is available in the `postman` directory:

1. Import the following files into Postman:
   - `postman/User_Checkin_System.postman_collection.json`
   - `postman/User_Checkin_System.postman_environment.json`
2. Set the "User Checkin System - Local" environment
3. Use the authentication endpoint to get a token (automatically saved to environment)
4. All other requests will automatically use the authentication token

For detailed information about using the API documentation tools, refer to the [API_DOCUMENTATION.md](API_DOCUMENTATION.md) file.

## GitHub Repository

This project is maintained in a GitHub repository at: https://github.com/naveen0818/naveen0818
