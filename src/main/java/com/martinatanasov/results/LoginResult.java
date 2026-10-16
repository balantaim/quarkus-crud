package com.martinatanasov.results;

public sealed interface LoginResult {

    record Success(String accessToken, String refreshToken, String userId) implements LoginResult {}
    record Failure() implements LoginResult {}

}
