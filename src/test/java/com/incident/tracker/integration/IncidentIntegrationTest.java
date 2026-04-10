package com.incident.tracker.integration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class IncidentIntegrationTest {
    @LocalServerPort
    int port;

    private static int incidentId;
    private String token;

    @BeforeAll
    void authenticate() {
        RestAssured.port = port;

        // 🔐 Call real login
        token = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username": "admin",
                            "password": "admin"
                        }
                        """)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        System.out.println("JWT Token = " + token);

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .setContentType(ContentType.JSON)
                .build();
    }

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
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue())
                .extract()
                .path("id");
        System.out.println("incidentId saved = " + incidentId);
    }

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
                .body("title", equalTo("Bug login"))
                .body("updatedAt", notNullValue());
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
                .body("status", equalTo("IN_PROGRESS"));
    }
}
