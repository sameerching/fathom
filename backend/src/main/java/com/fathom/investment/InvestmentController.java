package com.fathom.investment;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvestmentController {
    private final InvestmentHoldingService service;

    public InvestmentController(InvestmentHoldingService service) {
        this.service = service;
    }

    @PostMapping("/api/users/{userId}/investment-holdings")
    InvestmentDtos.InvestmentHoldingResponse create(
            @PathVariable UUID userId,
            @Valid @RequestBody InvestmentDtos.CreateInvestmentHoldingRequest request
    ) {
        return service.create(userId, request);
    }

    @GetMapping("/api/users/{userId}/investment-holdings")
    List<InvestmentDtos.InvestmentHoldingResponse> list(@PathVariable UUID userId) {
        return service.list(userId);
    }
}
