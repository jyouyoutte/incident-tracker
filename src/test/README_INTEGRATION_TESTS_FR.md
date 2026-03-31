README - Tests d'intégration

Emplacement :
/src/test/README.md

But
----
Ce fichier documente la stratégie des tests d'intégration présents dans le projet (notamment
`AuthIntegrationTest` et `IncidentIntegrationTest`) et explique comment les exécuter localement.

Contexte & stratégie
---------------------
- Les tests d'intégration démarrent l'application Spring Boot sur un port aléatoire
  (annotation `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`).
- Ils utilisent une base H2 en mémoire configurée via le profile `test` (voir `application-test.yaml`).
- RestAssured est utilisé pour appeler les endpoints HTTP réels de l'application.
- Le flux d'authentification est testé « end-to-end » :
  - on appelle `/api/auth/login` pour récupérer un JWT,
  - on met en place une RequestSpec RestAssured globale contenant l'en-tête
    `Authorization: Bearer <token>` afin d'appeler ensuite les endpoints protégés.
- Les tests sont idempotents : pour créer des users de test, les tests génèrent des usernames uniques
  (ex. `intuser<timestamp>`).

Principales classes de test
---------------------------
- `com.incident.tracker.integration.AuthIntegrationTest` : tests d'intégration pour l'authentification
  (login, enregistrement, cas d'erreur). Ce fichier illustre l'extraction de JWT et
  l'utilisation d'un RequestSpec global.
- `com.incident.tracker.integration.IncidentIntegrationTest` : tests d'API métier (création, lecture,
  mise à jour d'incidents). Montre comment réutiliser le token pour les endpoints protégés.
- `com.incident.tracker.infrastructure.web.AuthControllerTest` : tests unitaires (MockMvc) pour
  `AuthController` — utiles pour des tests rapides sans démarrer tout le contexte.

Comment exécuter les tests localement
------------------------------------
Depuis la racine du projet (où se trouve `mvnw`) :

- Exécuter tous les tests (unitaires + intégration) :

```bash
./mvnw test -f pom.xml
```

- Exécuter uniquement une classe d'intégration (ex. `AuthIntegrationTest`) :

```bash
./mvnw -Dtest=com.incident.tracker.integration.AuthIntegrationTest test -f pom.xml
```

- Exécuter uniquement la classe MockMvc `AuthControllerTest` :

```bash
./mvnw -Dtest=com.incident.tracker.infrastructure.web.AuthControllerTest test -f pom.xml
```

Conseils pour l'exécution dans l'IDE (IntelliJ / Eclipse)
--------------------------------------------------------
- Importez le projet Maven si ce n'est pas déjà fait.
- Ouvrez la classe de test (p.ex. `AuthIntegrationTest`) et lancez la configuration JUnit
  (run/debug). Les tests démarrent un serveur embarqué sur un port aléatoire, la valeur
  est récupérée automatiquement grâce à l'annotation `@LocalServerPort` dans le test.

Dépannage fréquent
------------------
- Erreur 403 (Forbidden) sur des appels protégés :
  - Vérifiez que le test récupère bien le JWT (contrôlez la réponse de `/api/auth/login`).
  - Dans les tests, la RequestSpec RestAssured est initialisée dans un `@BeforeAll` qui place
    l'en-tête `Authorization`. Sans cet en-tête, JwtAuthenticationFilter renverra 403.

- Erreur SQL (ex : "Table ROLES not found") lors du démarrage des tests :
  - Assurez-vous d'utiliser le profile `test` (les tests le font via `@ActiveProfiles("test")`).
  - Si vous avez modifié la façon dont les scripts SQL sont exécutés (schema.sql / data.sql),
    vérifiez l'option `spring.jpa.defer-datasource-initialization` dans `application-test.yaml`.

- Conflits de clés primaires (duplicate key) :
  - Eviter d'insérer des IDs fixes dans `data.sql` pour l'environnement de test. Les tests
    utilisent des inserts sans ID (identité/génération) ou créent des entités via les services
    pour rester idempotents.

Bonnes pratiques
----------------
- Pour tester la sécurité rélle, préférez les tests d'intégration qui récupèrent un vrai JWT
  et l'utilisent dans les appels. Pour les tests unitaires (contrôleurs), utilisez MockMvc et
  des `@MockitoBean` pour stubber les dépendances.
- Essayez d'isoler les tests longs (intégration) et de garder des tests unitaires rapides pour
  un feedback immédiat.

Questions / modifications
------------------------
Si vous voulez que j'ajoute :
- Un script SQL d'initialisation (`schema.sql` + `data.sql`) adapté au profile `test`, ou
- Une cible Maven distincte pour exécuter uniquement les tests d'intégration (ex: configuration
  de failsafe / profiles),
répondez et je l'ajouterai.

