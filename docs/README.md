# Incident Tracker — Quick Guide

## Purpose
Lightweight API to manage application incidents: creation, status tracking, assignment, comments, and search.

---

## Features
- Create an incident
- Change status: OPEN / IN_PROGRESS / RESOLVED / CLOSED

---

## Technical Stack

| Component | Version / Info                                      |
|-----------|----------------------------------------------------|
| Java | 21                                                 |
| Spring Boot | 3.5.10                                           |
| MySQL | 8.0.45                                            |
| Spring Security | Simple JWT (not yet available – future addition) |
| Docker & Docker Compose | 29.2.1                                  |
| Tests | Unit & integration                                |
| CI/CD | GitHub Actions (not yet available – future addition) |

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