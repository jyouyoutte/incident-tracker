package com.incident.tracker.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IncidentIntegrationTest {
    @LocalServerPort
    int port;

    private static int incidentId;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Should create an new incident")
    @Order(1)
    void createIncident() {
        incidentId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "title": "Bug login",
                            "description": "Impossible de se connecter via LDAP",
                            "priority": "MODERATE",
                            "status": "OPEN"
                        }
                        """)
                .when()
                .post("/api/incidents")
                .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("id", notNullValue())
                .body("title", equalTo("Bug login"))
                .body("status", equalTo("OPEN"))
                .extract()
                .path("id");
        System.out.println("incidentId saved = " + incidentId);
    }

    // ===========================
    // 2. GET INCIDENT BY ID
    // ===========================
    @Test
    @DisplayName("Should find the last created incident by id")
    @Order(2)
    void getIncidentById() {
        RestAssured.given()
                .when()
                .get("/api/incidents/{id}", incidentId)
                .then()
                .statusCode(200)
                .body("id", equalTo(incidentId))
                .body("title", equalTo("Bug login"));
    }

    @Test
    @DisplayName("Should find all incidents and find the last created incident in the list")
    @Order(3)
    void getAllIncidents() {
        RestAssured.given()
                .when()
                .get("/api/incidents")
                .then()
                .statusCode(200)
                .body("find { it.id == %d }.title", RestAssured.withArgs(incidentId), equalTo("Bug login"));
    }

    @Test
    @DisplayName("Should update the status of the last created incident")
    @Order(4)
    void patchIncident() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "status": "IN_PROGRESS"
                        }
                        """)
                .when()
                .patch("/api/incidents/{id}", incidentId)
                .then()
                .statusCode(200)
                .body("id", equalTo(incidentId))
                .body("status", equalTo("IN_PROGRESS"))
                .body("updatedAt", notNullValue());
    }
}
