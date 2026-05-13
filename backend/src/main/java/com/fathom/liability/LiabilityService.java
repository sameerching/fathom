package com.fathom.liability;

import com.fathom.user.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LiabilityService {
    private final LiabilityRepository repository;
    private final UserService userService;

    public LiabilityService(LiabilityRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public Liability create(UUID userId, Liability liability) {
        userService.getEntity(userId);
        liability.setUserId(userId);
        return repository.save(liability);
    }

    public List<Liability> list(UUID userId) {
        userService.getEntity(userId);
        return repository.findByUserId(userId);
    }
}
