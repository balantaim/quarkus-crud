package com.martinatanasov.repositories;

import com.martinatanasov.entities.Role;
import com.martinatanasov.models.RoleName;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class RoleRepository implements PanacheRepositoryBase<Role, Long> {

    public Optional<Role> findByName(RoleName name) {
        return find("name", name).firstResultOptional();
    }
}