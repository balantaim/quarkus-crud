
CREATE INDEX IF NOT EXISTS idx_users_roles_role_id
    ON users_roles (role_id);

CREATE INDEX IF NOT EXISTS idx_roles_authorities_authority_id
    ON roles_authorities (authority_id);