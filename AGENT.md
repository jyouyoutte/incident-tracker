# AGENTS — Guide rapide pour agents IA (Incident Tracker)

But : aider un agent IA à devenir productif immédiatement (builds, tests, patterns projet, points d'intégration).

1) Vue d'ensemble
- Projet Spring Boot (package racine `com.incident.tracker`) — structure DDD légère :
    - controllers REST : `infrastructure.web` (ex. `AdminController`, `AuthController`, `IncidentController`)
    - services applicatifs : `application.service` / `application.service.impl`
    - persistance / adapters : `infrastructure.persistence` (repositories JPA)
    - sécurité : `infrastructure.security` (provider JWT, filter, config, CustomUserDetailsService)
    - mappers / domain / dto répartis sous `com.incident.tracker.*`
- Entrypoint : `com.incident.tracker.IncidentTrackerApplication`

2) Build / tests / debug (commandes)
- Build & tests (wrapper maven inclus) :
  ./mvnw clean package
  ./mvnw test
- Lancer en local (Docker recommended) :
  docker-compose up --build
- Logs Docker :
  docker-compose logs -f <service_name>
- Swagger UI local : http://localhost:8080/swagger-ui/index.html

3) Conventions projet importantes (non génériques)
- Packages organisés par rôle (infrastructure, application, domain). Chercher `infrastructure` pour les adapters externes et `application` pour la logique métier.
- Endpoints :
    - Auth : `/api/auth/**` (exemptés de sécurité)
    - Admin : `/api/admin/**` (nécessite ROLE_ADMIN)
- Sécurité JWT :
    - Provider : `infrastructure.security.provider.JwtTokenProvider` (annoté `@Component`) — lit `app.jwt.secret` et `app.jwt.expiration-time-ms` via `@Value`.
    - Filtre : `infrastructure.security.filter.JwtAuthenticationFilter` (actuellement `@Component`) — valide le token et remplit le contexte Spring Security.
    - Config : `infrastructure.security.config.SecurityConfig` utilise `addFilterBefore(new JwtAuthenticationFilter(...), UsernamePasswordAuthenticationFilter.class)`.

4) Points d'attention pour les tests unitaires / slices
- `@WebMvcTest(...)` charge un *MVC slice* — il n'inclut pas tous les beans (services, providers, config de sécurité).
    - Si une classe annotée `@Component` a des dépendances non fournies par le slice, le contexte échouera à démarrer (ex : échec trouvé : `JwtTokenProvider` introuvable pour `JwtAuthenticationFilter`).
- Solutions test-specific :
    - Ajouter `@MockBean` pour les dépendances manquantes (ex. `JwtTokenProvider`, `CustomUserDetailsService`) dans les classes de test `@WebMvcTest`.
    - Ou ne pas annoter `JwtAuthenticationFilter` avec `@Component` (le laisser instancier uniquement dans `SecurityConfig`) si vous préférez éviter des beans globaux créés automatiquement.

5) Exemples (références de fichiers)
- Filtre JWT : `src/main/java/com/incident/tracker/infrastructure/security/filter/JwtAuthenticationFilter.java`
- Provider JWT : `src/main/java/com/incident/tracker/infrastructure/security/provider/JwtTokenProvider.java`
- Security config : `src/main/java/com/incident/tracker/infrastructure/security/config/SecurityConfig.java`
- Test slice qui a échoué : `src/test/java/com/incident/tracker/infrastructure/web/AdminControllerTest.java`

6) Correctifs recommandés (rapide)
- Pour corriger l'erreur observée en tests (trace fournie) : dans les tests `@WebMvcTest`, ajouter :
    - `@MockBean JwtTokenProvider` et `@MockBean CustomUserDetailsService`
- Alternative (code) : supprimer `@Component` de `JwtAuthenticationFilter` et gérer l'instanciation uniquement dans `SecurityConfig`.
- Exemple d'ajout dans un test :
```java
@MockBean
private JwtTokenProvider jwtTokenProvider;

@MockBean
private CustomUserDetailsService customUserDetailsService;
