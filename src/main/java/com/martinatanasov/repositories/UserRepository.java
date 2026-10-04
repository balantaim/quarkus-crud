package com.martinatanasov.repositories;

import com.martinatanasov.entities.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;


import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, Long> {

    private static final String ACTIVE = "enabled = true and accountNonExpired = true " +
                    "and credentialsNonExpired = true and accountNonLocked = true";

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<User> findByUserId(String userId) {
        return find("userId", userId).firstResultOptional();
    }

    public Optional<User> findByEmailAndEnabledTrue(String email) {
        return find("email = ?1 and enabled = true", email).firstResultOptional();
    }

    public Optional<User> findActiveByEmail(String email) {
        return find("email = ?1 and " + ACTIVE, email).firstResultOptional();
    }

    public Optional<User> findActiveByUserId(String userId) {
        return find("userId = ?1 and " + ACTIVE, userId).firstResultOptional();
    }

    public List<User> findAllPaged(int pageIndex, int pageSize) {
        return findAll().page(Page.of(pageIndex, pageSize)).list();
    }

}
