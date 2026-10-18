package com.martinatanasov.usersystem.repository;

import com.martinatanasov.usersystem.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, Long> {

    private static final String ACTIVE = "enabled = true " +
            "and accountNonExpired = true " +
            "and credentialsNonExpired = true " +
            "and accountNonLocked = true";

    private static final String FETCH_ROLES = "select u from User u left join fetch u.roles where ";

    public Optional<User> findActiveByEmail(String email) {
        return find(FETCH_ROLES + "u.email = ?1 and " + ACTIVE, email).firstResultOptional();
    }

    public Optional<User> findActiveByUserId(String userId) {
        return find(FETCH_ROLES + "u.userId = ?1 and " + ACTIVE, userId).firstResultOptional();
    }

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<User> findByUserId(String userId) {
        return find("userId", userId).firstResultOptional();
    }

    public Optional<User> findByEmailAndEnabledTrue(String email) {
        return find("email = ?1 and enabled = true", email).firstResultOptional();
    }

    public PanacheQuery<User> findAllOrdered(int pageIndex, int pageSize) {
        return findAll(Sort.by("id")).page(Page.of(pageIndex, pageSize));
    }

}
