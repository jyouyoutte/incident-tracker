package com.incident.tracker.integration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RolePersistenceIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String token;

    @BeforeAll
    void authenticate() {
        RestAssured.port = port;
        token = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"username\": \"admin\"," +
                        "\"password\": \"admin\"" +
                        "}")
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

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Registering a user persists entry in users_roles join table")
    void registeringUserPersistsUsersRoles() {
        String username = "rpuser" + System.currentTimeMillis();
        // register user with role ROLE_USER
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"username\": \"%s\", \"password\": \"pwd\", \"roles\": [\"ROLE_USER\"], \"name\": \"RP User\"}", username))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(anyOf(is(200), is(201)));

        // find user id
        Long userId = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = ?", new Object[]{username}, Long.class);
        assertThat(userId).isNotNull();

        // find role id
        Long roleId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = ?", new Object[]{"ROLE_USER"}, Long.class);
        assertThat(roleId).isNotNull();

        // check join table
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM users_roles WHERE user_id = ? AND role_id = ?", new Object[]{userId, roleId}, Integer.class);
        assertThat(count).isEqualTo(1);
    }
}

