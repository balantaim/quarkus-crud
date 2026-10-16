package com.martinatanasov.results;

import com.martinatanasov.entities.User;

public sealed interface AuthResult {

    record Success(User user) implements AuthResult {}
    record NotFound() implements  AuthResult {}

}