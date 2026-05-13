package com.fathom.category;

import com.fathom.user.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private final UserService userService;

    public CategoryService(CategoryRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public List<Category> getSystem() { return repository.findBySystemDefaultTrueAndActiveTrue(); }
    public List<Category> getByUser(UUID userId) { userService.getEntity(userId); return repository.findByUserIdAndActiveTrue(userId); }
}
