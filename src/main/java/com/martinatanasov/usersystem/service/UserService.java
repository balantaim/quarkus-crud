package com.martinatanasov.usersystem.service;

import com.martinatanasov.usersystem.entity.User;
import com.martinatanasov.usersystem.result.AuthResult;
import com.martinatanasov.usersystem.result.PageUserResult;
import com.martinatanasov.usersystem.result.UserResult;

import java.util.Optional;

public interface UserService {

    PageUserResult findAll(int pageIndex, int pageSize);

    UserResult createUser(String email, String fullName, String password);

    UserResult findByEmail(String email);

    UserResult findByUserId(String userId);

    AuthResult findByEmailAndFullEnabled(String email);

    Optional<User> findByUserIdAndFullEnabled(String userId);

    UserResult changeUserPassword(String userId, String oldPassword, String newPassword);

    UserResult changeUserFullName(String userId, String newFullName);

}
