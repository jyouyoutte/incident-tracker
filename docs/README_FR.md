# Incident Tracker — Guide rapide

## But
API légère pour gérer des incidents applicatifs : création, suivi de statut, affectation, commentaires et recherche.

## Fonctionnalités
- Créer un incident
- Changer le statut : OPEN / IN_PROGRESS / RESOLVED
- Assigner à un développeur
- Ajouter des commentaires
- Filtrer par statut
- Pagination des listes

## Technique
- Spring Boot 3
- MySQL
- Spring Security (JWT simple)
- Logs structurés
- Docker & Docker Compose
- Gestion d’erreurs centralisée
- Tests unitaires
- CI : GitHub Actions

## Commandes utiles
Positionnez-vous à la racine du projet.

- Démarrer uniquement la base de données :
  docker-compose up -d mysql-db
- Démarrer l'application + DB :
  docker-compose up --build
- Arrêter tout :
  docker-compose down
- Voir les conteneurs en cours :
  docker ps
- Suivre les logs :
  docker-compose logs -f <nom_du_container>
- Accéder à l'API :
  http://localhost:8080/

## Gestion du service MySQL local
- Vérifier l'état : sudo service mysql status  
- Démarrer : sudo service mysql start  
- Arrêter : sudo service mysql stop

## Vérifier la base de données (via Docker — recommandé)
1. Vérifier que le container MySQL tourne :
   docker ps  → repérer incident-tracker-db
2. Se connecter au container :
   docker exec -it incident-tracker-db mysql -u root -p
   (mot de passe : root ou celui défini dans docker-compose)
3. Sélectionner la base :
   USE incident_tracker;
4. Lister les tables :
   SHOW TABLES;
   — Tables attendues : incidents, comments
5. Voir la structure :
   DESCRIBE incident;

## Champs liés à l'enrichissement
- reporterName — nom d'affichage du rapporteur
- reporterEmail — email du rapporteur

Ces champs servent à stocker des informations éventuellement enrichies depuis un User Service externe.

## Instructions / Next steps
- L'enrichissement n'est pas implémenté : prévoir un client UserService (HTTP/gRPC) dans la couche service/adapter.
- Remplir reporterName et reporterEmail avant persistance si vous utilisez l'enrichissement.
- Recompiler le projet après modifications.

## Notes
- assignedDeveloper est pour l'instant un nom ou un identifiant simple.
- Ajouter des tests d'intégration pour couvrir les flux d'enrichissement si implémentés.
