# User Check-in System API Documentation

This document provides information on how to use the API documentation tools set up for this project.

## Swagger/OpenAPI Documentation

Swagger UI provides a visual, interactive documentation for the REST APIs. You can easily test the endpoints directly from the UI.

### Accessing Swagger UI

1. Start the application
2. Navigate to: http://localhost:8080/api/swagger-ui.html

### Features

- Visual documentation of all API endpoints
- Interactive testing of API endpoints
- Authentication support with JWT token
- Schema information for request/response models
- Detailed parameter descriptions

## Postman Collection

A comprehensive Postman collection has been created to help testing the APIs. The collection includes all endpoints with example request bodies and test scripts.

### Importing the Postman Collection and Environment

1. Open Postman
2. Click on "Import" button
3. Browse to `/postman` directory in the project
4. Import both files:
   - `User_Checkin_System.postman_collection.json`
   - `User_Checkin_System.postman_environment.json`

### Setting Up the Environment

1. After importing, select the "User Checkin System - Local" environment from the environment dropdown in the top right corner of Postman
2. The `baseUrl` variable is pre-configured to `http://localhost:8080/api`
3. The `authToken` variable will be automatically populated when you successfully log in

### Using the Collection

1. First, use the "Login" endpoint in the Authentication folder to get a token
2. The token will be automatically saved to the environment variable `authToken`
3. All other requests are configured to use this token for authentication
4. You can now easily test any endpoint in the collection

## API Endpoints Overview

The API is organized into the following categories:

1. **Authentication**
   - Login
   - Register New User

2. **Check-ins**
   - User Check-in at Location
   - User Check-out
   - Get Active Check-in
   - Get User Check-ins
   - Get Check-ins by User ID
   - Get Location Check-ins
   - Get User Check-ins by Date Range
   - Get Manager's Subordinates Check-ins by Date Range

3. **Users**
   - Get Current User Profile
   - Get All Users
   - Get User By ID

4. **Locations**
   - Get All Locations
   - Get Location By ID
   - Create Location

5. **Events**
   - Get All Events
   - Get Event By ID
   - Create Event
   - Update Event Status

6. **Planned Visits**
   - Get All Planned Visits
   - Get Planned Visit By ID
   - Create Planned Visit
   - Update Planned Visit Status

## Authentication

Most endpoints require authentication using a JWT token. The token should be included in the Authorization header as a Bearer token.

Example:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Error Handling

All endpoints follow a consistent error response format:

```json
{
  "timestamp": "2025-03-05T22:40:33.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error message describing the issue",
  "path": "/api/endpoint-path"
}
```
