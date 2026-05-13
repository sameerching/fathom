package com.fathom.liability;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
public class LiabilityController {
    private final LiabilityService service;

    public LiabilityController(LiabilityService service) {
        this.service = service;
    }

    @PostMapping("/api/users/{userId}/liabilities")
    LiabilityDtos.LiabilityResponse create(
            @PathVariable UUID userId,
            @Valid @RequestBody LiabilityDtos.CreateLiabilityRequest request
    ) {
        return service.create(userId, request);
    }

    @GetMapping("/api/users/{userId}/liabilities")
    List<LiabilityDtos.LiabilityResponse> list(@PathVariable UUID userId) {
        return service.list(userId);
    }
}
