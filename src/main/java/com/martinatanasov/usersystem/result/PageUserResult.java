package com.martinatanasov.usersystem.result;

import com.martinatanasov.usersystem.model.response.PageResponse;
import com.martinatanasov.usersystem.model.response.UserDetailsResponse;

public sealed interface PageUserResult {

    record Success(PageResponse<UserDetailsResponse> users) implements PageUserResult {}
    record NotFound() implements PageUserResult {}

}
