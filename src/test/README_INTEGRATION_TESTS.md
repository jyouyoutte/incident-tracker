README - Integration Tests

Location:
/src/test/README_EN.md

Purpose
-------
This file documents the integration test strategy used in the project (notably
`AuthIntegrationTest` and `IncidentIntegrationTest`) and explains how to run them locally.

Context & strategy
------------------
- Integration tests start the Spring Boot application on a random port
  (annotation `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`).
- Tests use an in-memory H2 database configured for the `test` profile (see `application-test.yaml`).
- RestAssured is used to call the application's real HTTP endpoints.
- The authentication flow is tested end-to-end:
  - call `/api/auth/login` to obtain a JWT,
  - set a global RestAssured RequestSpec that adds the `Authorization: Bearer <token>` header
    so protected endpoints can be exercised.
- Tests are idempotent: when creating test users, tests generate unique usernames
  (for example `intuser<timestamp>`).

Main test classes
-----------------
- `com.incident.tracker.integration.AuthIntegrationTest`: integration tests for authentication
  (login, registration, error cases). This test demonstrates JWT extraction and using a global RequestSpec.
- `com.incident.tracker.integration.IncidentIntegrationTest`: business API integration tests (create, read,
  update incidents). Shows how to reuse the token for protected endpoints.
- `com.incident.tracker.infrastructure.web.AuthControllerTest`: unit tests (MockMvc) for the `AuthController` —
  useful for fast controller-level checks without starting the full context.

How to run the tests locally
----------------------------
From the project root (where `mvnw` is located):

- Run all tests (unit + integration):

```bash
./mvnw test -f pom.xml
```

- Run only a single integration test class (e.g. `AuthIntegrationTest`):

```bash
./mvnw -Dtest=com.incident.tracker.integration.AuthIntegrationTest test -f pom.xml
```

- Run only the MockMvc unit test `AuthControllerTest`:

```bash
./mvnw -Dtest=com.incident.tracker.infrastructure.web.AuthControllerTest test -f pom.xml
```

IDE tips (IntelliJ / Eclipse)
---------------------------
- Import the Maven project if not already done.
- Open a test class (for example `AuthIntegrationTest`) and run the JUnit configuration (run/debug).
  Tests will start an embedded server on a random port; the port is injected with `@LocalServerPort` in the test.

Common troubleshooting
----------------------
- 403 (Forbidden) on protected endpoints:
  - Make sure the test actually retrieves the JWT (check `/api/auth/login` response).
  - The integration tests use a `@BeforeAll` to place the `Authorization` header into a global
    RestAssured RequestSpec. Without that header the `JwtAuthenticationFilter` will return 403.

- SQL errors (e.g. "Table ROLES not found") when starting tests:
  - Ensure the `test` profile is active (`@ActiveProfiles("test")` in test classes).
  - If you changed how SQL scripts are executed (schema.sql / data.sql), check
    `spring.jpa.defer-datasource-initialization` in `application-test.yaml`.

- Duplicate primary key errors:
  - Avoid inserting fixed IDs in `data.sql` for the test environment. Prefer inserts without explicit IDs
    (letting identity/sequence generate them) or create entities through services in tests to remain idempotent.

Best practices
--------------
- To test real security behavior, prefer integration tests that obtain a real JWT and use it for calls.
  For controller unit tests use MockMvc and `@MockitoBean` to stub dependencies.
- Keep long-running integration tests separate and rely on fast unit tests for quick feedback.

Next steps (optional)
---------------------
If you want, I can also:
- add a SQL initialization script (`schema.sql` + `data.sql`) tailored to the `test` profile, or
- add a Maven configuration (Failsafe plugin or profile) to separate integration tests from unit tests.

If you want either option, tell me which one and I will implement it.

