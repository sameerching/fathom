package com.fathom.user;

import com.fathom.common.DuplicateResourceException;
import com.fathom.common.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AppUserRepository repository;

    public UserService(AppUserRepository repository) {
        this.repository = repository;
    }

    public UserDtos.UserResponse create(UserDtos.CreateUserRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        AppUser user = new AppUser();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setStatus(request.status());

        return toResponse(repository.save(user));
    }

    public UserDtos.UserResponse get(UUID id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public List<UserDtos.UserResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public AppUser getEntity(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserDtos.UserResponse toResponse(AppUser user) {
        return new UserDtos.UserResponse(
                user.getId(), user.getName(), user.getEmail(), user.getStatus(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
