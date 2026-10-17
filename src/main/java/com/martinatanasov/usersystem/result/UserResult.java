package com.martinatanasov.usersystem.result;

import com.martinatanasov.usersystem.model.response.UserDetailsResponse;

public sealed interface UserResult {

    record Success(UserDetailsResponse userDetailsResponse) implements UserResult {}
    record AlreadyExists() implements  UserResult {}
    record NotFound() implements  UserResult {}

}
