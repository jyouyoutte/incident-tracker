## Tests – Incident Tracker
Objectif

Ce projet met en œuvre plusieurs niveaux de tests afin de garantir la qualité du code, la fiabilité des API et la stabilité de l'application.

La stratégie de test suit la pyramide de tests :

Tests unitaires

Tests d'intégration

Tests API

Tests end-to-end

## 1. Tests unitaires

Les tests unitaires vérifient la logique métier isolée des services.

Ils utilisent :

JUnit 5

Mockito

Les dépendances externes (repository, API, etc.) sont mockées.

Exemple :

IncidentServiceTest

Objectifs :

vérifier la logique métier

tester rapidement des cas fonctionnels

isoler les dépendances

Ces tests sont rapides et représentent la majorité des tests.

## 2. Tests Repository (JPA)

Les tests repository vérifient la persistance des données avec JPA.

Ils utilisent :

Spring Boot Test

annotation @DataJpaTest

base de données H2 en mémoire

Exemple :

IncidentRepositoryTest

Objectifs :

vérifier les requêtes JPA

valider le mapping Hibernate

tester la couche de persistance

## 3. Tests Controller (API REST)

Ces tests vérifient les endpoints REST sans démarrer toute l'application.

Ils utilisent :

Spring Boot Test

@WebMvcTest

MockMvc

Exemple :

IncidentControllerTest

Objectifs :

tester les endpoints REST

vérifier les statuts HTTP

valider les entrées / sorties JSON

## 4. Tests d'intégration API

Les tests d'intégration vérifient l'application dans sa totalité :

Controller
Service
Repository
Base de données

Ils utilisent :

Spring Boot Test

REST-assured

Exemple :

IncidentWorkflowTest

Ces tests vérifient des scénarios complets :

création d'incident

récupération d'incident

mise à jour

suppression

## 5. Base de données de test

Les tests utilisent une base H2 en mémoire.

Configuration :

src/test/resources/application-test.properties

Avantages :

tests rapides

isolation complète

aucune dépendance externe

## 6. Exécution des tests

Pour exécuter tous les tests :

mvn test

Les tests exécutés incluent :

tests unitaires

tests repository

tests controller

tests API end-to-end

## 7. Structure des tests
   src/test/java/com/incident/tracker

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


## 8. Outils utilisés

Java 21

Spring Boot 3

JUnit 5

Mockito

REST-assured

H2 Database

## 9. Bonnes pratiques appliquées

isolation des tests

base de données en mémoire

tests rapides et reproductibles

couverture des différentes couches de l'application

tests lisibles et maintenables