
INSERT INTO authorities (id, name)
VALUES (1, 'CUSTOMER_READ'),
       (2, 'CUSTOMER_WRITE'),
       (3, 'ADMIN_READ'),
       (4, 'ADMIN_WRITE');

INSERT INTO roles (id, name)
VALUES (1, 'CUSTOMER'),
       (2, 'ADMIN');

INSERT INTO roles_authorities (role_id, authority_id)
VALUES (1, 1),
       (1, 2);

INSERT INTO roles_authorities (role_id, authority_id)
VALUES (2, 3),
       (2, 4);

INSERT INTO users (email, full_name, password, user_id, account_non_expired, account_non_locked, credentials_non_expired, enabled)
VALUES ('abv@abv.bg', 'Customer', '$2a$10$bxhbNhLBFY3CTKYdSXbxau1ef9rqO9gydddJBFV5xohrVVk2kEcUu',
        gen_random_uuid(), TRUE, TRUE, TRUE, TRUE),
       ('admin@abv.bg', 'Admin', '$2a$10$bxhbNhLBFY3CTKYdSXbxau1ef9rqO9gydddJBFV5xohrVVk2kEcUu',
        gen_random_uuid(), TRUE, TRUE, TRUE, TRUE);

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1),
       (2, 1),
       (2, 2);