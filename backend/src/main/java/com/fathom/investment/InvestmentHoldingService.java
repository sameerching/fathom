package com.fathom.investment;

import com.fathom.user.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class InvestmentHoldingService {
    private final InvestmentHoldingRepository repository;
    private final UserService userService;

    public InvestmentHoldingService(InvestmentHoldingRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public InvestmentHolding create(UUID userId, InvestmentHolding holding) {
        userService.getEntity(userId);
        holding.setUserId(userId);
        return repository.save(holding);
    }

    public List<InvestmentHolding> list(UUID userId) {
        userService.getEntity(userId);
        return repository.findByUserId(userId);
    }
}
