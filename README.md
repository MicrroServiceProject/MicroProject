# ArtBlog Microservices Project

## Overview
This project is a distributed web application built with Spring Boot, Eureka, and Spring Cloud Gateway. It includes a backend microservice for managing blog posts, comments, and notifications, a Eureka Server for service discovery, and an API Gateway for routing requests.

## Setup Instructions
1. **Start the Eureka Server:**
   - Navigate to the `eureka-server` folder.
   - Run `mvn spring-boot:run` or use IntelliJ IDEA to start `EurekaServerApplication`.
   - Access the Eureka dashboard at `http://localhost:8761`.

2. **Start the Backend:**
   - Navigate to the `backend` folder.
   - Ensure MongoDB is running on `localhost:27017`.
   - Run `mvn spring-boot:run` or use IntelliJ IDEA to start `BackendApplication`.

3. **Start the API Gateway:**
   - Navigate to the `api-gateway` folder.
   - Run `mvn spring-boot:run` or use IntelliJ IDEA to start `ApiGatewayApplication`.

4. **Start the Frontend:**
   - Navigate to the `frontend` folder.
   - Run `npm install` and `ng serve` (assuming Angular).
   - Access the frontend at `http://localhost:4200`.

## API Endpoints
- **Get all posts:** `GET /api/posts`
- **Get a post by ID:** `GET /api/posts/{id}`
- **Create a post:** `POST /api/posts`
- **Update a post:** `PUT /api/posts/{id}`
- **Delete a post:** `DELETE /api/posts/{id}`
- **Toggle like:** `POST /api/posts/{id}/like`
- **Toggle favorite:** `POST /api/posts/{id}/favorite`
- **Search posts:** `GET /api/posts/search?query={query}`
- **Add a comment:** `POST /api/comments`
- **Get comments by post ID:** `GET /api/comments/post/{postId}`
- **Get all notifications:** `GET /api/notifications`
- **Add a notification:** `POST /api/notifications`
- **Mark notification as read:** `PUT /api/notifications/{id}/read`
