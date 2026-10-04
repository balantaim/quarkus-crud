package com.martinatanasov.mappers;

import com.martinatanasov.entities.User;
import com.martinatanasov.models.UserDetailsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface UserMapper {

    UserDetailsDto userToUserDataDto(User user);

    User UserDataDtoToUser(UserDetailsDto userDetailsDto);

}
