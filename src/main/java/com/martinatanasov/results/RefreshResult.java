package com.martinatanasov.results;

public sealed interface RefreshResult {

    record Success(String accessToken) implements RefreshResult {}
    record Failure() implements RefreshResult {}

}
