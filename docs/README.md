# Incident Tracker — Quick Guide

## Purpose
Lightweight API to manage application incidents: creation, status tracking, assignment, comments, and search.

---

## Features
- Create an incident
- Change status: OPEN / IN_PROGRESS / RESOLVED / CLOSED

---

## Technical Stack

| Component                 | Version / Info                                         |
|---------------------------|--------------------------------------------------------|
| Java                      | 21                                                     |
| Spring Boot               | 3.5.10                                                 |
| MySQL                     | 8.0.45                                                 |
| Spring Security           | Simple JWT (not yet available – future addition)       |
| Docker & Docker Compose   | 29.2.1                                                 |
| Tests                     | Unit & integration                                     |
| CI/CD                     | GitHub Actions (not yet available – future addition)   |
| Documentation             | OpenAPI / Swagger                                      |

---

## Architecture / DDD
- **Model**: data classes (Incident, Comment, Status)
- **Domain**: DTO objects (IncidentDto)
- **Repository**: persistence interface to MySQL
- **Service / Adapter**: business logic, incident management, potential enrichment from an external User Service
- **Mapper**: conversion between Model and Domain
- **Controller**: exposed REST API
- **Security**: JWT, Spring Security

> Simplified diagram:  
Controller --> Service --> Repository --> MySQL

src
└── main
    └── java
        └── com.incident.tracker
            ├── controller
            ├── service
            ├── repository
            ├── domain
            └── configuration

## Useful Docker Commands
Run these commands from the project root.

Start the database only:
docker-compose up -d mysql-db

Start the application + DB:
docker-compose up --build

Stop everything:
docker-compose down

View running containers:
docker ps

Follow logs:
docker-compose logs -f <service_name>

Access the API:
http://localhost:8080/

Swagger / OpenAPI: http://localhost:8080/swagger-ui/index.html

## Local MySQL Service Management
Check status: sudo service mysql status

Start: sudo service mysql start

Stop: sudo service mysql stop

## Verify the Database (via Docker — recommended)
Check if the MySQL container is running:
docker ps → look for incident-tracker-db

Connect to the container:
docker exec -it incident-tracker-db mysql -u root -p
(Password: root or the one defined in docker-compose)

Select the database:
USE incident_tracker;

List tables:
SHOW TABLES;
— Expected tables: incidents, comments

View structure:
DESCRIBE incident;

## Enrichment-related Fields
reporterName — display name of the reporter

reporterEmail — email of the reporter

These fields are used to store information potentially enriched from an external User Service.

Instructions / Next Steps
Enrichment is not yet implemented: plan for a UserService client (HTTP/gRPC) in the service/adapter layer.

Populate reporterName and reporterEmail before persistence if using enrichment.

Recompile the project after making changes.

## Notes
assignedDeveloper is currently a simple name or identifier.
Add integration tests to cover enrichment flows if implemented.

## Contributing
Create a feature/xyz branch.
Add unit / integration tests.
Run tests: ./mvnw test
Open a PR for review.