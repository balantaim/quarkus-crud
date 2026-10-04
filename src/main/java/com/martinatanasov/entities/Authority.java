package com.martinatanasov.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.martinatanasov.models.AuthorityName;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authorities")
public class Authority extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "authority_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "authority_id_seq",sequenceName = "authority_id_seq",allocationSize = 1)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private AuthorityName name;

    @Version
    @Column(nullable = false)
    private Long version;

    @JsonIgnore
    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles = new HashSet<>();

    public Authority() {}

    public Authority(AuthorityName name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthorityName getName() {
        return name;
    }

    public void setName(AuthorityName name) {
        this.name = name;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + id +
                ", name=" + name +
                ", version=" + version +
                '}';
    }
}
