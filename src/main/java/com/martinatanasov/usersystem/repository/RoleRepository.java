package com.martinatanasov.usersystem.repository;

import com.martinatanasov.usersystem.entity.Role;
import com.martinatanasov.usersystem.model.RoleName;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class RoleRepository implements PanacheRepositoryBase<Role, Long> {

    public Optional<Role> findByName(RoleName name) {
        return find("name", name).firstResultOptional();
    }
}