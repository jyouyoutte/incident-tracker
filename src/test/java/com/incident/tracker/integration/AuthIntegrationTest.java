package com.incident.tracker.integration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthIntegrationTest {
    @LocalServerPort
    int port;

    private String token;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @BeforeAll
    void authenticateAdmin() {
        RestAssured.port = port;

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

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    @DisplayName("Login with valid admin credentials returns token and type")
    void loginWithValidCredentialsReturnsToken() {
        RestAssured.given()
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
                .body("token", notNullValue())
                .body("type", equalTo("Bearer"));
    }

    @Test
    @DisplayName("Login with invalid credentials returns 401")
    void loginWithInvalidCredentialsReturns401() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "username": "noone",
                          "password": "bad"
                        }
                        """)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Register a new user then login with it returns a token")
    void registerNewUserThenLoginSucceeds() {
        String username = "intuser" + System.currentTimeMillis();

        // Register new user
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"username\": \"%s\", \"password\": \"pwd\", \"roles\": [\"ROLE_USER\"], \"name\": \"Integration User\"}", username))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(anyOf(is(200), is(201)));

        // Login with the new user
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"username\": \"%s\", \"password\": \"pwd\"}", username))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    @DisplayName("Registering an existing user returns failure")
    void registerExistingUserReturnsFailure() {
        // admin user is seeded in test profile
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "username": "admin",
                          "password": "admin",
                          "roles": ["ROLE_ADMIN"],
                          "name": "Admin User"
                        }
                        """)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(409)
                .body("code", equalTo("USER_ALREADY_EXISTS"))
                .body("message", equalTo("User with username admin already exists"));
    }
}

