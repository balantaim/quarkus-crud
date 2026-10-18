package com.martinatanasov.usersystem.service;

import com.martinatanasov.usersystem.entity.Role;
import com.martinatanasov.usersystem.entity.RoleName;
import com.martinatanasov.usersystem.entity.User;
import com.martinatanasov.usersystem.mapper.UserMapper;
import com.martinatanasov.usersystem.model.response.PageResponse;
import com.martinatanasov.usersystem.model.response.UserDetailsResponse;
import com.martinatanasov.usersystem.repository.RoleRepository;
import com.martinatanasov.usersystem.repository.UserRepository;
import com.martinatanasov.usersystem.result.AuthResult;
import com.martinatanasov.usersystem.result.PageUserResult;
import com.martinatanasov.usersystem.result.UserResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;
    private final UserMapper userMapper;

    @Inject
    UserServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordService passwordService,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordService = passwordService;
        this.userMapper = userMapper;
    }


    @Override
    public PageUserResult findAll(int pageIndex, int pageSize) {
        PanacheQuery<User> query = userRepository.findAllOrdered(pageIndex, pageSize);

        long total = query.count();
        // Ensure that out of range page returns 404
        if (total > 0 && (long) pageIndex * pageSize >= total) {
            return new PageUserResult.NotFound();
        }

        List<UserDetailsResponse> users = query.list()
                .stream()
                .map(userMapper::userToUserDetails)
                .toList();

        return new PageUserResult.Success(new PageResponse<>(
                users, pageIndex, pageSize, total, query.pageCount()
        ));
    }

    @Transactional
    @Override
    public UserResult createUser(String email, String fullName, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            Log.infof("User registered: %s", email);
            return new UserResult.AlreadyExists();
        }
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("Role CUSTOMER not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);

        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordService.encode(password));
        // May be set to false if we require email verification
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setRoles(roles);

        // userId is set in @PrePersist
        userRepository.persist(user);
        userRepository.flush();
        Log.infof("User registered: %s", user.getEmail());
        User createdUser = userRepository.findById(user.getId());
        return new UserResult.Success(userMapper.userToUserDetails(createdUser));
    }

    @Override
    public UserResult findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return new UserResult.NotFound();
        }
        return new UserResult.Success(userMapper.userToUserDetails(user.get()));
    }

    @Override
    public UserResult findByUserId(String userId) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            return new UserResult.NotFound();
        }
        return new UserResult.Success(userMapper.userToUserDetails(user.get()));
    }

    @Override
    public AuthResult findByEmailAndFullEnabled(String email) {
        Optional<User> activeByEmail = userRepository.findActiveByEmail(email);
        if (activeByEmail.isEmpty()) {
            return new AuthResult.NotFound();
        }
        return new AuthResult.Success(activeByEmail.get());
    }

    @Override
    public Optional<User> findByUserIdAndFullEnabled(String userId) {
        return userRepository.findActiveByUserId(userId);
    }

    @Transactional
    @Override
    public UserResult changeUserPassword(String userId, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.findActiveByUserId(userId);
        if (user.isEmpty()) {
            return new UserResult.NotFound();
        }
        if (!passwordService.matches(oldPassword, user.get().getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        user.get().setPassword(passwordService.encode(newPassword));
        return new UserResult.Success(userMapper.userToUserDetails(user.get()));
    }

    @Transactional
    @Override
    public UserResult changeUserFullName(String userId, String newFullName) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            return new UserResult.NotFound();
        }
        user.get().setFullName(newFullName);
        userRepository.persist(user.get());
        return new UserResult.Success(userMapper.userToUserDetails(user.get()));
    }

}
