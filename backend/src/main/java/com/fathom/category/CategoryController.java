package com.fathom.category; import java.time.Instant; import java.util.*; import org.springframework.web.bind.annotation.*;
@RestController public class CategoryController { private final CategoryRepository repo; public CategoryController(CategoryRepository repo){this.repo=repo;} record Res(UUID id,UUID userId,String name,CategoryType categoryType,boolean systemDefault,boolean active,Instant createdAt,Instant updatedAt){}
@GetMapping("/api/categories/system") List<Res> system(){ return repo.findBySystemDefaultTrueAndActiveTrue().stream().map(this::m).toList(); }
@GetMapping("/api/users/{userId}/categories") List<Res> byUser(@PathVariable UUID userId){ return repo.findByUserIdAndActiveTrue(userId).stream().map(this::m).toList(); }
private Res m(Category c){return new Res(c.getId(),c.getUserId(),c.getName(),c.getCategoryType(),c.isSystemDefault(),c.isActive(),c.getCreatedAt(),c.getUpdatedAt());}}
