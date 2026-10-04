package com.martinatanasov.results;

import com.martinatanasov.models.UserDetailsDto;

sealed public interface UserResult {

    record Success(UserDetailsDto userDetailsDto) implements  UserResult {}
    record AlreadyExists() implements  UserResult {}
    record NotFound() implements  UserResult {}

}
