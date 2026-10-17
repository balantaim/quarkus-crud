package com.martinatanasov.usersystem.result;

import com.martinatanasov.usersystem.entity.User;

public sealed interface AuthResult {

    record Success(User user) implements AuthResult {}
    record NotFound() implements  AuthResult {}

}