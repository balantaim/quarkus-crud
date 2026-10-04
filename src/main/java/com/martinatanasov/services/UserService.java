package com.martinatanasov.services;

import com.martinatanasov.models.UserChangePasswordDto;
import com.martinatanasov.models.UserRegisterDto;
import com.martinatanasov.results.PageUserResult;
import com.martinatanasov.results.UserResult;

public interface UserService {

    PageUserResult findAll(int pageIndex, int pageSize);

    UserResult createUser(UserRegisterDto userRegisterDto);

    UserResult findByEmail(String email);

    UserResult findByUserId(String userId);

    UserResult findByEmailAndFullEnabled(String email);

    UserResult findByUserIdAndFullEnabled(String userId);

    UserResult changeUserPassword(String userId, UserChangePasswordDto userChangePasswordDto);

    UserResult changeUserFullName(String userId, String newFullName);

}
