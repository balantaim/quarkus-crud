package com.martinatanasov.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.martinatanasov.entities.Role;

import java.time.LocalDateTime;
import java.util.Collection;

public record UserDetailsDto(String userId,
                             String email,
                             String fullName,
                             @JsonIgnore Collection<Role> roles,
                             LocalDateTime createdDate) {

}
