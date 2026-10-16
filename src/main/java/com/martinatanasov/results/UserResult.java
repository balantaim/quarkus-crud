package com.martinatanasov.results;

import com.martinatanasov.models.response.UserDetailsResponse;

public sealed interface UserResult {

    record Success(UserDetailsResponse userDetailsResponse) implements UserResult {}
    record AlreadyExists() implements  UserResult {}
    record NotFound() implements  UserResult {}

}
