package com.martinatanasov.usersystem.repository;

import com.martinatanasov.usersystem.entity.Authority;
import com.martinatanasov.usersystem.entity.AuthorityName;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AuthorityRepository implements PanacheRepositoryBase<Authority, Long> {

    public Optional<Authority> findByName(AuthorityName name) {
        return find("name", name).firstResultOptional();
    }
}
