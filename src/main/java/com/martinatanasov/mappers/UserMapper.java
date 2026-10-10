package com.martinatanasov.mappers;

import com.martinatanasov.entities.User;
import com.martinatanasov.models.UserDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface UserMapper {

    UserDetailsDto userToUserDataDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    User UserDataDtoToUser(UserDetailsDto userDetailsDto);

}
