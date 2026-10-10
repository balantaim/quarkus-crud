
CREATE SEQUENCE IF NOT EXISTS user_id_seq      START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS role_id_seq      START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS authority_id_seq START WITH 100 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS users
(
    id                      BIGSERIAL PRIMARY KEY,
    email                   VARCHAR(255) NOT NULL UNIQUE,
    full_name               VARCHAR(150) NOT NULL,
    password                VARCHAR(60) NOT NULL,
    user_id                 VARCHAR(36)  NOT NULL UNIQUE,
    account_non_expired     BOOLEAN      NOT NULL DEFAULT FALSE,
    account_non_locked      BOOLEAN      NOT NULL DEFAULT FALSE,
    credentials_non_expired BOOLEAN      NOT NULL DEFAULT FALSE,
    enabled                 BOOLEAN      NOT NULL DEFAULT FALSE,
    version                 BIGINT       NOT NULL DEFAULT 0,
    created_date            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS authorities
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(20) NOT NULL UNIQUE,
    version       BIGINT      NOT NULL DEFAULT 0,
    created_date  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(20) NOT NULL UNIQUE,
    version       BIGINT      NOT NULL DEFAULT 0,
    created_date  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS roles_authorities
(
    role_id      BIGINT NOT NULL,
    authority_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, authority_id),
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (authority_id) REFERENCES authorities (id) ON DELETE CASCADE
);
