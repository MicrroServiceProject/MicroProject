# Microproject: Course Management System

This project is a Spring Boot–based microservices application for managing courses, instructors, materials, student enrollments, and course analytics. It leverages Eureka for service discovery and an API Gateway (running on port **8093**) for dynamic routing, making it a scalable and modular system suitable for a distributed architecture.

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture & System Design](#architecture--system-design)
- [Module Breakdown](#module-breakdown)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Data Models & Schemas](#data-models--schemas)
- [Configuration](#configuration)
- [Deployment Instructions](#deployment-instructions)
- [Additional Notes](#additional-notes)

## Project Overview
This project provides RESTful APIs to perform operations related to educational courses. Key functionalities include:

- **Course Management:** Create, retrieve, update, and delete courses; assign instructors.
- **Enrollment Management:** Enroll students in courses and list enrollments.
- **Instructor & Student Management:** Create and list instructors and students.
- **Material Management:** Add and list course materials, with support for different material types (PDF, VIDEO, QUIZ).
- **Analytics:** Report enrollment counts per course.
- **Service Discovery & Gateway Routing:** Services are registered with Eureka, and the API Gateway (port **8093**) provides dynamic routing and load balancing.

## Architecture & System Design
The system is built around microservices and leverages the following components:

- **Spring Boot Microservices:** Each domain (courses, enrollments, instructors, students, materials, analytics) can be hosted as an independent service.
- **Eureka Server:** Acts as the service registry. Each service registers with Eureka so that they can be discovered dynamically.
- **API Gateway:** Uses Spring Cloud Gateway on port **8093** to route external requests to the appropriate internal service. It is configured for dynamic routing based on Eureka–registered service instances.
- **In-Memory H2 Database:** Used for rapid development/testing with automatic schema updates.

## Module Breakdown
- **Microproject Application:**  
  Entry point for the course management service. It includes core functionalities for handling business logic for courses, enrollments, instructors, students, and materials.
- **Eureka Server:**  
  Configured to run on port **8761**. It does not register itself and serves purely as a registry.
- **API Gateway:**  
  Configured to run on port **8093**. It uses dynamic routing via Eureka for forwarding requests to the appropriate microservice (using logical service names).

## Project Structure
![image](https://github.com/user-attachments/assets/937f9ed6-e832-4fb9-a193-4650ccf58c67)
## API Endpoints
The following endpoints are provided across the different controllers in the project:

### Analytics
- **GET /api/analytics/enrollments**  
  Retrieve enrollment count data for courses.

### Courses
- **GET /api/courses**  
  Retrieve a list of all courses.
- **GET /api/courses/{id}**  
  Retrieve details of a single course.
- **POST /api/courses**  
  Create a new course.
- **PUT /api/courses/{id}**  
  Update an existing course.
- **DELETE /api/courses/{id}**  
  Delete a course.
- **PUT /api/courses/{courseId}/instructor/{instructorId}**  
  Assign an instructor to a course.
- **PUT /api/courses/instructor/{instructorId}/assign**  
  Assign an instructor to multiple courses.
- **GET /api/courses/search?keyword={keyword}**  
  Search courses by a given keyword.

### Enrollments
- **POST /enrollments?studentId={studentId}&courseId={courseId}**  
  Create a new enrollment.
- **GET /enrollments**  
  Retrieve a list of all enrollments.

### Instructors
- **POST /instructors**  
  Add a new instructor.
- **GET /instructors**  
  Retrieve a list of all instructors.

### Materials
- **POST /materials/course/{courseId}**  
  Add new material to a course.
- **GET /materials**  
  Retrieve a list of all materials.

### Students
- **POST /students**  
  Add a new student.
- **GET /students**  
  Retrieve a list of all students.

## Data Models & Schemas
The core entities and their relationships are as follows:

### Course
- **Attributes:** id, name, description.
- **Relationships:** 
  - Many-to-one with Instructor.
  - One-to-many with Enrollment.
  - One-to-many with Material.

### Enrollment
- **Attributes:** id, enrollmentDate.
- **Relationships:** 
  - Many-to-one with Course.
  - Many-to-one with Student.

### Instructor
- **Attributes:** id, name, email.
- **Relationships:** 
  - One-to-many with Course.

### Material
- **Attributes:** id, name, url.
- **Enum:** MaterialType (e.g., PDF, VIDEO, QUIZ).
- **Relationship:** Many-to-one with Course.

### Student
*(Structure assumed similar to other entities; add attributes as needed)*

### CourseEnrollmentDTO
A data transfer object used to aggregate enrollment counts by course, as returned by the AnalyticsController.

## Configuration
The project leverages Spring configuration files (e.g., `application.properties`) and uses the following key settings:

### Microproject Service (Course Management Service)
- **Server Port:** 8085  
- **Datasource:** H2 in-memory database (for development/testing)  
- **Eureka Settings:**  
  - `eureka.client.service-url.defaultZone` set to `http://localhost:8761/eureka/`  
  - Service registration enabled.

### Eureka Server
- **Application Name:** eureka-server  
- **Server Port:** 8761  
- **Registration:** Disabled for self-registration.

### API Gateway
- **Application Name:** api-gateway  
- **Server Port:** 8093  
- **Discovery:** Dynamic routing with Eureka enabled.
- **Route Example:**  
  - Routes with the ID `microproject-route` route requests from `/microproject/**` to the MICROPROJECT service.

## Deployment Instructions

### Pre-Requisites
- [JDK 11+](https://adoptopenjdk.net/)
- [Maven or Gradle](https://maven.apache.org/) (depending on your build tool)
- [Docker](https://www.docker.com/) (if you plan to run containers)
- [Eureka & Gateway]: Eureka Server and API Gateway source projects/configurations
  1-Run the Microproject Service:
  2-Starting Eureka Server and API Gateway:

###Access Endpoints:

H2 Console: http://localhost:8085/h2-console

Eureka Dashboard: http://localhost:8761

API Gateway: http://localhost:8093

Test API endpoints with Postman or your preferred API client.
##Additional Notes
Service Discovery:
The project is integrated with Eureka for dynamic service discovery. This enables scaling and dynamic routing, especially when used with the API Gateway.

Dynamic Routing:
The API Gateway uses Spring Cloud Gateway’s discovery locator for dynamic routing based on service names.

Logging & Debugging:
Debug-level logs are enabled for Spring Cloud Gateway and related components to ease troubleshooting.



