-- Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER');

-- Users (insertion explicite d'IDs)
INSERT INTO users (id, name, username, password)
VALUES (1, 'Admin User', 'admin', '$2a$12$O9CpoX8MQ.N5EYG3jp3rwuZwtZNWHKmgovekvj/btz1KkqTDCWmF6');

INSERT INTO users (id, name, username, password)
VALUES (2, 'Regular User', 'user', '$2a$12$rq2Nqnls1QOtlE3dHIir9Ohsgw0i/rofquxoTrQn3ttM.ig1GgZ72');

-- Liaison users_roles (role_id, user_id)
INSERT INTO users_roles (role_id, user_id) VALUES (1, 1); -- admin -> ROLE_ADMIN
INSERT INTO users_roles (role_id, user_id) VALUES (2, 2); -- user  -> ROLE_USER

-- Remettre la séquence de roles (déjà présent dans votre script)
ALTER SEQUENCE IF EXISTS roles_seq RESTART WITH 3;

-- Remettre la valeur d'identité pour users.id afin d'éviter conflit avec les insert explicites ci‑dessus
ALTER TABLE users ALTER COLUMN id RESTART WITH 3;
