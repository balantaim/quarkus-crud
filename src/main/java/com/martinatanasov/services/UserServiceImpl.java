package com.martinatanasov.services;

import com.martinatanasov.entities.Role;
import com.martinatanasov.entities.User;
import com.martinatanasov.mappers.UserMapper;
import com.martinatanasov.models.*;
import com.martinatanasov.repositories.RoleRepository;
import com.martinatanasov.repositories.UserRepository;
import com.martinatanasov.results.PageUserResult;
import com.martinatanasov.results.UserResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Dependent
class UserServiceImpl implements UserService {

    @Inject
    private UserRepository userRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private UserMapper userMapper;
    @Inject
    private PasswordService passwordService;

    @Override
    public PageUserResult findAll(int pageIndex, int pageSize) {
        PanacheQuery<User> query = userRepository
                .findAll(Sort.by("id"))
                .page(Page.of(pageIndex, pageSize));

        PageResponse<UserDetailsDto> page = new PageResponse<>(
                query.list(), pageIndex, pageSize, query.count(), query.pageCount()
        ).map(user -> {
            Log.debugf("User found: %s", user.getEmail());
            return userMapper.userToUserDataDto(user);
        });
        // Ensure that out of range page returns 404
        if (page.content().isEmpty() && page.totalElements() > 0) {
            return new PageUserResult.NotFound();
        }
        return new PageUserResult.Success(page);
    }

    @Transactional
    @Override
    public UserResult createUser(UserRegisterDto userRegisterDto) {
        if (userRepository.findByEmail(userRegisterDto.email()).isPresent()) {
            Log.infof("User registered: %s", userRegisterDto.email());
            return new UserResult.AlreadyExists();
        }
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);

        User user = new User();
        user.setEmail(userRegisterDto.email());
        user.setFullName(userRegisterDto.fullName());
        user.setPassword(passwordService.encode(userRegisterDto.password()));
        user.setEnabled(true);
        user.setRoles(roles);

        // userId is set in @PrePersist
        userRepository.persist(user);
        Log.infof("User registered: %s", user.getEmail());
        return new UserResult.Success(userMapper.userToUserDataDto(user));
    }

    @Override
    public UserResult findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return new UserResult.NotFound();
        }
        return new UserResult.Success(userMapper.userToUserDataDto(user.get()));
    }

    @Override
    public UserResult findByUserId(String userId) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            return new UserResult.NotFound();
        }
        return new UserResult.Success(userMapper.userToUserDataDto(user.get()));
    }

    @Override
    public UserResult findByEmailAndFullEnabled(String email) {
        Optional<User> activeByEmail = userRepository.findActiveByEmail(email);
        if (activeByEmail.isEmpty()) {
            return new UserResult.NotFound();
        }
        return new UserResult.Success(userMapper.userToUserDataDto(activeByEmail.get()));
    }

    @Override
    public UserResult findByUserIdAndFullEnabled(String userId) {
        Optional<User> activeByUserId = userRepository.findActiveByUserId(userId);
        if (activeByUserId.isEmpty()) {
            return new UserResult.NotFound();
        }
        return new UserResult.Success(userMapper.userToUserDataDto(activeByUserId.get()));
    }

    @Transactional
    @Override
    public UserResult changeUserPassword(String userId, UserChangePasswordDto userChangePasswordDto) {
        Optional<User> user = userRepository.findActiveByUserId(userId);
        if (user.isEmpty()) {
            return new UserResult.NotFound();
        }
        if (!passwordService.matches(userChangePasswordDto.oldPassword(), user.get().getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        user.get().setPassword(passwordService.encode(userChangePasswordDto.newPassword()));
        return new UserResult.Success(userMapper.userToUserDataDto(user.get()));
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
        return new UserResult.Success(userMapper.userToUserDataDto(user.get()));
    }

}
