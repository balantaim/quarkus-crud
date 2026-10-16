package com.martinatanasov.results;

import com.martinatanasov.models.response.PageResponse;
import com.martinatanasov.models.response.UserDetailsResponse;

public sealed interface PageUserResult {

    record Success(PageResponse<UserDetailsResponse> users) implements PageUserResult {}
    record NotFound() implements PageUserResult {}

}
