package com.fathom.user;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    UserDtos.UserResponse create(@Valid @RequestBody UserDtos.CreateUserRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    UserDtos.UserResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    List<UserDtos.UserResponse> list() {
        return service.list();
    }
}
