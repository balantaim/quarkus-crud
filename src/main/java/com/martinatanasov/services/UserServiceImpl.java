package com.martinatanasov.services;

import com.martinatanasov.entities.Role;
import com.martinatanasov.entities.User;
import com.martinatanasov.mappers.UserMapper;
import com.martinatanasov.models.PageResponse;
import com.martinatanasov.models.RoleName;
import com.martinatanasov.models.UserDetailsDto;
import com.martinatanasov.repositories.RoleRepository;
import com.martinatanasov.repositories.UserRepository;
import com.martinatanasov.results.PageUserResult;
import com.martinatanasov.results.UserResult;
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

    @Inject
    private UserRepository userRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private PasswordService passwordService;
    @Inject
    private UserMapper userMapper;

    @Override
    public PageUserResult findAll(int pageIndex, int pageSize) {
        PanacheQuery<User> query = userRepository.findAllOrdered(pageIndex, pageSize);

        long total = query.count();
        // Ensure that out of range page returns 404
        if (total > 0 && (long) pageIndex * pageSize >= total) {
            return new PageUserResult.NotFound();
        }

        List<UserDetailsDto> content = query.list().stream()
                .map(userMapper::userToUserDataDto)
                .toList();

        return new PageUserResult.Success(new PageResponse<>(
                content, pageIndex, pageSize, total, query.pageCount()
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
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);

        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordService.encode(password));
        user.setEnabled(true);
        user.setRoles(roles);

        // userId is set in @PrePersist
        userRepository.persist(user);
        userRepository.flush();
        Log.infof("User registered: %s", user.getEmail());
        User createdUser = userRepository.findById(user.getId());
        return new UserResult.Success(userMapper.userToUserDataDto(createdUser));
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
    public UserResult changeUserPassword(String userId, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.findActiveByUserId(userId);
        if (user.isEmpty()) {
            return new UserResult.NotFound();
        }
        if (!passwordService.matches(oldPassword, user.get().getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        user.get().setPassword(passwordService.encode(newPassword));
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
