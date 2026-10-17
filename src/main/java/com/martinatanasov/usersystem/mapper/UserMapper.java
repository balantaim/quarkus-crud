package com.martinatanasov.usersystem.mapper;

import com.martinatanasov.usersystem.entity.User;
import com.martinatanasov.usersystem.model.response.UserDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface UserMapper {

    UserDetailsResponse userToUserDetails(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    User UserDetailsToUser(UserDetailsResponse userDetailsResponse);

}
