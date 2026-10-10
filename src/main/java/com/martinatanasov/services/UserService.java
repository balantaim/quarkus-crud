package com.martinatanasov.services;

import com.martinatanasov.results.PageUserResult;
import com.martinatanasov.results.UserResult;

public interface UserService {

    PageUserResult findAll(int pageIndex, int pageSize);

    UserResult createUser(String email, String fullName, String password);

    UserResult findByEmail(String email);

    UserResult findByUserId(String userId);

    UserResult findByEmailAndFullEnabled(String email);

    UserResult findByUserIdAndFullEnabled(String userId);

    UserResult changeUserPassword(String userId, String oldPassword, String newPassword);

    UserResult changeUserFullName(String userId, String newFullName);

}
