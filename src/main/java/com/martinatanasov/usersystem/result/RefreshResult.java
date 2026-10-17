package com.martinatanasov.usersystem.result;

public sealed interface RefreshResult {

    record Success(String accessToken) implements RefreshResult {}
    record Failure() implements RefreshResult {}

}
