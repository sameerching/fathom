package com.fathom.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public class UserDtos {
    public record CreateUserRequest(@NotBlank String name, @NotBlank @Email String email, @NotNull UserStatus status) {}

    public record UserResponse(UUID id, String name, String email, UserStatus status, Instant createdAt, Instant updatedAt) {}
}
