package com.martinatanasov.results;

import com.martinatanasov.models.PageResponse;
import com.martinatanasov.models.UserDetailsDto;

public sealed interface PageUserResult {

    record Success(PageResponse<UserDetailsDto> users) implements PageUserResult {}
    record NotFound() implements PageUserResult {}

}
