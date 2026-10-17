package com.martinatanasov.usersystem.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.martinatanasov.usersystem.entity.Role;

import java.time.LocalDateTime;
import java.util.Collection;

public record UserDetailsResponse(@JsonProperty("userId") String userId,
                                  @JsonProperty("email") String email,
                                  @JsonProperty("fullName") String fullName,
                                  @JsonIgnore Collection<Role> roles,
                                  @JsonProperty("createdDate") LocalDateTime createdDate) {

}
