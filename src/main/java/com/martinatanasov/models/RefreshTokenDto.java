package com.martinatanasov.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDto(@NotBlank @JsonProperty("refresh_token") String refreshToken) {

}
