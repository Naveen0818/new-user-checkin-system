# User Check-In System

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
