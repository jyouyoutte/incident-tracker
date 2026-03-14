## Testing – Incident Tracker
Objective
This project implements multiple testing levels to ensure code quality, API reliability, and application stability.

The testing strategy follows the Testing Pyramid:

Unit Tests

Integration Tests

API Tests

End-to-End (E2E) Tests

## 1. Unit Tests
   Unit tests verify the business logic in isolation within the services.

Tools used:

JUnit 5

Mockito

External dependencies (repositories, APIs, etc.) are mocked.

Example: IncidentServiceTest

Objectives:

Verify business logic.

Quickly test functional cases.

Isolate dependencies.

These tests are fast and represent the majority of the test suite.

## 2. Repository Tests (JPA)
   Repository tests verify data persistence using JPA.

Tools used:

Spring Boot Test

@DataJpaTest annotation

H2 in-memory database

Example: IncidentRepositoryTest

Objectives:

Verify JPA queries.

Validate Hibernate mapping.

Test the persistence layer.

## 3. Controller Tests (REST API)
   These tests verify REST endpoints without starting the entire application context.

Tools used:

Spring Boot Test

@WebMvcTest

MockMvc

Example: IncidentControllerTest

Objectives:

Test REST endpoints.

Verify HTTP status codes.

Validate JSON input/output.

## 4. API Integration Tests
   Integration tests verify the application in its entirety: Controller → Service → Repository → Database.

Tools used:

Spring Boot Test

REST-assured

Example: IncidentWorkflowTest

Objectives (Complete Scenarios):

Incident creation.

Incident retrieval.

Updates and deletions.

## 5. Test Database
   Tests utilize an H2 in-memory database.

Configuration: src/test/resources/application-test.properties

Advantages:

High-speed execution.

Complete isolation.

No external dependencies required.

## 6. Running Tests
   To execute all tests, run:
   mvn test

The executed suite includes unit, repository, controller, and end-to-end API tests.

## 7. Test Structure
tests
├── service
│     IncidentServiceTest
│
├── repository
│     IncidentRepositoryTest
│
├── controller
│     IncidentControllerTest
│
└── integration
      IncidentIntegrationTest

## 8. Tech StackToolVersion 

Language : Java 21
Framework : Spring Boot 3.5.10
Testing Framework : JUnit 5
Mocking : Mockito
API Testing : REST-assured
Database :H2 Database

## 9. Applied Best Practices
Test Isolation: Each test runs independently.

In-Memory Database: Ensures a clean state for every run.

Fast & Reproducible: Optimized for CI/CD pipelines.

Layered Coverage: Full coverage across all application layers.

Maintainability: Clean, readable, and well-structured test code.