Je veux implémenter une authentification JWT simple dans un projet Spring Boot.

Contexte :
- Projet backend REST (incident-tracker)
- Pas de base de données (authentification fake pour test)
- Un endpoint /login qui retourne un JWT
- Un endpoint /incidents protégé
- Utiliser Spring Security
- Auth stateless (pas de session)

Exigences :
1. Créer un JwtService pour générer et valider les tokens
2. Créer un filtre JWT (OncePerRequestFilter)
3. Configurer Spring Security pour :
    - autoriser /login
    - protéger les autres endpoints
4. Ajouter un controller avec /login
5. Simuler un utilisateur (username/password hardcodé)

Contraintes :
- Code simple et pédagogique
- Utiliser une clé secrète simple
- Ajouter des commentaires pour comprendre

Génère tout le code nécessaire.