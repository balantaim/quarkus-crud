package com.martinatanasov.repositories;

import com.martinatanasov.entities.Authority;
import com.martinatanasov.models.AuthorityName;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AuthorityRepository implements PanacheRepositoryBase<Authority, Long> {

    public Optional<Authority> findByName(AuthorityName name) {
        return find("name", name).firstResultOptional();
    }
}
