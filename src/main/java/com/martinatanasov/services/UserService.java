package com.martinatanasov.services;

import com.martinatanasov.entities.User;
import com.martinatanasov.results.AuthResult;
import com.martinatanasov.results.PageUserResult;
import com.martinatanasov.results.UserResult;

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
