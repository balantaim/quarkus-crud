package com.martinatanasov.usersystem.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshTokenResponse(@JsonProperty("access_token") String accessToken) {

}
